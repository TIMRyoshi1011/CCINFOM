import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StaffAssignment {
    public static void assignStaff(Scanner scan) {
        Main.header("Setting Staff Assignment");

        Main.header("Available Staff (ACTIVE)");
        StaffRecords.listAllActiveStaff();

        Main.header("Scheduled Theater Shows");
        listScheduledTheaterShows();

        System.out.print("\nEnter Staff ID to assign: ");
        String staffId = scan.nextLine().trim();

        System.out.print("Enter Theater Show ID: ");
        String theaterShowId = scan.nextLine().trim();

        try {
            Connection conn = Main.getConnection();

            if (!isActiveStaff(conn, staffId)) {
                System.out.println("\nError: Staff ID is invalid or not ACTIVE.");
                return;
            }

            if (!isScheduledShow(conn, theaterShowId)) {
                System.out.println("\nError: Theater Show ID is invalid or not SCHEDULED.");
                return;
            }

            if (assignmentExists(conn, staffId, theaterShowId)) {
                System.out.println("\nError: This staff is already assigned to the selected show.");
                return;
            }

            try {
                conn.setAutoCommit(false);
                updateStaffStatus(conn, staffId, "ASSIGNED");
                insertAssignment(conn, staffId, theaterShowId);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

            System.out.println("\n✓ Staff assignment completed successfully!");
            System.out.println("Staff ID " + staffId + " -> Theater Show ID " + theaterShowId);

        } catch (SQLException e) {
            System.out.println("\nError: Unable to complete the staff assignment transaction: " + e.getMessage());
        }
    }

    private static void listScheduledTheaterShows() {
        String query = "SELECT ts.THEATER_SHOW_ID, s.TITLE, ts.START_TIME, ts.END_TIME, ts.SHOW_STATUS "
                + "FROM theater_shows ts JOIN shows s ON ts.SHOW_ID = s.SHOW_ID "
                + "WHERE ts.SHOW_STATUS = 'SCHEDULED' ORDER BY ts.START_TIME";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Theater Show ID: " + rs.getString("THEATER_SHOW_ID"));
                System.out.println("Title: " + rs.getString("TITLE"));
                System.out.println("Time: " + rs.getString("START_TIME") + " - " + rs.getString("END_TIME"));
                System.out.println("Status: " + rs.getString("SHOW_STATUS"));
                Main.subheader();
            }

            if (!found) {
                System.out.println("No scheduled theater shows found.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to list theater shows: " + e.getMessage());
        }
    }

    private static boolean isActiveStaff(Connection conn, String staffId) throws SQLException {
        String query = "SELECT 1 FROM staff WHERE STAFF_ID = ? AND EMPLOYMENT_STATUS = 'ACTIVE'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isScheduledShow(Connection conn, String theaterShowId) throws SQLException {
        String query = "SELECT 1 FROM theater_shows WHERE THEATER_SHOW_ID = ? AND SHOW_STATUS = 'SCHEDULED'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, theaterShowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean assignmentExists(Connection conn, String staffId, String theaterShowId) throws SQLException {
        String query = "SELECT 1 FROM staff_assignment WHERE STAFF_ID = ? AND THEATER_SHOW_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            pstmt.setString(2, theaterShowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void updateStaffStatus(Connection conn, String staffId, String newStatus) throws SQLException {
        String query = "UPDATE staff SET EMPLOYMENT_STATUS = ? WHERE STAFF_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, staffId);
            pstmt.executeUpdate();
        }
    }

    private static void insertAssignment(Connection conn, String staffId, String theaterShowId) throws SQLException {
        String query = "INSERT INTO staff_assignment (STAFF_ID, THEATER_SHOW_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            pstmt.setString(2, theaterShowId);
            pstmt.executeUpdate();
        }
    }
}

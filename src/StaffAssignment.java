import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StaffAssignment {
    public static void assignStaff(Scanner scan) {
        Main.header("SETTING STAFF ASSIGNMENT");

        String staffId = null;
        String theaterShowId = null;

        Connection conn = null;
        try {
            conn = Main.getConnection();

            Main.header("Scheduled Theater Shows");
            String showQuery = "SELECT ts.THEATER_SHOW_ID, s.TITLE, ts.START_TIME, ts.END_TIME, ts.SHOW_STATUS "
                    + "FROM theater_shows ts JOIN shows s ON ts.SHOW_ID = s.SHOW_ID "
                    + "WHERE ts.SHOW_STATUS = 'SCHEDULED' ORDER BY ts.START_TIME";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(showQuery)) {
                if (!rs.next()) {
                    System.out.println("\nNo scheduled theater shows found.");
                    return;
                }

                do {
                    System.out.println("Theater Show ID: " + rs.getString("THEATER_SHOW_ID"));
                    System.out.println("Title: " + rs.getString("TITLE"));
                    System.out.println("Time: " + rs.getString("START_TIME") + " - " + rs.getString("END_TIME"));
                    System.out.println("Status: " + rs.getString("SHOW_STATUS"));
                    Main.subheader();
                } while (rs.next());
            }

            System.out.print("\nEnter Theater Show ID: ");
            theaterShowId = scan.nextLine().trim();

            if (!isScheduledShow(conn, theaterShowId)) {
                System.out.println("\nError: Theater Show ID is invalid or not SCHEDULED.");
                return;
            }

            Main.header("Available Staff for This Show");
            String availableStaffQuery = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, s.POSITION, s.EMPLOYMENT_STATUS, s.SALARY "
                    + "FROM staff s "
                    + "WHERE s.EMPLOYMENT_STATUS = 'ACTIVE' "
                    + "AND s.STAFF_ID NOT IN (SELECT STAFF_ID FROM staff_assignment WHERE THEATER_SHOW_ID = ?) "
                    + "ORDER BY s.LAST_NAME, s.FIRST_NAME";

            try (PreparedStatement ps = conn.prepareStatement(availableStaffQuery)) {
                ps.setString(1, theaterShowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out
                                .println("\nNo available staff for this show. All active staff are already assigned.");
                        return;
                    }

                    do {
                        System.out.println("ID: " + rs.getString("STAFF_ID"));
                        System.out.println("Name: " + rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
                        System.out.println("Position: " + rs.getString("POSITION"));
                        System.out.println("Employment Status: " + rs.getString("EMPLOYMENT_STATUS"));
                        System.out.println("Salary: $" + rs.getInt("SALARY"));
                        Main.subheader();
                    } while (rs.next());
                }
            }

            System.out.print("\nEnter Staff ID to assign: ");
            staffId = scan.nextLine().trim();

            if (!isActiveStaff(conn, staffId)) {
                System.out.println("\nError: Staff ID is invalid or not ACTIVE.");
                return;
            }

            if (assignmentExists(conn, staffId, theaterShowId)) {
                System.out.println("\nError: This staff is already assigned to the selected show.");
                return;
            }

            try {
                conn.setAutoCommit(false);
                insertAssignment(conn, staffId, theaterShowId);
                conn.commit();
            } catch (SQLException e) {
                if (conn != null)
                    conn.rollback();
                throw e;
            } finally {
                if (conn != null)
                    conn.setAutoCommit(true);
            }

            System.out.println("\nStaff assignment completed successfully!");
            System.out.println("Staff ID " + staffId + " assigned to Theater Show ID " + theaterShowId);

        } catch (SQLException e) {
            System.out.println("\nError: Unable to complete the staff assignment transaction: " + e.getMessage());
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

    private static void insertAssignment(Connection conn, String staffId, String theaterShowId) throws SQLException {
        String query = "INSERT INTO staff_assignment (STAFF_ID, THEATER_SHOW_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            pstmt.setString(2, theaterShowId);
            pstmt.executeUpdate();
        }
    }
}

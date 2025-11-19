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
            String showQuery = "SELECT ts.THEATER_SHOW_ID, s.TITLE, ts.START_TIME, ts.END_TIME, tr.RESERVED_DATE "
                    + "FROM theater_shows ts "
                    + "JOIN shows s ON ts.SHOW_ID = s.SHOW_ID "
                    + "JOIN theater_reservation tr ON ts.THEATER_RESERVATION_ID = tr.THEATER_RESERVATION_ID "
                    + "WHERE ts.SHOW_STATUS = 'SCHEDULED' ORDER BY tr.RESERVED_DATE, ts.START_TIME";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(showQuery)) {
                if (!rs.next()) {
                    System.out.println("\nNo scheduled theater shows found.");
                    return;
                }

                do {
                    String showId = rs.getString("THEATER_SHOW_ID");
                    String title = rs.getString("TITLE");
                    String startTime = rs.getString("START_TIME");
                    String endTime = rs.getString("END_TIME");
                    String reservedDate = rs.getString("RESERVED_DATE");

                    // Ensure printed lines never exceed 55 chars: ID field (12) + space + content
                    // (42)
                    String displayStart = (startTime != null && startTime.length() >= 5) ? startTime.substring(0, 5)
                            : (startTime == null ? "" : startTime);
                    String displayEnd = (endTime != null && endTime.length() >= 5) ? endTime.substring(0, 5)
                            : (endTime == null ? "" : endTime);

                    // Print show title as-is (no wrapping/truncation)
                    System.out.printf("%-12s %s%n", showId, title);
                    System.out.printf("             %s %s - %s%n", reservedDate, displayStart, displayEnd);
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
            String availableStaffQuery = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, s.POSITION "
                    + "FROM staff s "
                    + "WHERE s.EMPLOYMENT_STATUS = 'ACTIVE' "
                    + "AND s.STAFF_ID NOT IN (SELECT STAFF_ID FROM staff_assignment WHERE THEATER_SHOW_ID = ?) "
                    + "ORDER BY s.LAST_NAME, s.FIRST_NAME";

            try (PreparedStatement ps = conn.prepareStatement(availableStaffQuery)) {
                ps.setString(1, theaterShowId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out
                                .println("\nNo available staff for this show. All active staff are already assigned.\n");
                        return;
                    }

                    do {
                        String staffIdVal = rs.getString("STAFF_ID");
                        String name = rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME");
                        String position = rs.getString("POSITION");

                        System.out.printf("%-12s %s%n", staffIdVal, name);
                        System.out.printf("             %s%n", position);
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
            System.out.println(); 
            String successMessage = "Staff assignment completed successfully!";
            String assignmentMessage = "ST" + staffId + " assigned to " + theaterShowId;

            if (assignmentMessage.length() > 51) {
                assignmentMessage = assignmentMessage.substring(0, 48) + "...";
            }

            int boxWidth = 50;
            int paddingSuccess = (boxWidth - successMessage.length() - 2) / 2;
            int paddingAssignment = (boxWidth - assignmentMessage.length() - 2) / 2;

            System.out.println("-".repeat(boxWidth));
            System.out.println("| " + " ".repeat(paddingSuccess) + successMessage
                    + " ".repeat(boxWidth - successMessage.length() - paddingSuccess - 3) + "|");
            System.out.println("-".repeat(boxWidth));
            System.out.println("| " + " ".repeat(paddingAssignment) + assignmentMessage
                    + " ".repeat(boxWidth - assignmentMessage.length() - paddingAssignment - 3) + "|");
            System.out.println("-".repeat(boxWidth));
            System.out.println(); 

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

import java.sql.*;
import java.util.Scanner;

public class StaffRecords {

    // Add new staff
    public static void enterStaffDetails(Scanner scan) {
        System.out.println("");
        System.out.print("Enter First Name: ");
        String firstName = scan.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scan.nextLine();

        System.out.print("Enter Position: ");
        String position = scan.nextLine();

        System.out.print("Enter Employment Status (ACTIVE/INACTIVE): ");
        String status = scan.nextLine();

        System.out.print("Enter Salary: ");
        int salary = Integer.parseInt(scan.nextLine());

        addStaffToDB(firstName, lastName, position, status, salary);
    }

    public static void addStaffToDB(String firstName, String lastName, String position, String status,
            int salary) {
        String query = "INSERT INTO staff (FIRST_NAME, LAST_NAME, POSITION, EMPLOYMENT_STATUS, SALARY) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, position);
            pstmt.setString(4, status);
            pstmt.setInt(5, salary);
            pstmt.executeUpdate();

            System.out.println("\nStaff member added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update staff details
    public static void updateStaffDetails(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Staff ID to update: ");
        String staffId = scan.nextLine();

        // Check if Staff ID exists
        String checkQuery = "SELECT 1 FROM staff WHERE STAFF_ID = ?";
        try (Connection conn = Main.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, staffId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nError: Staff ID not found.");
                    return; 
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError checking Staff ID: " + e.getMessage());
            return;
        }

        System.out.print("Enter New First Name (or press Enter to skip): ");
        String firstName = scan.nextLine();

        System.out.print("Enter New Last Name (or press Enter to skip): ");
        String lastName = scan.nextLine();

        System.out.print("Enter New Position (or press Enter to skip): ");
        String position = scan.nextLine();

        System.out.print("Enter New Employment Status (or press Enter to skip): ");
        String status = scan.nextLine();

        System.out.print("Enter New Salary (or press Enter to skip): ");
        String salaryInput = scan.nextLine();
        Integer salary = salaryInput.isEmpty() ? null : Integer.parseInt(salaryInput);

        updateStaffInDB(staffId, firstName, lastName, position, status, salary);
    }

    public static void updateStaffInDB(String staffId, String firstName, String lastName, String position,
            String status,
            Integer salary) {
        StringBuilder query = new StringBuilder("UPDATE staff SET ");
        boolean hasUpdate = false;

        try {
            Connection conn = Main.getConnection();

            if (!firstName.isEmpty()) {
                query.append("FIRST_NAME = ?, ");
                hasUpdate = true;
            }
            if (!lastName.isEmpty()) {
                query.append("LAST_NAME = ?, ");
                hasUpdate = true;
            }
            if (!position.isEmpty()) {
                query.append("POSITION = ?, ");
                hasUpdate = true;
            }
            if (!status.isEmpty()) {
                query.append("EMPLOYMENT_STATUS = ?, ");
                hasUpdate = true;
            }
            if (salary != null) {
                query.append("SALARY = ?, ");
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No updates provided.");
                return;
            }

            query.setLength(query.length() - 2);
            query.append(" WHERE STAFF_ID = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!firstName.isEmpty())
                pstmt.setString(paramIndex++, firstName);
            if (!lastName.isEmpty())
                pstmt.setString(paramIndex++, lastName);
            if (!position.isEmpty())
                pstmt.setString(paramIndex++, position);
            if (!status.isEmpty())
                pstmt.setString(paramIndex++, status);
            if (salary != null)
                pstmt.setInt(paramIndex++, salary);

            pstmt.setString(paramIndex, staffId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nStaff details updated successfully!");
            } else {
                System.out.println("\nStaff ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete staff record
    public static void deleteStaff(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Staff ID to delete: ");
        String staffId = scan.nextLine();

        // Check if Staff ID exists
        String checkQuery = "SELECT 1 FROM staff WHERE STAFF_ID = ?";
        try (Connection conn = Main.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, staffId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nError: Staff ID not found.");
                    return; // Exit the feature if Staff ID does not exist
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError checking Staff ID: " + e.getMessage());
            return;
        }

        String query = "DELETE FROM staff WHERE STAFF_ID = ?";
        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, staffId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nStaff member deleted successfully!");
            } else {
                System.out.println("\nStaff ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View staff by position
    public static void viewStaffByPosition(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Position to search: ");
        String position = scan.nextLine();

        String query = "SELECT * FROM staff WHERE POSITION = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff by Position: " + position);
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayStaffRecord(rs);
            }

            if (!found) {
                System.out.println("No staff found for position: " + position);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View staff by shift
    public static void viewStaffByShift(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Employment Status to search (Active/Inactive): ");
        String status = scan.nextLine();

        String query = "SELECT * FROM staff WHERE EMPLOYMENT_STATUS = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff by Employment Status: " + status);
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayStaffRecord(rs);
            }

            if (!found) {
                System.out.println("No staff found for status: " + status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all staff salaries
    public static void viewAllStaffSalaries() {
        String query = "SELECT * FROM staff ORDER BY SALARY DESC";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            Main.header("All Staff Salaries");
            while (rs.next()) {
                displayStaffRecord(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List all staff members
    public static void listAllStaff() {
        String query = "SELECT * FROM staff";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            Main.header("All Staff Members");
            while (rs.next()) {
                displayStaffRecord(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List all active staff members
    public static void listAllActiveStaff() {
        String query = "SELECT * FROM staff WHERE EMPLOYMENT_STATUS = 'ACTIVE'";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            Main.header("All Active Staff Members");
            while (rs.next()) {
                displayStaffRecord(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View an individual staff member with their shift in the show & theater record
    public static void viewStaffWithShowDetails(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Staff ID: ");
        String staffId = scan.nextLine();

        // Check if Staff ID exists
        String checkQuery = "SELECT 1 FROM staff WHERE STAFF_ID = ?";
        try (Connection conn = Main.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, staffId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nError: Staff ID not found.\n");
                    return; // Exit the feature if Staff ID does not exist
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError checking Staff ID: " + e.getMessage());
            return;
        }

        String query = "SELECT s.*, sh.TITLE as SHOW_TITLE, tr.THEATER_ID " +
                "FROM staff s " +
                "LEFT JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "LEFT JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "LEFT JOIN shows sh ON ts.SHOW_ID = sh.SHOW_ID " +
                "LEFT JOIN theater_reservation tr ON ts.THEATER_RESERVATION_ID = tr.THEATER_RESERVATION_ID " +
                "WHERE s.STAFF_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, staffId);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff Details with Shows");
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    // Display staff details once
                    displayStaffRecord(rs);
                    found = true;
                }

                String showTitle = rs.getString("SHOW_TITLE");
                if (showTitle != null) {
                    String theaterId = rs.getString("THEATER_ID");
                    System.out.println("  Assigned to Show: " + showTitle +
                            (theaterId != null ? " | Theater ID: " + theaterId : ""));
                }
            }

            if (!found) {
                System.out.println("Staff ID not found.\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all staff for a specific show
    public static void viewStaffForShow(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Show ID: ");
        String showId = scan.nextLine();

        String query = "SELECT s.* " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "WHERE ts.SHOW_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, showId);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff for Show ID: " + showId);
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayStaffRecord(rs);
            }

            if (!found) {
                System.out.println("No staff assigned to this show or show not found.\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to display staff record
    private static void displayStaffRecord(ResultSet rs) throws SQLException {
        String id = rs.getString("STAFF_ID");
        String firstName = rs.getString("FIRST_NAME");
        String lastName = rs.getString("LAST_NAME");
        String position = rs.getString("POSITION");
        String status = rs.getString("EMPLOYMENT_STATUS");
        int salary = rs.getInt("SALARY");

        System.out.println("ID: " + id);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Position: " + position);
        System.out.println("Employment Status: " + status);
        System.out.println("Salary: ₱" + salary);
        Main.subheader();
    }

    // Staff management menu
    public static void staffMenu(Scanner scan) {
        int option = 0;

        do {
            System.out.println("\n=================== Staff Management ===================");
            System.out.println("1. Add New Staff");
            System.out.println("2. Update Staff Details");
            System.out.println("3. Delete Staff Record");
            System.out.println("4. View Staff by Position");
            System.out.println("5. View Staff by Employment Status");
            System.out.println("6. View All Staff Salaries");
            System.out.println("7. List All Staff Members");
            System.out.println("8. List All Active Staff Members");
            System.out.println("9. View Staff with Show Details");
            System.out.println("10. View All Staff for a Specific Show");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nChoose an option: ");
            option = Integer.parseInt(scan.nextLine());

            switch (option) {
                case 1:
                    enterStaffDetails(scan);
                    break;
                case 2:
                    updateStaffDetails(scan);
                    break;
                case 3:
                    deleteStaff(scan);
                    break;
                case 4:
                    viewStaffByPosition(scan);
                    break;
                case 5:
                    viewStaffByShift(scan);
                    break;
                case 6:
                    viewAllStaffSalaries();
                    break;
                case 7:
                    listAllStaff();
                    break;
                case 8:
                    listAllActiveStaff();
                    break;
                case 9:
                    viewStaffWithShowDetails(scan);
                    break;
                case 10:
                    viewStaffForShow(scan);
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 0);
    }
}

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
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

    private static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int totalPadding = width - text.length();
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + text + " ".repeat(right);
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

        // show all available positions
        System.out.println("Available positions:");
        String allPositionsQuery = "SELECT DISTINCT POSITION FROM staff WHERE POSITION IS NOT NULL AND POSITION <> '' ORDER BY POSITION";
        List<String> allPositions = new ArrayList<>();
        try (Connection conn = Main.getConnection(); Statement stmt = conn.createStatement(); ResultSet rsAll = stmt.executeQuery(allPositionsQuery)) {
            while (rsAll.next()) {
                String pos = rsAll.getString("POSITION");
                if (pos != null && !pos.isEmpty()) {
                    allPositions.add(pos);
                    System.out.println("  - " + pos);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (allPositions.isEmpty()) {
            System.out.println("No positions found in the system.");
            return;
        }

        System.out.println();
        System.out.print("Enter position: ");
        String input = scan.nextLine().trim();

        String positionsQuery = "SELECT DISTINCT POSITION FROM staff WHERE POSITION IS NOT NULL AND POSITION <> '' AND POSITION LIKE ? ORDER BY POSITION";
        List<String> positions = new ArrayList<>();
        try (Connection conn = Main.getConnection(); PreparedStatement ps = conn.prepareStatement(positionsQuery)) {
            ps.setString(1, "%" + input + "%");
            try (ResultSet rsPos = ps.executeQuery()) {
                while (rsPos.next()) {
                    String pos = rsPos.getString("POSITION");
                    if (pos != null && !pos.isEmpty()) positions.add(pos);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (positions.isEmpty()) {
            System.out.println("No matching positions found for: '" + input + "'");
            return;
        }

        // multiple match so show both and then ask user to pick
        String position;
        if (positions.size() == 1) {
            position = positions.get(0);
            System.out.println("Using position: " + position);
        } else {
            System.out.println("Multiple positions found — select one:");
            for (int i = 0; i < positions.size(); i++) {
                System.out.printf("%2d. %s%n", i + 1, positions.get(i));
            }
            System.out.println(" 0. Cancel");

            int choice = -1;
            while (choice < 0) {
                System.out.print("Choose a number (0 to cancel): ");
                String sel = scan.nextLine();
                try {
                    choice = Integer.parseInt(sel);
                    if (choice < 0 || choice > positions.size()) {
                        System.out.println("Invalid selection. Try again.");
                        choice = -1;
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Please enter a valid number.");
                }
            }

            if (choice == 0) {
                System.out.println("Cancelled.");
                return;
            }

            position = positions.get(choice - 1);
        }

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
                System.out.println("\nNo staff found for position: " + position);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View staff by shift
    public static void viewStaffByShift(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Employment Status to search (ACTIVE/INACTIVE): ");
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
                System.out.println("\nNo staff found for status: " + status);
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
            System.out.printf("%-12s %-25s %8s%n", "Staff ID", "Name", "Salary");
            Main.subheader();
            while (rs.next()) {
                String id = rs.getString("STAFF_ID");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                int salary = rs.getInt("SALARY");

                String name = (firstName == null ? "" : firstName) + (lastName == null || lastName.isEmpty() ? "" : " " + lastName);
                String currency = "₱" + salary;
                System.out.printf("%-12s %-25s %8s%n", id, name, currency);
                Main.subheader();
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
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("\nError checking Staff ID: " + e.getMessage());
            return;
        }

        String query = "SELECT s.*, sh.TITLE as SHOW_TITLE, ts.THEATER_ID, ts.RESERVATION_DATE, ts.START_TIME, ts.END_TIME " +
                "FROM staff s " +
                "LEFT JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "LEFT JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "LEFT JOIN shows sh ON ts.SHOW_ID = sh.SHOW_ID " +
                "WHERE s.STAFF_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, staffId);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff Details with Shows");
            boolean found = false;
            boolean hasShows = false;
            
            while (rs.next()) {
                if (!found) {
                    // Display staff details once
                    displayStaffRecord(rs); 
                    found = true;
                }

                String showTitle = rs.getString("SHOW_TITLE");
                if (showTitle != null) {
                    hasShows = true;
                    String theaterId = rs.getString("THEATER_ID");
                    String reservationDate = rs.getString("RESERVATION_DATE");
                    String startTime = rs.getString("START_TIME");
                    String endTime = rs.getString("END_TIME");
                    
                    String displayStart = (startTime != null && startTime.length() >= 5) ? startTime.substring(0, 5) : "";
                    String displayEnd = (endTime != null && endTime.length() >= 5) ? endTime.substring(0, 5) : "";
                    
                                System.out.println(centerText(showTitle, 50));
                                StringBuilder detailsBuilder = new StringBuilder();
                                if (theaterId != null && !theaterId.isEmpty()) {
                                    detailsBuilder.append("Theater: ").append(theaterId);
                                }
                                if (reservationDate != null && !reservationDate.isEmpty()) {
                                    if (detailsBuilder.length() > 0) detailsBuilder.append(" | ");
                                    detailsBuilder.append(reservationDate);
                                }
                                String timeRange = "";
                                if (!displayStart.isEmpty() || !displayEnd.isEmpty()) {
                                    timeRange = displayStart.isEmpty() ? displayEnd : (displayStart + (displayEnd.isEmpty() ? "" : "-" + displayEnd));
                                    if (timeRange != null && !timeRange.isEmpty()) {
                                        if (detailsBuilder.length() > 0) detailsBuilder.append(" ");
                                        detailsBuilder.append(timeRange);
                                    }
                                }

                                String details = detailsBuilder.toString();
                                if (!details.isEmpty()) {
                                    System.out.println(centerText(details, 50));
                                }
                                Main.subheader();
                }
            }

            if (!found) {
                System.out.println("Staff ID not found.");
            } else if (!hasShows) {
                System.out.println("No show assignments found for this staff member.\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all staff for a specific show
    public static void viewStaffForShow(Scanner scan) {
        System.out.println("");

        // Fetch all distinct shows to display
        String showsQuery = "SELECT SHOW_ID, TITLE FROM shows ORDER BY TITLE";
        List<String> showIds = new ArrayList<>();
        List<String> showTitles = new ArrayList<>();
        try (Connection conn = Main.getConnection(); Statement stmt = conn.createStatement(); ResultSet rsShows = stmt.executeQuery(showsQuery)) {
            while (rsShows.next()) {
                showIds.add(rsShows.getString("SHOW_ID"));
                showTitles.add(rsShows.getString("TITLE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (showTitles.isEmpty()) {
            System.out.println("No shows found in the system.");
            return;
        }

        // Display shows for user to choose
        System.out.println("Select a show:");
        for (int i = 0; i < showTitles.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, showTitles.get(i));
        }
        System.out.println(" 0. Cancel");

        int choice = -1;
        while (choice < 0) {
            System.out.print("Choose a number (0 to cancel): ");
            String input = scan.nextLine();
            try {
                choice = Integer.parseInt(input);
                if (choice < 0 || choice > showTitles.size()) {
                    System.out.println("Invalid selection. Try again.");
                    choice = -1;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a valid number.");
            }
        }

        if (choice == 0) {
            System.out.println("Going back to menu...");
            return;
        }

        String showId = showIds.get(choice - 1);
        String showTitle = showTitles.get(choice - 1);
        
        // get all the dates and then the times
        String schedulesQuery = "SELECT THEATER_SHOW_ID, THEATER_ID, RESERVATION_DATE, START_TIME, END_TIME " +
                                "FROM theater_shows WHERE SHOW_ID = ? ORDER BY RESERVATION_DATE, START_TIME";
        List<String> theaterShowIds = new ArrayList<>();
        List<String> scheduleDescriptions = new ArrayList<>();
        try (Connection conn = Main.getConnection(); PreparedStatement ps = conn.prepareStatement(schedulesQuery)) {
            ps.setString(1, showId);
            try (ResultSet rsSchedules = ps.executeQuery()) {
                while (rsSchedules.next()) {
                    String tsId = rsSchedules.getString("THEATER_SHOW_ID");
                    String theaterId = rsSchedules.getString("THEATER_ID");
                    String reservationDate = rsSchedules.getString("RESERVATION_DATE");
                    String startTime = rsSchedules.getString("START_TIME");
                    String endTime = rsSchedules.getString("END_TIME");

                    String displayStart = (startTime != null && startTime.length() >= 5) ? startTime.substring(0, 5) : "";
                    String displayEnd = (endTime != null && endTime.length() >= 5) ? endTime.substring(0, 5) : "";
                    String timeRange = displayStart.isEmpty() ? displayEnd : (displayStart + (displayEnd.isEmpty() ? "" : "-" + displayEnd));

                    String desc = "ID: " + tsId + " | Theater: " + theaterId + " | " + reservationDate + " " + timeRange;
                    theaterShowIds.add(tsId);
                    scheduleDescriptions.add(desc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (scheduleDescriptions.isEmpty()) {
            System.out.println("No scheduled dates/times found for show: " + showTitle);
            return;
        }

        // Display scheduled dates/times for user to choose
        System.out.println("\nSelect a scheduled date/time for '" + showTitle + "':");
        for (int i = 0; i < scheduleDescriptions.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, scheduleDescriptions.get(i));
        }
        System.out.println(" 0. Cancel");

        int choice2 = -1;
        while (choice2 < 0) {
            System.out.print("Choose a number (0 to cancel): ");
            String input = scan.nextLine();
            try {
                choice2 = Integer.parseInt(input);
                if (choice2 < 0 || choice2 > scheduleDescriptions.size()) {
                    System.out.println("Invalid selection. Try again.");
                    choice2 = -1;
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a valid number.");
            }
        }

        if (choice2 == 0) {
            System.out.println("Cancelled.");
            return;
        }

        String theaterShowId = theaterShowIds.get(choice2 - 1);

        // Step 3: Fetch staff assigned to the selected theater show
        String query = "SELECT s.* " +
                       "FROM staff s " +
                       "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                       "WHERE sa.THEATER_SHOW_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theaterShowId);
            ResultSet rs = pstmt.executeQuery();

            Main.header("Staff for: " + showTitle);
            System.out.println(scheduleDescriptions.get(choice2 - 1));
            Main.subheader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayStaffRecord(rs);
            }

            if (!found) {
                System.out.println("No staff assigned to this scheduled show.\n");
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
            Main.header("Staff Management");
            System.out.println("1. Add New Staff");
            System.out.println("2. Update Staff Details");
            System.out.println("3. Delete Staff Record");
            System.out.println("4. View Staff by Position");
            System.out.println("5. View Staff by Employment Status");
            System.out.println("6. View All Staff Salaries");
            System.out.println("7. List All Staff Members");
            System.out.println("8. View Staff with Show Details");
            System.out.println("9. View All Staff for a Specific Show");
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
                    viewStaffWithShowDetails(scan);
                    break;
                case 9:
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

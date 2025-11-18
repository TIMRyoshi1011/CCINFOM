import java.sql.*;
import java.util.Scanner;

public class StaffAssignmentReport {

    private static final int width = 55;

    public static void generateReport(Scanner scan) {
        Main.header("STAFF ASSIGNMENT REPORT");

        System.out.println("\nSelect Report Period:");
        System.out.println("1 - Monthly");
        System.out.println("2 - Quarterly");
        System.out.println("3 - Yearly");
        System.out.print("\nChoose an option: ");

        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scan.nextLine().trim());
                if (choice >= 1 && choice <= 3)
                    break;
                System.out.print("Invalid option. Enter 1 (Monthly), 2 (Quarterly), or 3 (Yearly): ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter 1 (Monthly), 2 (Quarterly), or 3 (Yearly): ");
            }
        }

        int year;
        int month = 0;
        int quarter = 0;

        while (true) {
            System.out.print("Enter Year (e.g., 2025): ");
            String yearInput = scan.nextLine().trim();
            try {
                year = Integer.parseInt(yearInput);
                if (year > 2000)
                    break;
                System.out.println("Year must be greater than 2000. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid year. Please enter a numeric year (e.g., 2025).");
            }
        }

        if (choice == 1) {
            while (true) {
                System.out.print("Enter Month (1-12): ");
                String monthInput = scan.nextLine().trim();
                try {
                    int m = Integer.parseInt(monthInput);
                    if (m >= 1 && m <= 12) {
                        month = m;
                        break;
                    }
                    System.out.println("Month must be between 1 and 12. Try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid month. Please enter a number between 1 and 12.");
                }
            }
            generateMonthlyReport(year, month);
        } else if (choice == 2) {
            while (true) {
                System.out.print("Enter Quarter (1-4): ");
                String quarterInput = scan.nextLine().trim();
                try {
                    int q = Integer.parseInt(quarterInput);
                    if (q >= 1 && q <= 4) {
                        quarter = q;
                        break;
                    }
                    System.out.println("Quarter must be between 1 and 4. Try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quarter. Please enter a number between 1 and 4.");
                }
            }
            generateQuarterlyReport(year, quarter);
        } else if (choice == 3) {
            generateYearlyReport(year);
        }
    }

    private static void generateMonthlyReport(int year, int month) {
        String timeRange = getMonthName(month) + " " + year;
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theater_reservation tr ON ts.THEATER_RESERVATION_ID = tr.THEATER_RESERVATION_ID " +
                "WHERE YEAR(tr.RESERVED_DATE) = ? AND MONTH(tr.RESERVED_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

        generateReport(query, timeRange, year, month);
    }

    private static void generateQuarterlyReport(int year, int quarter) {
        String timeRange = "Q" + quarter + " " + year;
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theater_reservation tr ON ts.THEATER_RESERVATION_ID = tr.THEATER_RESERVATION_ID " +
                "WHERE YEAR(tr.RESERVED_DATE) = ? AND QUARTER(tr.RESERVED_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

        generateReport(query, timeRange, year, quarter);
    }

    private static void generateYearlyReport(int year) {
        String timeRange = "Year " + year;
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theater_reservation tr ON ts.THEATER_RESERVATION_ID = tr.THEATER_RESERVATION_ID " +
                "WHERE YEAR(tr.RESERVED_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

        generateReport(query, timeRange, year, null);
    }

    private static void generateReport(String query, String period, int year, Integer param2) {
        try (Connection conn = Main.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, year);
            if (param2 != null) {
                pstmt.setInt(2, param2);
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.println();
            Main.subheader();
            System.out.println(centerText("Staff Assignment Report - " + period, width));
            Main.subheader();

            // Print header
            System.out.printf("%-12s %-20s %6s   %8s%n",
                    "Staff ID", "Name", "Shows", "Hours");
            Main.subheader();

            int totalStaff = 0;
            int totalShows = 0;
            int totalHours = 0;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String staffId = rs.getString("STAFF_ID");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                String name = (firstName == null ? "" : firstName)
                        + (lastName == null || lastName.isEmpty() ? "" : " " + lastName);
                int shows = rs.getInt("total_shows");
                int hours = rs.getInt("total_hours");

                System.out.printf("%-12s %-20s %6d   %8d%n",
                        staffId, name, shows, hours);

                totalStaff++;
                totalShows += shows;
                totalHours += hours;
            }

            if (!hasData) {
                System.out.println(centerText("No staff assignments found for this period.", width));
            }

            Main.subheader();
            System.out.printf("%-12s %-20s %6s   %8s%n",
                    "TOTAL", totalStaff + " staff", totalShows, totalHours);
            Main.subheader();
        } catch (SQLException e) {
            System.out.println("\nError generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    private static String getMonthName(int month) {
        String[] months = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };
        return months[month - 1];
    }
}

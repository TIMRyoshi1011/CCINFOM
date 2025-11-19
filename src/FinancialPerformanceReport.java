import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FinancialPerformanceReport {

    private static final int width = 50;

        public static void generateReport(Scanner scan) {
        Main.header("FINANCIAL PERFORMANCE REPORT");

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
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? AND MONTH(b.booking_date) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date), MONTH(b.booking_date) " + 
                "ORDER BY s.title, year;";

        generateReport(query, timeRange, year, month);
    }

    private static void generateQuarterlyReport(int year, int quarter) {
        String timeRange = "Q" + quarter + " " + year;
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? " +
                "AND CEILING(MONTH(b.booking_date)/3) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date) " + 
                "ORDER BY s.title, year; ";

        generateReport(query, timeRange, year, quarter);
    }

    private static void generateYearlyReport(int year) {
        String timeRange = "Year " + year;
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date) " + 
                "ORDER BY s.title, year;";

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
            System.out.println("\tFinancial Performance Report - " + period);
            Main.subheader();

            // Print header
            System.out.printf("%s \t%s \t\t%s \t%6s%n",
                    "Year", "Title", "Total Revenue", "T Salary");
            Main.subheader();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int yearofshow = rs.getInt("year");
                String title = rs.getString("title");
                String revenue = rs.getString("total_show_revenue");
                String salary = rs.getString("total_salary_cost");

                System.out.printf("%-6d %-20s PHP %s \tPHP %-20s\n", yearofshow, title, revenue, salary);
            }

            if (!hasData) {
                System.out.println(centerText("No shows found for this period.", width));
            }

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TheaterPerformanceReport {
    
    private static final int width = 70;

    public static void generateReport (Scanner scan) {
        Main.header("THEATER PERFORMANCE REPORT");

        System.out.println("\nSelect Report Period:");
        System.out.println("1 - Monthly");
        System.out.println("2 - Quarterly");
        System.out.println("3 - Yearly");
        System.out.println("\nChoose an option: ");

        int choice;
        while (true) { 
            try {
                choice = Integer.parseInt(scan.nextLine().trim());
                if (choice >= 1 && choice <= 3)
                    break;
                System.out.println("Invalid option. Enter 1 (Monthly), 2 (Quarterly), or 3 (Yearly): ");
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
                    System.out.println("Month must be between 1 and 12. Please try again.");
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
                    System.out.println("Quarter must be between 1 and 4. Please try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid month. Please enter a number between 1 and 4.");
                }
            }
            generateQuarterlyReport(year, quarter);
        } else if (choice == 3) {
            generateYearlyReport(year);
        }
    }

    private static void generateMonthlyReport (int year, int month) {
        String timeRange = getMonthName(month) + " " + year;
        String query = "SELECT th.THEATER_ID, th.THEATER_NAME, " +
                        "COUNT(ts.THEATER_SHOW_ID) AS \"Total Shows Scheduled\", " +
                        "SUM(ts.AUDIENCE_TURNOUT) AS \"Total Audience\", " + 
                        "SUM(th.CAPACITY) AS \"Total Capacity\", " +
                        "CONCAT(ROUND((CAST(SUM(ts.AUDIENCE_TURNOUT) AS DECIMAL) / SUM(th.CAPACITY)) * 100, 2), \"%\")  AS \"Overall Occupancy Rate\" " + 
                        "FROM theaters th " + 
                        "JOIN theater_shows ts ON th.THEATER_ID = ts.THEATER_ID " + 
                        "WHERE YEAR(ts.RESERVATION_DATE) = ? AND MONTH(ts.RESERVATION_DATE) = ? " + 
                        "GROUP BY th.THEATER_ID, th.THEATER_NAME";

        generateReport(query, timeRange, year, month);
    }

    private static void generateQuarterlyReport (int year, int quarter) {
        String timeRange = "Q" + quarter + " " + year;
        String query = "SELECT th.THEATER_ID, th.THEATER_NAME, " +
                        "COUNT(ts.THEATER_SHOW_ID) AS \"Total Shows Scheduled\", " +
                        "SUM(ts.AUDIENCE_TURNOUT) AS \"Total Audience\", " + 
                        "SUM(th.CAPACITY) AS \"Total Capacity\", " +
                        "CONCAT(ROUND((CAST(SUM(ts.AUDIENCE_TURNOUT) AS DECIMAL) / SUM(th.CAPACITY)) * 100, 2), \"%\")  AS \"Overall Occupancy Rate\" " + 
                        "FROM theaters th " + 
                        "JOIN theater_shows ts ON th.THEATER_ID = ts.THEATER_ID " + 
                        "WHERE YEAR(ts.RESERVATION_DATE) = ? AND QUARTER(ts.RESERVATION_DATE) = ? " + 
                        "GROUP BY th.THEATER_ID, th.THEATER_NAME";
        
        generateReport(query, timeRange, year, quarter);
    }

    private static void generateYearlyReport (int year) {
        String timeRange = "Year " + year;
        String query = "SELECT th.THEATER_ID, th.THEATER_NAME, " +
                        "COUNT(ts.THEATER_SHOW_ID) AS \"Total Shows Scheduled\", " +
                        "SUM(ts.AUDIENCE_TURNOUT) AS \"Total Audience\", " + 
                        "SUM(th.CAPACITY) AS \"Total Capacity\", " +
                        "CONCAT(ROUND((CAST(SUM(ts.AUDIENCE_TURNOUT) AS DECIMAL) / SUM(th.CAPACITY)) * 100, 2), \"%\")  AS \"Overall Occupancy Rate\" " + 
                        "FROM theaters th " + 
                        "JOIN theater_shows ts ON th.THEATER_ID = ts.THEATER_ID " + 
                        "WHERE YEAR(ts.RESERVATION_DATE) = ? " + 
                        "GROUP BY th.THEATER_ID, th.THEATER_NAME";
        
        generateReport(query, timeRange, year, null);
    }

    private static void generateReport (String query, String period, int year, Integer param2) {
        try (Connection conn = Main.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, year);
            if (param2 != null) {
                pstmt.setInt(2, param2);
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.print("\n");
            Main.subheader;
            System.out.println(centerText("Theater Performance Report - " + period, width));
            Main.subheader();

            System.out.printf("%-12s, %-10s, %-12s, %-10s, %-10s, %-15s%n",
                            "Theater ID", "Theater Name", "Total Shows Scheduled", "Total Audience", "Total Capacity", "Overall Occupancy Rate");
            Main.subheader();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String theater_ID = rs.getString("THEATER_ID");
                String theater_Name = rs.getString("THEATER_NAME");
                int totalShowsScheduled = rs.getInt("Total Shows Scheduled");
                int totalAudience = rs.getInt("Total Audience");
                int totalCapacity = rs.getInt("Total Capacity");
                float overallOccupancyRate = rs.getFloat("Overall Occupancy Rate");

                System.out.printf("%-12s, %-10s, %-12d, %-10d, %-10d, %-15.2f%n", 
                                    theater_ID, theater_Name, totalShowsScheduled, totalAudience, totalCapacity, overallOccupancyRate);
            }

            if (!hasData) {
                System.out.println(centerText("No theaters utilized for this period.", width));
            }
            
        } catch (SQLException e) {
            System.out.println("\nError generating report: " + e.getMessage());
            e.printStackTrace();
        }
            
    }

    private static String centerText (String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text;
    }

    private static String getMonthName (int month) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", 
                            "August", "September", "October", "November", "December"};
        return months[month - 1];
    }


}

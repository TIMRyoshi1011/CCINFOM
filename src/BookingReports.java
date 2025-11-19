import java.sql.*;
import java.util.Scanner;

public class BookingReports {

    public static void bookingStatusReport(Scanner scan) {
        int choice = 0;
        Main.header("Booking Status Report");
        System.out.println("[1] Daily");
        System.out.println("[2] Monthly");
        System.out.println("[3] Yearly");
        System.out.print("Select option: ");
        choice = Integer.parseInt(scan.nextLine());

        String dateInput = "";
        String query = "";
        String reportType = "";

        switch (choice) {
            case 1: //Daily
                System.out.print("Enter date (YYYY-MM-DD): ");
                dateInput = scan.nextLine().trim();
                query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE booking_date = ? " +
                        "GROUP BY booking_status";
                break;
            case 2: //Monthly
                System.out.print("Enter month and year (MM-YYYY): ");
                dateInput = scan.nextLine().trim();
                query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE MONTH(booking_date) = ? AND YEAR(booking_date) = ? " +
                        "GROUP BY booking_status";
                break;
            case 3: //Yearly
                System.out.print("Enter year (YYYY): ");
                dateInput = scan.nextLine().trim();
                query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE YEAR(booking_date) = ? " +
                        "GROUP BY booking_status";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }


        try (Connection conn = Main.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // --- SET SQL PARAMETERS ---
            if (choice == 1) {
                ps.setString(1, dateInput);
                reportType = "Daily Report";

            } else if (choice == 2) {
                String[] parts = dateInput.split("-");
                ps.setInt(1, Integer.parseInt(parts[0]));
                ps.setInt(2, Integer.parseInt(parts[1]));
                reportType = "Monthly Report";

            } else if (choice == 3) {
                ps.setInt(1, Integer.parseInt(dateInput));
                reportType = "Annual Report";
            }

            ResultSet rs = ps.executeQuery();


            Main.header(reportType);
            System.out.println("\nSTATUS | COUNT");
            System.out.println("----------------");

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                System.out.printf("%-10s | %d\n",
                        rs.getString("booking_status"),
                        rs.getInt("count"));
            }
            if (!hasData) {
                System.out.println("No records found for that period.");
            }

            Main.next(scan);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

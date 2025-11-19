import java.sql.*;
import java.util.Scanner;

public class CustomerRecords {

    public static void enterCustomerDetails(Scanner scan) {
        System.out.println("");
        System.out.print("Enter your First Name: ");
        String fName = scan.nextLine();

        System.out.print("Enter your Last Name: ");
        String lName = scan.nextLine();

        System.out.print("Enter your Phone Number: ");
        String phoneNo = scan.nextLine();

        System.out.print("Enter your Email Address: ");
        String email = scan.nextLine();

        addCustomerToDB(fName, lName, phoneNo, email);
    }

    public static void addCustomerToDB(String fName, String lName, String phoneNo, String emailAdd) {
        String query = "INSERT INTO customers (FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL_ADDRESS) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, phoneNo);
            pstmt.setString(4, emailAdd);
            pstmt.executeUpdate();

            System.out.println("\nCustomer added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomerName(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Customer ID to update: ");
        String cusId = scan.nextLine();

        System.out.print("Enter New First Name (or press Enter to skip): ");
        String firstName = scan.nextLine();

        System.out.print("Enter New Last Name (or press Enter to skip): ");
        String lastName = scan.nextLine();

        System.out.print("Enter New Phone Number (or press Enter to skip): ");
        String phoneNo = scan.nextLine();

        System.out.print("Enter New Email Address (or press Enter to skip): ");
        String email = scan.nextLine();

        updateCustomerInDB(cusId, firstName, lastName, phoneNo, email);
    }

    public static void updateCustomerInDB(String cusId, String firstName, String lastName, String phoneNo, String email) {
        StringBuilder sql = new StringBuilder("UPDATE customers SET ");
        boolean firstField = true;

        if (!firstName.isEmpty()) {
            sql.append("first_name = ?");
            firstField = false;
        }

        if (!lastName.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("last_name = ?");
            firstField = false;
        }

        if (!phoneNo.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("phone_number = ?");
            firstField = false;
        }

        if (!email.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("email_address = ?");
        }

        sql.append(" WHERE customer_id = ?");

        if (firstName.isEmpty() && lastName.isEmpty() && phoneNo.isEmpty() && email.isEmpty()) {
            System.out.println("No fields to update.");
            return;
        }

        try (Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (!firstName.isEmpty()) {
                pstmt.setString(paramIndex++, firstName);
            }
            if (!lastName.isEmpty()) {
                pstmt.setString(paramIndex++, lastName);
            }
            if (!phoneNo.isEmpty()) {
                pstmt.setString(paramIndex++, phoneNo);
            }
            if (!email.isEmpty()) {
                pstmt.setString(paramIndex++, email);
            }

            pstmt.setString(paramIndex, cusId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("\nCustomer updated successfully.");
            } else {
                System.out.println("\nCustomer ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCustomer(Scanner scan) {
        System.out.println("");
        System.out.print("Enter Customer ID to delete: ");
        String cusId = scan.nextLine();

        if (cusId.isEmpty()) {
            System.out.println("Customer ID cannot be empty.");
            return;
        }

        deleteCustomerInDB(cusId);
    }

    public static void deleteCustomerInDB(String cusId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cusId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("\nCustomer deleted successfully.");
            } else {
                System.out.println("\nCustomer ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewCustomerByLName() {
        String query = "SELECT FIRST_NAME, LAST_NAME FROM customers ORDER BY LAST_NAME ASC";
        int counter = 1;

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--------------All Customer Names Alphabetically----------------");

            while (rs.next()) {
                String fName = rs.getString("FIRST_NAME");
                String lName = rs.getString("LAST_NAME");

                System.out.println(counter + " - " + lName + ", " + fName);
                counter++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listAllCustomerNames() {
        String query = "SELECT FIRST_NAME, LAST_NAME FROM customers";
        int counter = 1;

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--------------------All Customer Names--------------------");

            while (rs.next()) {
                String fName = rs.getString("FIRST_NAME");
                String lName = rs.getString("LAST_NAME");

                System.out.println(counter + " - " + fName + " " + lName);
                counter++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listAllCustomers() {
        String query = "SELECT * FROM customers";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n-----------------------All Customers-----------------------");

            while (rs.next()) {
                String id = rs.getString("CUSTOMER_ID");
                String fName = rs.getString("FIRST_NAME");
                String lName = rs.getString("LAST_NAME");
                String phoneNo = rs.getString("PHONE_NUMBER");
                String emailAdd = rs.getString("EMAIL_ADDRESS");

                System.out.println("ID: " + id + " | Name: " + fName + " " + lName);
                System.out.println("Phone Number: " + phoneNo + " | Email Address: " + emailAdd);
                System.out.println("-------------------------------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewCustomerBooking() {
        String query = "SELECT c.CUSTOMER_ID, c.FIRST_NAME, c.LAST_NAME, b.NOOFTICKETS, " +
                        "GROUP_CONCAT(CONCAT('Row: ', s.ROW_NO, ', Col: ', s.COL_NO) SEPARATOR ' | ') AS SEATS, " +
                        "sh.TITLE " +
                        "FROM booking b " +
                        "JOIN customers c ON b.CUSTOMER_ID = c.CUSTOMER_ID " +
                        "JOIN seat_booking sb ON b.BOOKING_ID = sb.BOOKING_ID " +
                        "JOIN seat s ON sb.SEAT_ID = s.SEAT_ID " +
                        "JOIN theater_shows ts ON b.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                        "JOIN shows sh ON ts.SHOW_ID = sh.SHOW_ID " +
                        "GROUP BY b.BOOKING_ID;";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n-------------------All Customer Bookings--------------------");

            while (rs.next()) {
                String cusId = rs.getString("CUSTOMER_ID");
                String fName = rs.getString("FIRST_NAME");
                String lName = rs.getString("LAST_NAME");
                int noTickets = rs.getInt("NOOFTICKETS");
                String seats = rs.getString("SEATS"); 
                String title = rs.getString("TITLE");

                System.out.println("Customer ID: " + cusId);
                System.out.println("Name: " + fName + " " + lName);
                System.out.println("Number of tickets: " + noTickets);
                System.out.println("Seats: " + seats);
                System.out.println("Show Title: " + title);
                System.out.println("-------------------------------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void customerMenu(Scanner scan) {
        int option = 0;

        do {
            System.out.println("\n================= Customer Management =================");
            System.out.println("1. Add customer details");
            System.out.println("2. Update customer details");
            System.out.println("3. Delete a customer");
            System.out.println("4. View customers by last name alphabetically");
            System.out.println("5. List all customer full names");
            System.out.println("6. List all customer details");
            System.out.println("7. View customer with booking details");
            System.out.println("0. Back to Main Menu");
            System.out.print("\nChoose an option: ");
            option = Integer.parseInt(scan.nextLine());

            switch (option) {
                case 1:
                    enterCustomerDetails(scan);
                    Main.next(scan);
                    break;
                case 2:
                    updateCustomerName(scan);
                    Main.next(scan);
                    break;
                case 3:
                    deleteCustomer(scan);
                    Main.next(scan);
                    break;
                case 4:
                    viewCustomerByLName();
                    Main.next(scan);
                    break;
                case 5:
                    listAllCustomerNames();
                    Main.next(scan);
                    break;
                case 6:
                    listAllCustomers();
                    Main.next(scan);
                    break;
                case 7:
                    viewCustomerBooking();
                    Main.next(scan);
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

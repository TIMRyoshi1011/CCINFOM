//import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final int width = 50;

    public static void header(String title) {
        String content = " " + title + " ";
        int padding = width - content.length();
        if (padding <= 0) {
            System.out.println();
            System.out.println(content);
            return;
        }
        int left = padding / 2;
        int right = padding - left;
        System.out.println();
        System.out.println("=".repeat(left) + content + "=".repeat(right));
    }

    public static void subheader() {
        System.out.println("-".repeat(width));
    }

    private static final String URL = "jdbc:mysql://localhost:3306/theatershows";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // <----- enter your password in mysql

    private static Connection conn = null;

    private static Scanner scan = new Scanner(System.in);

    /*
     * private static void createDatabase() throws databaseCreated {
     * String url = "jdbc:mysql://localhost:3306/?allowMultiQueries=true";
     * String scriptFilePath = "GROUP8-DBCREATION.sql";
     * Statement statement = null;
     * 
     * try {
     * conn = DriverManager.getConnection(url, USER, PASSWORD); // Create a
     * connection to MySQL (no database
     * // selected yet)
     * statement = conn.createStatement(); // Create a Statement object to execute
     * the script
     * 
     * // check if database already exists
     * String checkDBQuery = "SHOW DATABASES LIKE 'theatershows';";
     * ResultSet rs = statement.executeQuery(checkDBQuery);
     * 
     * if (rs.next()) {
     * throw new
     * databaseCreated("Database already exists. Proceeding to connect...");
     * }
     * 
     * else {
     * String script = readScriptFile(scriptFilePath); // Read the SQL script file
     * statement.executeUpdate(script); // Execute the script to create the database
     * System.out.println("Database created successfully!");
     * }
     * 
     * } catch (SQLException | IOException e) {
     * System.out.println("Error: " + e.getMessage());
     * } finally {
     * try {
     * if (statement != null) {
     * statement.close();
     * }
     * if (conn != null) {
     * conn.close();
     * }
     * } catch (SQLException ex) {
     * System.out.println("Error: " + ex.getMessage());
     * }
     * }
     * }
     * 
     * private static String readScriptFile(String filePath) throws IOException {
     * StringBuilder script = new StringBuilder();
     * BufferedReader reader = new BufferedReader(new FileReader(filePath));
     * String line;
     * while ((line = reader.readLine()) != null) {
     * script.append(line).append("\n");
     * }
     * reader.close();
     * return script.toString();
     * }
     */
    public static void connectToDB() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database.\n");

        } catch (SQLException e) {
            System.out.println("Error connecting to DB: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }

    // --------------------------Records Management------------------------------

    public static void manageRecords() {
        int select;
        header("Select Records");
        System.out.println("1 - Show Records");
        System.out.println("2 - Theater Records");
        System.out.println("3 - Staff Records");
        System.out.println("4 - Customer Records");
        System.out.println("0 - Exit");
        System.out.print("\nChoose an option: ");

        do {
            select = Integer.parseInt(scan.nextLine());

            switch (select) {
                case 1:
                    ShowRecords.ShowMenu(scan);
                    break;
                case 2:
                    System.out.println("\nEnter Add Theater Here\n");
                    break;
                case 3:
                    StaffRecords.staffMenu(scan);
                    break;
                case 4:
                    CustomerRecords.customerMenu(scan);
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.print("\nInvalid option. Please try again: ");
            }
        } while (select > 4 || select < 0);
    }

    // --------------------------Transactions------------------------------
    public static void makeTransaction() {
        int select;
        header("Select Transaction");
        System.out.println("1 - Booking Show Tickets");
        System.out.println("2 - Cancelling Bookings");
        System.out.println("3 - Setting Staff Assignments");
        System.out.println("4 - Scheduling Shows");
        System.out.println("0 - Exit");
        System.out.print("\nChoose an option: ");

        do {
            select = Integer.parseInt(scan.nextLine());

            switch (select) {
                case 1:
                    System.out.println("\nBooking Show Tickets\n");
                    break;
                case 2:
                    System.out.println("\nCancelling Bookings\n");
                    break;
                case 3:
                    StaffAssignment.assignStaff(scan);
                    break;
                case 4:
                    System.out.println("\nScheduling Shows\n");
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.print("\nInvalid option. Please try again: ");
            }
        } while (select > 4 || select < 0);
    }

    // --------------------------Reports to be Generated------------------------------
    public static void generateReports() {
        int select;
        header("Select Report");
        System.out.println("1 - Booking Status Report");
        System.out.println("2 - Theater Performance Report");
        System.out.println("3 - Financial Performance Report");
        System.out.println("4 - Staff Assignment Report");
        System.out.println("0 - Exit");
        System.out.print("\nChoose an option: ");

        do {
            select = Integer.parseInt(scan.nextLine());

            switch (select) {
                case 1:
                    System.out.println("\nBooking Status Report\n");
                    break;
                case 2:
                    System.out.println("\nTheater Performance Report\n");
                    break;
                case 3:
                    System.out.println("\nFinancial Performance Report\n");
                    break;
                case 4:
                    System.out.println("\nStaff Assignment Report\n");
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.print("\nInvalid option. Please try again: ");
            }
        } while (select > 4 || select < 0);
    }

    public static void main(String[] args) {
        clearConsole();
        int option = -1;

        // check if database is already created:
        // try {
        // createDatabase(); // Step 1: Create the database
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD); // Step 1: Check if database exists
        } catch (SQLException e) {
            option = 0;
        }

        connectToDB(); // Step 2: Connect to the database
        
        while (option != 0) {
            System.out.println("1 - Manage Records");
            System.out.println("2 - Make a Transaction");
            System.out.println("3 - Generate Reports");
            System.out.println("0 - Exit");
            System.out.print("\nChoose an option: ");
            option = Integer.parseInt(scan.nextLine());

            switch (option) {
                case 1:
                    manageRecords();
                    clearConsole();
                    break;
                case 2:
                    makeTransaction();
                    clearConsole();
                    break;
                case 3:
                    generateReports();
                    clearConsole();
                    break;
                case 0:
                    System.out.println("Thank You!");
                    break;
                default:
                    System.out.println("\nInvalid option. Please try again.\n");
            }
        }

        scan.close();
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

// class databaseCreated extends Exception {
// public databaseCreated(String message) {
// super(message);
// }

// }

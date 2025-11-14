import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class TheaterShows {

    private static final String URL = "jdbc:mysql://localhost:3306/theatershows";
    private static final String USER = "root";
    private static final String PASSWORD = " "; //<----- enter your password in mysql

    private static Connection conn = null;

    private static Scanner scan = new Scanner(System.in);;

    private static void createDatabase() throws databaseCreated {
        String url = "jdbc:mysql://localhost:3306/?allowMultiQueries=true";
        String scriptFilePath = "GROUP8-DBCREATION.sql"; 
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(url, USER, PASSWORD); // Create a connection to MySQL (no database selected yet)

            statement = conn.createStatement();                     // Create a Statement object to execute the script

            // check if database already exists
            String checkDBQuery = "SHOW DATABASES LIKE 'theatershows';";
            ResultSet rs = statement.executeQuery(checkDBQuery);

            if (rs.next()) {
                throw new databaseCreated("Database already exists. Proceeding to connect...");
            } 

            else {
                String script = readScriptFile(scriptFilePath);         // Read the SQL script file
                statement.executeUpdate(script);                         // Execute the script to create the database
                System.out.println("Database created successfully!");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String readScriptFile(String filePath) throws IOException {
        StringBuilder script = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            script.append(line).append("\n");
        }
        reader.close();
        return script.toString();
    }

    public static void connectToDB() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database.\n");

        }   catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void enterCustomerDetails() {
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
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, phoneNo);
            pstmt.setString(4, emailAdd);
            pstmt.executeUpdate();

        }   catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewCustomers() {
        String query = "SELECT * FROM customers";

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("CUSTOMER_ID");
                String fName = rs.getString("FIRST_NAME");
                String lName = rs.getString("LAST_NAME");
                String phoneNo = rs.getString("PHONE_NUMBER");
                String emailAdd = rs.getString("EMAIL_ADDRESS");

                System.out.println("ID: " + id + ", \nFirst Name: " + fName + ", \nLast Name: " + lName + ", \nPhone Number: " + phoneNo + ", \nEmail Address: " + emailAdd);
                System.out.println("-------------------------------------------------------");
            }

        }   catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {   

        // check if database is already created:
        try {
            createDatabase();       // Step 1: Create the database 
        } catch (Exception e) {
            // System.out.println("Database already exists. Proceeding to connect...");
            System.out.println(e.getMessage());
        }

        connectToDB();             // Step 2: Connect to the database
        int option = 0;

        do {
            System.out.println("1. Add Show");
            System.out.println("2. Add Theater");
            System.out.println("3. Add Staff");
            System.out.println("4. Add Customer");
            System.out.println("0. Exit");
            System.out.print("\nChoose an option: ");
            option = Integer.parseInt(scan.nextLine());

            switch (option) {   // update this main function to change UI (was thinking of like option 1: customer view, option 2: admin view somethin like that)
                case 1:
                    System.out.println("\nEnter Add Show Here\n");
                    break;
                case 2:
                    System.out.println("\nEnter Add Theater Here\n");
                    break;
                case 3:
                    System.out.println("\nEnter Add Staff Here\n");
                    break;
                case 4:
                    enterCustomerDetails();
                    System.out.println("\n-----------------------Customers-----------------------");
                    viewCustomers();          // View all customers in the database, will move this once UI is finalized
                    System.out.println("");
                    break;                      // Will add UPDATE and DELETE next
                case 0:
                    System.out.println("Thank You!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (option != 0);
        scan.close();
    }
}

class databaseCreated extends Exception {
    public databaseCreated(String message) {
        super(message);
    }
}
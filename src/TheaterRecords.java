import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class TheaterRecords {

    //Add Theater Records
    public static void addTheaterRecord(Scanner scan) {

        String theater_Name, theater_Status;
        int capacity, max_Rows, max_Cols;

        do { 
            System.out.print("Theater Name: ");
            theater_Name = scan.nextLine().toUpperCase();

            if(theater_Name.isEmpty()) {
                System.out.println("No input, please enter a valid name.");
            }
        } while (theater_Name.isEmpty());

        do { 
            System.out.print("Theater Capacity: ");
            capacity = scan.nextInt();
            scan.nextLine(); //to consume new line

            if(capacity <= 0) {
                System.out.println("Capacity should be greater than 0, please try again.");
            }
        } while (capacity <= 0);

        do { 
            System.out.print("Maximum Rows: ");
            max_Rows = scan.nextInt();
            scan.nextLine(); //to consume new line

            if(max_Rows <= 0 || max_Rows > 5) {
                System.out.println("Maximum Rows should be greater than 0 but less than or equal to 5, please try again.");
            }
        } while (max_Rows <= 0 || max_Rows > 5);

        do { 
            System.out.print("Maximum Columns: ");
            max_Cols = scan.nextInt();
            scan.nextLine(); //to consume new line

            if(max_Cols <= 0 || max_Cols > 10) {
                System.out.println("Maximum Rows should be greater than 0 but less than or equal to 10, please try again.");
            }
        } while (max_Rows <= 0 || max_Rows > 5);

        do { 
            System.out.print("Theater Status: ");
            theater_Status = scan.nextLine().trim().toUpperCase();
        } while (theater_Status.isEmpty());

        addTheaterToDB(theater_Name, capacity, max_Rows, max_Cols, theater_Status);

        System.out.println("Theater record added.");
    }

    //Adds Theater Record to Database
    public static void addTheaterToDB (String theater_Name, int capacity, int max_Rows, int max_Cols, String theater_Status) {
        String query = "INSERT INTO theaters (THEATER_NAME, CAPACITY, MAX_ROWS, MAX_COLS, THEATER_STATUS) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, theater_Name);
            pstmt.setInt(2, capacity);
            pstmt.setInt(3, max_Rows);
            pstmt.setInt(4, max_Cols);
            pstmt.setString(5, theater_Status);
            pstmt.executeUpdate();

            System.out.println("\nTheater Record added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Updates Theater Name 
    public static void updateTheaterRecord (Scanner scan) {
        System.out.println("\n");
        System.out.print("Enter Theater ID to update: ");
        String theater_ID = scan.nextLine();

        System.out.print("Enter New Theater Name (or press enter to skip): ");
        String theater_Name = scan.nextLine();

        System.out.print("Enter New Theater Capacity (or press enter to skip): ");
        int capacity = scan.nextInt();
        scan.nextLine(); //To consume new line

        System.out.print("Enter New Theater Maximum Rows (or press enter to skip): ");
        int max_Rows = scan.nextInt();
        scan.nextLine(); //To consume new line

        System.out.print("Enter New Theater Maximum Columns (or press enter to skip): ");
        int max_Cols = scan.nextInt();
        scan.nextLine(); //To consume new line

        System.out.print("Enter New Theater Status (or press enter to skip): ");
        String theater_Status = scan.nextLine();

        updateTheaterInDB(theater_ID, theater_Name, capacity, max_Rows, max_Cols, theater_Status);

    }

    //Updates Theater Record in Database
    public static void updateTheaterInDB(String theater_ID, String theater_Name, int capacity, int max_Rows, int max_Cols, String theater_Status) {
        StringBuilder query = new StringBuilder("UPDATE theaters SET ");
        boolean hasUpdate = false;

        try {
            Connection conn = Main.getConnection();

            if (!theater_Name.isEmpty()) {
                query.append("THEATER_NAME = ?, ");
                hasUpdate = true;
            }
            if (capacity > 0) {
                query.append("CAPACITY = ?, ");
                hasUpdate = true;
            }
            if (max_Rows > 0 && max_Rows <= 5) {
                query.append("MAX_ROWS = ?, ");
                hasUpdate = true;
            }
            if (max_Cols > 0 && max_Cols <= 10) {
                query.append("MAX_COLS = ?, ");
                hasUpdate = true;
            }
            if (!theater_Status.isEmpty()) {
                query.append("THEATER_STATUS = ?, ");
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No updates provided.");
                return;
            }

            query.setLength(query.length() - 2); //Removes last comma and space
            query.append(" WHERE THEATER_ID = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!theater_Name.isEmpty()) {
                pstmt.setString(paramIndex++, theater_Name);
            }
            if (capacity > 0) {
                pstmt.setInt(paramIndex++, capacity);
            }
            if (max_Rows > 0 && max_Rows <= 5) {
                pstmt.setInt(paramIndex++, max_Rows);
            }
            if (max_Cols > 0 && max_Cols <= 10) {
                pstmt.setInt(paramIndex++, max_Cols);
            }
            if (!theater_Status.isEmpty()) {
                pstmt.setString(paramIndex++, theater_Status);
            }

            pstmt.setString(paramIndex, theater_ID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nTheater Details updated successfully!");
            } else {
                System.out.println("\nTheater ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Delete Theater Records 
    public static void deleteTheaterRecord (Scanner scan) {
        System.out.println("\n");
        System.out.println("Enter Theater ID to delete: ");
        String theater_ID = scan.nextLine();

        String query = "DELETE FROM theaters WHERE THEATER_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theater_ID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Theater deleted successfully");
            } else {
                System.out.println("Theater not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //View Theater Record by Status 
    public static void viewTheaterRecordByStatus (Scanner scan) {
        System.out.print("\n");
        System.out.print("Enter Status to search (ACTIVE, INACTIVE): ");
        String reservation_Status = scan.nextLine();

        String query = "SELECT * FROM theaters WHERE STATUS = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, reservation_Status);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-----------------------Theater by Status: " + reservation_Status + "-----------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                //displayTheaterRecord(rs);
            }

            if(!found) {
                System.out.println("No theater record exists with the " + reservation_Status + " status");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     //List All Active Theaters 
    public static void listAllActiveTheaters () {
        String query = "SELECT * FROM theaters WHERE THEATER_STATUS = 'ACTIVE'";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n-----------------------All Active Theaters-----------------------");
            while (rs.next()) {
                //displayTheaterRecord(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //List All Theaters 
    public static void listAllTheaters () {
        String query = "SELECT * FROM theaters";

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n-----------------------All Theaters-----------------------");
            while (rs.next()) {
                //displayTheaterRecord(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Theater Capacity + Reservation Status (Theater Reservation) 
    public static void viewCapacityWithReservationStatus (Scanner scan) {
        System.out.print("\n");
        System.out.print("Enter Theater ID: ");
        String theater_ID = scan.nextLine();

        String query = "SELECT th.*, tr.RESERVED_DATE, tr.RESERVATION_STATUS" +
                        "FROM theaters th" +
                        "LEFT JOIN theater_reservation tr ON th.THEATER_ID = tr.THEATER_ID" +
                        "WHERE th.THEATER_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theater_ID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-----------------------Theater Details with Reservation Status-----------------------");
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    found = true;
                    //displayTheaterRecord(rs);
                }  

                String theater_Name = rs.getString("THEATER_NAME");
                if (theater_Name != null) {
                    int capacity = rs.getInt("CAPACITY");
                    String theater_Status = rs.getString("THEATER_STATUS");
                    LocalDate reserved_Date = rs.getDate(5).toLocalDate();
                    String reservation_Status = rs.getString("RESERVATION_STATUS");
                    System.out.println("Theater Name: " + theater_Name + " | Capacity: " + capacity + " | Theater Status: " + theater_Status + 
                                        " | Reserved Date: " + reserved_Date + " | Reservation Status: " + reservation_Status + " |");
                }
            }

            if (!found) {
                System.out.println("Theater ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Theater to be used by a show
    public static void viewTheaterUsedByShow (Scanner scan) {
        System.out.print("\n");
        System.out.print("Enter Show ID: ");
        String show_ID = scan.nextLine();

        String query = "SELECT sh.SHOW_ID, sh.TITLE, th.THEATER_NAME" +
                        "FROM theaters th" +
                        "LEFT JOIN theater_reservation tr ON th.THEATER_ID = tr.THEATER_ID" + 
                        "LEFT JOIN theater_shows ts ON tr.THEATER_RESERVATION_ID = ts.THEATER_RESERVATION_ID" +
                        "LEFT JOIN shows sh ON ts.SHOW_ID = sh.SHOW_ID" +
                        "WHERE sh.SHOW_ID = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, show_ID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n-----------------------Theater to be used by a Show-----------------------");
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    found = true;
                    //displayTheaterRecord(rs);
                }
                
                String show_Title = rs.getString("TITLE");
                if (show_Title != null) {
                    String theater_Name = rs.getString("THEATER_NAME");
                    System.out.println("Show Title: " + show_Title + "Theater Name: " + theater_Name);
                }
            }

            if (!found) {
                System.out.println("Show ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Helper method to display theater record
    private static void displayTheaterRecord (ResultSet rs) throws SQLException {
        String theater_ID = rs.getString("THEATER_ID");
        String theater_Name = rs.getString("THEATER_NAME");
        int capacity = rs.getInt("CAPACITY");
        int max_Rows = rs.getInt("MAX_ROWS");
        int max_Cols = rs.getInt("MAX_COLS");
        String theater_Status = rs.getString("THEATER_STATUS");

        System.out.println("Theater ID: " + theater_ID + " | Theater Name: " + theater_Name + " | Capacity: " + capacity + 
                            " | Max Rows: " + max_Rows + " | Max Columns: " + max_Cols + " | Theater Status: " + theater_Status);
        Main.header();
    }

    public static void theaterMenu(Scanner sc) {
        int option = 0;

        do { 
            System.out.println("\n=================== Theater Management ===================");
            System.out.println("1. Add New Theater");
            System.out.println("2. Update Theater Details");
            System.out.println("3. Delete Theater Record");
            System.out.println("4. View Theater Record by Status");
            System.out.println("5. List All Active Theaters");
            System.out.println("6. List All Theaters");
            System.out.println("7. View Theater Capacity and Reservation Status");
            System.out.println("8. View Theater Used by Show");
            System.out.println("0. Returning to main menu...");
        
            switch (option) {
                case 1:
                    addTheaterRecord(sc);
                    break;
                case 2:
                    updateTheaterRecord(sc);
                    break;
                case 3:
                    deleteTheaterRecord(sc);
                    break;
                case 4:
                    viewTheaterRecordByStatus(sc);
                    break;
                case 5:
                    listAllActiveTheaters();
                    break;
                case 6:
                    listAllTheaters();
                    break;
                case 7:
                    viewCapacityWithReservationStatus(sc);
                    break;
                case 8:
                    viewTheaterUsedByShow(sc);
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }
}

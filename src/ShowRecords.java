import java.sql.*;
import java.time.LocalTime;
import java.util.Scanner;

public class ShowRecords {


    private static void addShowToDB(String title, LocalTime runtime,int price, String status){

        String query = "INSERT INTO shows (title, runtime, show_price, status) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Main.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setTime(2, java.sql.Time.valueOf(runtime));
            pstmt.setInt(3, price);
            pstmt.setString(4, status);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Show added successfully!" : "No rows inserted.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting show.");
        }
    }

    private static void enterShowDetails(Scanner scan) {
        System.out.print("\nEnter Show Title: ");
        String title = scan.nextLine();

        System.out.print("Enter Runtime(HH:MM:SS): ");
        LocalTime runtime = LocalTime.parse(scan.nextLine());

        System.out.print("Enter Price: ");
        int price = scan.nextInt();
        scan.nextLine();

        System.out.print("Enter Status(Upcoming, Ongoing, Completed): ");
        String status = scan.nextLine().toUpperCase().trim();


        addShowToDB(title, runtime, price, status);
    }

    private static Show getShowById(String showId) {
        String query = "SELECT * FROM shows WHERE show_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, showId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Show(
                        rs.getString("title"),
                        rs.getTime("runtime").toLocalTime(),
                        rs.getInt("price"),
                        rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void updateShowInDB(String showId, String newTitle,
                                      LocalTime runtime, int price, String status) {

        String query = "UPDATE shows SET title = ?, runtime = ?, "
                + "show_price = ?, status = ? WHERE show_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newTitle);
            pstmt.setTime(2, java.sql.Time.valueOf(runtime));
            pstmt.setInt(3, price);
            pstmt.setString(4, status);
            pstmt.setString(5, showId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Show updated successfully!");
            } else {
                System.out.println("Show not found or no changes made.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error updating show.");
        }
    }

    private static void updateShowDetails(Scanner scan) {

        System.out.println("\n--- UPDATE SHOW DETAILS ---");

        System.out.print("Enter the Show ID of the show you want to update: ");
        String showId = scan.nextLine();

        Show show = getShowById(showId);

        if (show == null){
            System.out.println("Show not Found.");
            return;
        }

        show.showDetails();

        System.out.print("Enter NEW Show Title: (ENTER to skip)");
        String newTitle = scan.nextLine();
        if (newTitle.isEmpty()) newTitle = show.getTitle();

        System.out.print("Enter NEW Runtime (HH:MM:SS): (ENTER to skip)");
        String runtimeInput = scan.nextLine();
        LocalTime newRuntime = runtimeInput.isEmpty()
                                ? show.getShowRuntime()
                                : LocalTime.parse(runtimeInput);

        System.out.print("Enter NEW Price: (ENTER to skip)");
        String priceInput = scan.nextLine();
        int newPrice = priceInput.isEmpty() ? show.getPrice()
                    : Integer.parseInt(priceInput);

        System.out.print("Enter NEW Status(Upcoming, Ongoing, Completed) (ENTER to skip): ");
        String statusInput = scan.nextLine();
        String newStatus = statusInput.isEmpty() ? show.getShowStatus()
                : statusInput.trim().toUpperCase();

        updateShowInDB(showId, newTitle, newRuntime, newPrice, newStatus);
    }

    private static void deleteShow(Scanner scan){
        System.out.print("\nEnter Show ID to delete: ");
        String showId = scan.nextLine();

        String query = "DELETE FROM shows WHERE show_id = ?";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, showId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nShow deleted successfully!");
            } else {
                System.out.println("\nShow ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayShowRecord(ResultSet rs) throws SQLException {
        String showId = rs.getString("show_id");
        String title = rs.getString("title");
        LocalTime runtime = LocalTime.parse(rs.getString("runtime"));
        String price = rs.getString("price");
        String status = rs.getString("STATUS");

        System.out.println("Show ID: " + showId + " | Title: " + title);
        System.out.println("Price: P" + price + " | Status: " + status);
        System.out.println("Runtime: " + runtime);
        System.out.println("-------------------------------------------------------");
    }

    private static void viewAllShows(){
        String query = "SELECT * FROM shows";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("------LIST OF SHOWS-------");
            while(rs.next()){
                displayShowRecord(rs);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewShowDetails(Scanner scan){

        String query = "SELECT * FROM shows WHERE show_id = ?";
        System.out.print("Enter Show ID of Show: ");
        String showId = scan.nextLine();

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1,showId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.getRow() <= 0){
                System.out.println("Show Not Found.");
                return;
            }

            System.out.println("-----Details of " + showId +" ---------");
            displayShowRecord(rs);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewUpcomingShows(){

        String query = "SELECT * FROM shows WHERE status like 'UPCOMING'";

        try{
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.getRow() <= 0){
                System.out.println("No Upcoming Shows.");
                return;
            }

            System.out.println("------- Upcoming Shows---------");
            while (rs.next()){
                displayShowRecord(rs);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void staffMenu(Scanner scan) {
        int option;

        do {
            System.out.println("=================== Shows ===================");
            System.out.println("1. Add New Show");
            System.out.println("2. Update Show Details");
            System.out.println("3. Delete Show");
            System.out.println("4. View All Shows");
            System.out.println("5. View Show Details");
            System.out.println("6. List All Upcoming Shows");
            System.out.println("7. View Show Details With Seat Availability");
            System.out.println("0. Back to Main Menu");
            System.out.println("Choose an option: ");
            option = Integer.parseInt(scan.nextLine());

            switch (option) {
                case 1:
                    enterShowDetails(scan);
                    break;
                case 2:
                    updateShowDetails(scan);
                    break;
                case 3:
                    deleteShow(scan);
                    break;
                case 4:
                    viewAllShows();
                    break;
                case 5:
                    viewShowDetails(scan);
                    break;
                case 6:
                    viewUpcomingShows();
                    break;
                case 7:

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

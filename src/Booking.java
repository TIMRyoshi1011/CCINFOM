import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Booking {



    public static void addNewTheaterShow(Scanner sc){

        Main.header("Add New Theater Show");
        ShowRecords.viewOngoingShows();
        System.out.print("Input Show Id to Schedule: ");
        String showId = sc.nextLine().toUpperCase().trim();

        if(ShowRecords.getShowById(showId) == null){
            System.out.println("Show Not Found");
            return;
        }

        TheaterRecords.listAllActiveTheaters();
        System.out.print("Input Theater Id to Schedule: ");
        String theaterId = sc.nextLine().toUpperCase().trim();

        if(TheaterRecords.getTheaterById(theaterId) == null){
            System.out.println("Theater Not Found");
            return;
        }

        System.out.print("Enter Date To Schedule (YYYY-MM-DD): ");
        String inputDate = sc.nextLine();
        LocalDate date = LocalDate.parse(inputDate);

        LocalTime runtime = Objects.requireNonNull(ShowRecords.getShowById(showId)).getShowRuntime();
        System.out.print("Enter Start Time (HH:MM:SS): ");
        String inputTime = sc.nextLine();
        LocalTime startTime = LocalTime.parse(inputTime);

        Duration duration = Duration.ofHours(runtime.getHour())
                            .plusMinutes(runtime.getMinute())
                            .plusSeconds(runtime.getSecond());

        LocalTime endTime = startTime.plus(duration);
        String status = "SCHEDULED";

        addTheaterShowToDB(theaterId, showId, startTime, endTime, date, status);
    }

    private static void addTheaterShowToDB(String theaterId, String showId, LocalTime startTime,
                                           LocalTime endTime, LocalDate date, String status){

        String query = "INSERT INTO shows (theater_id, show_id, start_time, end_time, res_date, show_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, theaterId);
            pstmt.setString(2, showId);
            pstmt.setTime(3, java.sql.Time.valueOf(startTime));
            pstmt.setTime(4, java.sql.Time.valueOf(endTime));
            pstmt.setDate(5, java.sql.Date.valueOf(date));
            pstmt.setString(6, status);

            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Theater Show added successfully!" : "No rows inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Customer getCustomerByID(String customerId){
        String query = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = Main.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getString("email_address")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void viewAllScheduledTheaterShows(){
        String query = "SELECT t.theater_show_id, s.title, t.start_time, s.show_price, t.res_date " +
                        "FROM theater_shows t JOIN shows s ON s.show_id = t.show_id " +
                        "WHERE show_status like 'SCHEDULED'";

        try{
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;

            Main.header("Scheduled Theater Shows");
            while (rs.next()){
                found = true;
                displayTheaterShow(rs);
            }

            if(!found){
                System.out.println("No Scheduled Theater Shows.");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void displayTheaterShow(ResultSet rs) throws SQLException{
        String tsID = rs.getString("theater_show_id");
        String title = rs.getString("title");
        LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
        int price = rs.getInt("show_price");
        LocalDate date = LocalDate.parse(rs.getString("res_date"));

        System.out.println("Theater Show ID: " + tsID + " | Title: " + title);
        System.out.println("Price: P" + price + " | Start Time: " + startTime);
        System.out.println("Date: " + date);
        System.out.println("-------------------------------------------------------");
    }

    private static int getTotalPrice(String theater_show_id, int tix){
        String query = "SELECT  s.show_price " +
                "FROM theater_shows t JOIN shows s ON s.show_id = t.show_id " +
                "WHERE theater_show_id = ?";

        int price = -1;
        try{
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theater_show_id);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;

            Main.header("Scheduled Theater Shows");
            while (rs.next()){
                found = true;
                price = rs.getInt("show_price");
            }

            if(!found){
                System.out.println("Show Not Found.");
                return -1;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return price * tix;
    }

    public static void bookShowTickets(Scanner sc){

        Main.header("Book Show Tickets");
        CustomerRecords.listAllCustomers();
        System.out.print("Enter Customer Id: ");
        String customerId = sc.nextLine().trim().toUpperCase();

        if (getCustomerByID(customerId) == null){
            System.out.print("Customer Not Found.");
            Main.next(sc);
            return;
        }

        viewAllScheduledTheaterShows();
        System.out.print("Input Theater Show ID to Book: ");
        String tsId = sc.nextLine().toUpperCase().trim();

        System.out.print("Input Number of Tickets: ");
        int numOfTix = Integer.parseInt(sc.nextLine());

        List<String> availableSeats = getAvailableSeats(tsId);

        if (availableSeats.size() < numOfTix) {
            System.out.println("Not enough seats available.");
            return;
        }

        Main.header("Available Seats");
        for (String seat : availableSeats) System.out.print(seat + " ");
        System.out.println("\n----------------------------");

        List<String> selectedSeats = new ArrayList<>();

        while (selectedSeats.size() < numOfTix) {

            System.out.print("Choose Seat (" + (selectedSeats.size()+1) + "/" + numOfTix + "): ");
            String seatId = sc.nextLine().trim().toUpperCase();

            if (seatId.isEmpty()) {
                continue;
            }
            if (!availableSeats.contains(seatId)) {
                System.out.println("Invalid or unavailable seat. Try again.");
                continue;
            }
            if (selectedSeats.contains(seatId)) {
                System.out.println("Seat already selected.");
                continue;
            }

            selectedSeats.add(seatId);
        }

        int totalPrice = getTotalPrice(tsId, numOfTix);
        String status = "PENDING";
        LocalDate bookingDate = null;
        addBookingToDB(customerId, tsId, numOfTix, totalPrice, status, bookingDate, selectedSeats);
        System.out.println("Booking Complete!");
        sc.nextLine();
    }

    private static String generateBookingId() {
        String prefix = "BK";
        int nextId = 1;

        try {
            Connection conn = Main.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT booking_id FROM booking ORDER BY booking_id DESC LIMIT 1");

            if (rs.next()) {
                String lastId = rs.getString("booking_id");
                nextId = Integer.parseInt(lastId.substring(2)) + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format("%s%06d", prefix, nextId); // BK000011
    }

    private static void addBookingToDB(String customerId, String tsId, int numOfTix, int totalPrice,
                                       String status, LocalDate bookingDate, List<String> selectedSeats) {

        String bookingId = generateBookingId(); // generate ID first

        String query = "INSERT INTO booking (booking_id, customer_id, theater_show_id, nooftickets, total_price, " +
                "booking_status, booking_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, bookingId);
            pstmt.setString(2, customerId);
            pstmt.setString(3, tsId);
            pstmt.setInt(4, numOfTix);
            pstmt.setInt(5, totalPrice);
            pstmt.setString(6, status);
            if (bookingDate == null)
                pstmt.setNull(7, java.sql.Types.DATE);
            else
                pstmt.setDate(7, java.sql.Date.valueOf(bookingDate));

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Booking Added Successfully");
                addSeatBookings(bookingId, selectedSeats);
                System.out.println("Seats Booked Successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getAvailableSeats(String tsId) {

        String query = """
        SELECT s.seat_id
        FROM seat s
        JOIN theater_shows ts ON ts.theater_id = s.theater_id
        WHERE ts.theater_show_id = ?
        AND s.seat_id NOT IN (
            SELECT sb.seat_id
            FROM seat_booking sb
            JOIN booking b ON b.booking_id = sb.booking_id
            WHERE b.theater_show_id = ?
        )
        ORDER BY s.seat_id
        """;

        List<String> availableSeats = new ArrayList<>();

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tsId);
            pstmt.setString(2, tsId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                availableSeats.add(rs.getString("seat_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableSeats;
    }

    private static void addSeatBookings(String bookingId, List<String> seatIds) {

        String query = "INSERT INTO seat_booking (booking_id, seat_id) VALUES (?, ?)";

        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            for (String seatId : seatIds) {
                pstmt.setString(1, bookingId);
                pstmt.setString(2, seatId);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listAllPendingBookings(String customerId){
        String query = "SELECT booking_id, theater_show_id, nooftickets, total_price, booking_status " +
                "FROM booking " +
                "WHERE customer_id = ? AND booking_status = 'PENDING'";
        try {
            Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            System.out.println("\nPending Bookings for Customer " + customerId + ":");
            System.out.println("Booking ID | Show ID | Tickets | Total Price | Status");
            while (rs.next()) {
                found = true;
                System.out.printf("%s | %s | %d | P%d | %s\n",
                        rs.getString("booking_id"),
                        rs.getString("theater_show_id"),
                        rs.getInt("nooftickets"),
                        rs.getInt("total_price"),
                        rs.getString("booking_status"));
            }

            if (!found) {
                System.out.println("No pending bookings found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getPrice(String bookingId){
        int totalPrice = 0;
        try {
            Connection conn = Main.getConnection();
            String query = "SELECT total_price FROM booking WHERE booking_id = ? AND booking_status = 'PENDING'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPrice = rs.getInt("total_price");
            } else {
                System.out.println("Invalid booking ID or booking is not pending.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    private static void confirmBooking(String bookingId){
        try {
            Connection conn = Main.getConnection();
            String updateQuery = "UPDATE booking SET booking_status = 'CONFIRMED', booking_date = ? WHERE booking_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmt.setString(2, bookingId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Payment successful! Booking is now CONFIRMED.");
            } else {
                System.out.println("Failed to update booking.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void pay(Scanner scan){

        int amountPaid = 0;

        Main.header("Payment");
        System.out.print("Enter Customer ID: ");
        String customerId = scan.nextLine().toUpperCase().trim();

        listAllPendingBookings(customerId);

        System.out.println("Select Booking To Pay (Enter Booking ID): ");
        String bookingId = scan.nextLine().toUpperCase().trim();

        int totalPrice = getPrice(bookingId);

        do {
            System.out.print("Enter Amount Paid: ");
            amountPaid = Integer.parseInt(scan.nextLine());

            if (amountPaid < totalPrice) {
                System.out.println("Insufficient amount.");
            }
        } while (amountPaid < totalPrice);

        confirmBooking(bookingId);

    }

    public static void updateBooking(String bookingId) {
        String sql = "UPDATE booking SET BOOKING_STATUS = 'REFUNDED' WHERE BOOKING_ID = ?";

        try (Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Payment is now Refunded!");
            } else {
                System.out.println("\nBooking ID not updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSeatBooking(String bookingId) {
        String sql = "DELETE FROM seat_booking WHERE BOOKING_ID = ?";

        try (Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("\nSeats are now vacant!");
            } else {
                System.out.println("\nBooking ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getBookingId(String cusId) {
        String booking_id = "";
        String query = "SELECT BOOKING_ID FROM booking WHERE CUSTOMER_ID = ?";

        try (Connection conn = Main.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cusId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                booking_id = rs.getString("BOOKING_ID");
                System.out.println("Cancelled Booking ID: " + booking_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        deleteSeatBooking(booking_id);
        updateBooking(booking_id);
    }

    public static void cancelBooking(Scanner scan) {
        Main.header("Cancel Bookings");
        CustomerRecords.viewCustomerBooking();
        System.out.print("\nEnter Customer Id To Cancel: ");
        String customerId = scan.nextLine().trim().toUpperCase();

        if (getCustomerByID(customerId) == null){
            System.out.print("Customer Not Found.\n");
            Main.next(scan);
            return;
        }

        getBookingId(customerId);
        Main.next(scan);
    } 
}

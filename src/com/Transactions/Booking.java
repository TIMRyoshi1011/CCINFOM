package com.Transactions;

import java.sql.*;
import java.time.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.App;
import com.Records.*;

import model.Customer;

public class Booking {

    public static void addNewTheaterShow(){

        JFrame inputFrame = new JFrame("Add New Theater Show");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Input Show Id to Schedule:");
        JTextField showIdGet = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JFrame inputFrame2 = new JFrame("Add New Theater Show");
        inputFrame2.setSize(350, 120);
        inputFrame2.setLayout(new FlowLayout());
        inputFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame2.setResizable(false);

        JLabel label2 = new JLabel("Input Theater Id to Schedule:");
        JTextField theaterIdGet = new JTextField(20);
        JButton searchButton2 = new JButton("Search");

        searchButton.addActionListener(e -> {
            String showId = showIdGet.getText().toUpperCase().trim();

            if(ShowRecords.getShowById(showId) == null){
                JOptionPane.showMessageDialog(null, "Show Not Found");
                return;
            }

            TheaterRecords.listAllActiveTheaters();
            inputFrame.dispose();


            inputFrame2.add(label2);
            inputFrame2.add(theaterIdGet);
            inputFrame2.add(searchButton2);

            inputFrame2.setLocationRelativeTo(null);
            inputFrame2.setVisible(true);

            searchButton2.addActionListener(e2 -> {
                String theaterId = theaterIdGet.getText().toUpperCase().trim();

                if(TheaterRecords.getTheaterById(theaterId) == null){
                    JOptionPane.showMessageDialog(null, "Theater Not Found");
                    return;
                }

                inputFrame2.dispose();

                String inputDate = JOptionPane.showInputDialog("Enter Date To Schedule (YYYY-MM-DD):");
                LocalDate date = LocalDate.parse(inputDate);

                LocalTime runtime = Objects.requireNonNull(ShowRecords.getShowById(showId)).getShowRuntime();

                String inputTime = JOptionPane.showInputDialog("Enter Start Time (HH:MM:SS):");
                LocalTime startTime = LocalTime.parse(inputTime);

                Duration duration = Duration.ofHours(runtime.getHour())
                    .plusMinutes(runtime.getMinute())
                    .plusSeconds(runtime.getSecond());

                    LocalTime endTime = startTime.plus(duration);
                    String status = "SCHEDULED";

                    addTheaterShowToDB(theaterId, showId, startTime, endTime, date, status);
            });
        });

        inputFrame.add(label1);
        inputFrame.add(showIdGet);
        inputFrame.add(searchButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);

        ShowRecords.viewOngoingShows();
    }

    private static void addTheaterShowToDB(String theaterId, String showId, LocalTime startTime,
                                           LocalTime endTime, LocalDate date, String status){

        String query = "INSERT INTO shows (theater_id, show_id, start_time, end_time, reservation_date, show_status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dao.SQLConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, theaterId);
            pstmt.setString(2, showId);
            pstmt.setTime(3, java.sql.Time.valueOf(startTime));
            pstmt.setTime(4, java.sql.Time.valueOf(endTime));
            pstmt.setDate(5, java.sql.Date.valueOf(date));
            pstmt.setString(6, status);

            int rows = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rows > 0 ? "Theater Show added successfully!" : "No rows inserted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Customer getCustomerByID(String customerId){
        String query = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
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
        String query = "SELECT t.theater_show_id, s.title, t.start_time, s.show_price, t.reservation_date " +
                        "FROM theater_shows t JOIN shows s ON s.show_id = t.show_id " +
                        "WHERE show_status like 'SCHEDULED'";
        connectTable(query);
    }

    // private static void displayTheaterShow(ResultSet rs) throws SQLException{
    //     String tsID = rs.getString("theater_show_id");
    //     String title = rs.getString("title");
    //     LocalTime startTime = LocalTime.parse(rs.getString("start_time"));
    //     int price = rs.getInt("show_price");
    //     LocalDate date = LocalDate.parse(rs.getString("reservation_date"));

    //     System.out.println("Theater Show ID: " + tsID + " | Title: " + title);
    //     System.out.println("Price: P" + price + " | Start Time: " + startTime);
    //     System.out.println("Date: " + date);
    //     System.out.println("-------------------------------------------------------");
    // }

    private static int getTotalPrice(String theater_show_id, int tix){
        String query = "SELECT  s.show_price " +
                "FROM theater_shows t JOIN shows s ON s.show_id = t.show_id " +
                "WHERE theater_show_id = ?";

        int price = -1;
        try{
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theater_show_id);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;

            // Main.header("Scheduled Theater Shows");
            while (rs.next()){
                found = true;
                price = rs.getInt("show_price");
            }

            if(!found){
                JOptionPane.showMessageDialog(null, "Show Not Found.");
                return -1;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return price * tix;
    }

    public static void bookShowTickets(){
        CustomerRecords.listAllCustomers();
        String customerId = JOptionPane.showInputDialog(null, "Enter Customer Id:");

        if (getCustomerByID(customerId) == null){
            JOptionPane.showMessageDialog(null, "Customer Not Found.");
            return;
        }

        viewAllScheduledTheaterShows();
        String tsId = JOptionPane.showInputDialog(null, "Enter Theater Show ID to Book:");

        int numOfTix = Integer.parseInt(JOptionPane.showInputDialog(null, "Input Number of Tickets:"));

        List<String> availableSeats = getAvailableSeats(tsId);

        if (availableSeats.size() < numOfTix) {
            JOptionPane.showMessageDialog(null, "Not enough seats available.");
            return;
        }

        String seats = "";

        int ctr = 0;

        for (String seat : availableSeats) {
            seats = seats + seat + " | ";
            if (ctr == 8) {
                seats = seats + "\n";
                ctr = 0;
            }
            else
                ctr++;
        }

        JFrame seatFrame = new JFrame("Available Seats");
        seatFrame.setSize(750, 300);
        JLabel seatLabel = new JLabel("<html><pre>" + seats + "</pre></html>");
        seatFrame.add(seatLabel);
        seatFrame.setLocationRelativeTo(null);
        seatFrame.setVisible(true);

        List<String> selectedSeats = new ArrayList<>();

        while (selectedSeats.size() < numOfTix) {
            String seatId = JOptionPane.showInputDialog(null, "Choose Seat (" + (selectedSeats.size()+1) + "/" + numOfTix + "):");

            if (seatId.isEmpty()) {
                continue;
            }
            if (!availableSeats.contains(seatId)) {
                JOptionPane.showMessageDialog(null, "Invalid or unavailable seat. Try again.");
                continue;
            }
            if (selectedSeats.contains(seatId)) {
                JOptionPane.showMessageDialog(null, "Seat already selected.");
                continue;
            }
            selectedSeats.add(seatId);
        }

        int totalPrice = getTotalPrice(tsId, numOfTix);
        String status = "PENDING";
        LocalDate bookingDate = null;
        addBookingToDB(customerId, tsId, numOfTix, totalPrice, status, bookingDate, selectedSeats);
        JOptionPane.showMessageDialog(null, "Booking Complete!");
    }

    private static String generateBookingId() {
        String prefix = "BK";
        int nextId = 1;

        try {
            Connection conn = dao.SQLConnect.getConnection();
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
            Connection conn = dao.SQLConnect.getConnection();
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
                JOptionPane.showMessageDialog(null, "Booking Added Successfully");
                addSeatBookings(bookingId, selectedSeats);
                JOptionPane.showMessageDialog(null, "Seats Booked Successfully");
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
            Connection conn = dao.SQLConnect.getConnection();
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
            Connection conn = dao.SQLConnect.getConnection();
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
        connectTableifUnknown(query, customerId, null);
    }

    private static int getPrice(String bookingId){
        int totalPrice = 0;
        try {
            Connection conn = dao.SQLConnect.getConnection();
            String query = "SELECT total_price FROM booking WHERE booking_id = ? AND booking_status = 'PENDING'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPrice = rs.getInt("total_price");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid booking ID or booking is not pending.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }

    private static void confirmBooking(String bookingId){
        try {
            Connection conn = dao.SQLConnect.getConnection();
            String updateQuery = "UPDATE booking SET booking_status = 'CONFIRMED', booking_date = ? WHERE booking_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmt.setString(2, bookingId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Payment successful! Booking is now CONFIRMED.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update booking.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void pay(){

        int amountPaid = 0;

        CustomerRecords.listAllCustomers();
        
        String customerId = JOptionPane.showInputDialog(null, "Enter Customer ID:");

        listAllPendingBookings(customerId);

        String bookingId = JOptionPane.showInputDialog(null, "Select Booking To Pay (Enter Booking ID):");

        int totalPrice = getPrice(bookingId);

        do {
            amountPaid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Amount Paid:"));

            if (amountPaid < totalPrice) {
                JOptionPane.showMessageDialog(null, "Insufficient amount.");
            }
        } while (amountPaid < totalPrice);

        confirmBooking(bookingId);
    }

    public static void updateBooking(String bookingId) {
        String sql = "UPDATE booking SET BOOKING_STATUS = 'REFUNDED' WHERE BOOKING_ID = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Payment is now Refunded!");
            } else {
                JOptionPane.showMessageDialog(null, "Booking ID not updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSeatBooking(String bookingId) {
        String sql = "DELETE FROM seat_booking WHERE BOOKING_ID = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Seats are now vacant!");
            } else {
                JOptionPane.showMessageDialog(null, "Booking ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getBookingId(String cusId) {
        String booking_id = "";
        String query = "SELECT BOOKING_ID FROM booking WHERE CUSTOMER_ID = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cusId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                booking_id = rs.getString("BOOKING_ID");
                JOptionPane.showMessageDialog(null, "Cancelled Booking ID: " + booking_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        deleteSeatBooking(booking_id);
        updateBooking(booking_id);
    }

    public static void cancelBooking() {
        CustomerRecords.viewCustomerBooking();
        String customerId = JOptionPane.showInputDialog(null, "Enter Customer ID To Cancel:");

        if (getCustomerByID(customerId) == null){
            JOptionPane.showMessageDialog(null, "Customer Not Found.");
            return;
        }

        getBookingId(customerId);
    }

    private static void connectTable(String query) {
        try {
            Connection conn = dao.SQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Get metadata for column names
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }

            // Read rows into DefaultTableModel
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }

            // Display in JTable
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            App.addTable(scrollPane);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connectTableifUnknown(String query, String missing, String missing2) {
        try (
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, missing);
            if (missing2 != null) {
                pstmt.setString(2, missing2);
            }

            try (ResultSet rs = pstmt.executeQuery()) {

                // Check if ResultSet has data
                if (!rs.isBeforeFirst()) { // true if ResultSet is empty
                    JOptionPane.showMessageDialog(null, "No records found for that period.");
                    return;
                }

                // Get metadata for column names
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                String[] columnNames = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = meta.getColumnLabel(i + 1);
                }

                // Create table model (read-only)
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                // Populate table model
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        rowData[i] = rs.getObject(i + 1);
                    }
                    model.addRow(rowData);
                }

                // Display in JTable
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);

                App.addTable(scrollPane);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

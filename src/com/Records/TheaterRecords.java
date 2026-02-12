package com.Records;

import java.sql.*;
import javax.swing.*;
import java.awt.*;

import model.Theater;

public class TheaterRecords {
    static String getTheaterId = "";
    static String getTheaterName = "";
    static Integer getRows = 0;
    static Integer getCols = 0;
    static String getTheaterStatus = "";

    //Add Theater Records
    public static void addTheaterRecord() {
        JFrame inputFrame = new JFrame("Add Theater");
        inputFrame.setSize(350, 150);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Theater Name:");
        JTextField name = new JTextField(20);
        JLabel label2 = new JLabel("Maximum Rows:");
        Integer[] maxRows = {1, 2, 3, 4, 5};
        JComboBox<Integer> rows = new JComboBox<>(maxRows);
        JLabel label3 = new JLabel("Maximum Columns:");
        Integer[] maxCols = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        JComboBox<Integer> cols = new JComboBox<>(maxCols);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {

            inputFrame.dispose();

            getTheaterName = name.getText();
            getRows = (Integer)rows.getSelectedItem();
            getCols = (Integer)cols.getSelectedItem();

            String theater_Status;
            int capacity;

            capacity = getRows * getCols;

            theater_Status = "ACTIVE";

            addTheaterToDB(getTheaterName, capacity, getRows, getCols, theater_Status);
        });

        inputFrame.add(label1);
        inputFrame.add(name);
        inputFrame.add(label2);
        inputFrame.add(rows);
        inputFrame.add(label3);
        inputFrame.add(cols);
        inputFrame.add(submitButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);        
    }

    public static String generateTheaterID() {
        String query = "SELECT THEATER_ID FROM theaters ORDER BY THEATER_ID DESC LIMIT 1";
        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("THEATER_ID"); // e.g., TH000005
                int num = Integer.parseInt(lastId.substring(2)) + 1;
                return String.format("TH%06d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "TH000001"; // default if table is empty
    }

    //Adds Theater Record to Database
    public static void addTheaterToDB(String theater_Name, int capacity, int max_Rows, int max_Cols, String theater_Status) {
        // Generate Theater ID manually
        String theaterId = generateTheaterID(); // e.g., TH000001, TH000002, etc.

        String query = "INSERT INTO theaters (THEATER_ID, THEATER_NAME, CAPACITY, MAX_ROWS, MAX_COLS, THEATER_STATUS) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, theaterId);
            pstmt.setString(2, theater_Name);
            pstmt.setInt(3, capacity);
            pstmt.setInt(4, max_Rows);
            pstmt.setInt(5, max_Cols);
            pstmt.setString(6, theater_Status);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Theater added successfully!");
                // Adds Seats to Database
                addSeats(theaterId, max_Rows, max_Cols);
                JOptionPane.showMessageDialog(null, "Seats added successfully for this theater!");
            } else {
                JOptionPane.showMessageDialog(null, "No rows inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSeats(String theaterId,int maxRow,int maxCol){
        String query = "INSERT INTO seat (theater_id, row_no, col_no) VALUES (?, ?, ?)";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            for (int row = 1; row <= maxRow; row++) {
                for (int col = 1; col <= maxCol; col++) {

                    pstmt.setString(1, theaterId);
                    pstmt.setInt(2, row);
                    pstmt.setInt(3, col);

                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Updates Theater Name 
    public static void updateTheaterRecord () {
        JFrame inputFrame = new JFrame("Update Theater");
        inputFrame.setSize(300, 255);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Theater ID to update: ");
        JTextField theaterID = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JLabel mainLabel = new JLabel("Leave blank if no update");
        mainLabel.setFont(new Font("Segoe UI", 0, 24));

        JLabel label2 = new JLabel("Enter New Theater Name:");
        JTextField name = new JTextField(20);
        JLabel label3 = new JLabel("Select New Theater Status:");
        String[] theaterStatus = {"ACTIVE", "INACTIVE"};
        JComboBox<String> status = new JComboBox<>(theaterStatus);
        JButton submitButton = new JButton("Submit");

        inputFrame.add(label1);
        inputFrame.add(theaterID);
        inputFrame.add(searchButton);

        searchButton.addActionListener(e -> {
            inputFrame.add(mainLabel);
            inputFrame.add(label2);
            inputFrame.add(name);
            inputFrame.add(label3);
            inputFrame.add(status);
            inputFrame.add(submitButton);
            inputFrame.revalidate();
            inputFrame.repaint();

            submitButton.addActionListener(f -> {
                inputFrame.dispose();

                getTheaterId = theaterID.getText();
                getTheaterName = name.getText();
                getTheaterStatus = (String)status.getSelectedItem();

                updateTheaterInDB(getTheaterId, getTheaterName, getTheaterStatus);
            });
        });

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    //Updates Theater Record in Database
    public static void updateTheaterInDB(String theater_ID, String theater_Name, String theater_Status) {
        StringBuilder query = new StringBuilder("UPDATE theaters SET ");
        boolean hasUpdate = false;

        try {
            Connection conn = dao.SQLConnect.getConnection();

            if (!theater_Name.isEmpty()) {
                query.append("THEATER_NAME = ?, ");
                hasUpdate = true;
            }
            if (!theater_Status.isEmpty()) {
                query.append("THEATER_STATUS = ?, ");
                hasUpdate = true;
            }
            if (!hasUpdate) {
                JOptionPane.showMessageDialog(null, "No updates provided.");
                return;
            }

            query.setLength(query.length() - 2); //Removes last comma and space
            query.append(" WHERE THEATER_ID = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!theater_Name.isEmpty()) {
                pstmt.setString(paramIndex++, theater_Name);
            }
            if (!theater_Status.isEmpty()) {
                pstmt.setString(paramIndex++, theater_Status);
            }

            pstmt.setString(paramIndex, theater_ID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Theater Details updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Theater ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Delete Theater Records 
    public static void deleteTheaterRecord () {
        JFrame inputFrame = new JFrame("Delete Theater");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Theater ID to delete: ");
        JTextField theaterID = new JTextField(20);
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {

            inputFrame.dispose();

            getTheaterId = theaterID.getText();

            deleteTheaterInDB(getTheaterId);
        });

        inputFrame.add(label1);
        inputFrame.add(theaterID);
        inputFrame.add(deleteButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void deleteTheaterInDB(String theater_ID) {
        String query = "DELETE FROM theaters WHERE THEATER_ID = ?";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, theater_ID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Theater deleted successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Theater not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //View Theater Record by Status 
    public static void viewTheaterRecordByStatus () {
        JFrame inputFrame = new JFrame("View Theater By Status");
        inputFrame.setSize(350, 100);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Select Status to Search: ");
        JButton active = new JButton("ACTIVE");
        JButton inactive = new JButton("INACTIVE");

        active.addActionListener(e -> {
            inputFrame.dispose();
            String status = "ACTIVE";
            viewStatusDB(status);
        });

        inactive.addActionListener(e -> {
            inputFrame.dispose();
            String status = "INACTIVE";
            viewStatusDB(status);
        });

        inputFrame.add(label1);
        inputFrame.add(active);
        inputFrame.add(inactive);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void viewStatusDB(String reservation_Status) {
        String query = "SELECT * FROM theaters WHERE THEATER_STATUS = '?'";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, reservation_Status);
            ResultSet rs = pstmt.executeQuery();

            // System.out.println("\n-----------------------Theater by Status: " + reservation_Status + "-----------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                displayTheaterRecord(rs);
            }

            if(!found) {
                JOptionPane.showMessageDialog(null, "No theater record exists with the " + reservation_Status + " status");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //List All Theaters 
    public static void listAllTheaters () {
        String query = "SELECT * FROM theaters";
        connectTable(query);
    }

    // List all active theaters
    public static void listAllActiveTheaters() {
        String query = "SELECT * FROM theaters WHERE THEATER_STATUS = 'ACTIVE'";
        connectTable(query);
    }

    //Theater to be used by a show
    public static void viewTheaterUsedByShow () {
        JFrame inputFrame = new JFrame("View Theater Used By Show");
        inputFrame.setSize(350, 110);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Show ID: ");
        JTextField searchId = new JTextField(20);
        JButton search = new JButton("Search");

        search.addActionListener(e -> {
            inputFrame.dispose();
            getTheaterId = searchId.getText();
            
        String query = "SELECT sh.SHOW_ID, sh.TITLE, th.THEATER_NAME" +
            "FROM theaters th" +
            "LEFT JOIN theater_shows ts ON th.THEATER_ID = ts.THEATER_ID" +
            "LEFT JOIN shows sh ON ts.SHOW_ID = sh.SHOW_ID" +
            "WHERE sh.SHOW_ID = '?'";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, getTheaterId);
            ResultSet rs = pstmt.executeQuery();

            // System.out.println("\n-----------------------Theater to be used by a Show-----------------------");
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    found = true;
                    displayTheaterRecord(rs);
                }
                
                String show_Title = rs.getString("TITLE");
                if (show_Title != null) {
                    String theater_Name = rs.getString("THEATER_NAME");
                    JOptionPane.showMessageDialog(null, "Show Title: " + show_Title + "Theater Name: " + theater_Name);
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(null, "Show ID not found.");
            }

        } catch (SQLException f) {
            f.printStackTrace();
        }
        });

        inputFrame.add(label1);
        inputFrame.add(searchId);
        inputFrame.add(search);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static Theater getTheaterById(String theaterId) {
        String query = "SELECT * FROM theaters WHERE theater_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, theaterId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Theater(
                        rs.getString("theater_name"),
                        rs.getInt("capacity"),
                        rs.getInt("max_rows"),
                        rs.getInt("max_cols"),
                        rs.getString("theater_status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //Helper method to display theater record
    private static void displayTheaterRecord (ResultSet rs) throws SQLException {
        String theater_ID = rs.getString("THEATER_ID");
        String theater_Name = rs.getString("THEATER_NAME");
        int capacity = rs.getInt("CAPACITY");
        int max_Rows = rs.getInt("MAX_ROWS");
        int max_Cols = rs.getInt("MAX_COLS");
        String theater_Status = rs.getString("THEATER_STATUS");

        JOptionPane.showMessageDialog(null, "Theater Records\n" + "Theater ID: " + theater_ID + " | Theater Name: " + theater_Name + " | Capacity: " + capacity + 
                            " | Max Rows: " + max_Rows + " | Max Columns: " + max_Cols + " | Theater Status: " + theater_Status);
    }

    private static void connectTable(String query) {
        try (
            Connection conn = dao.SQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            TableDisplay frame = new TableDisplay();
            frame.displayResultSet(rs);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.Records;

import java.sql.*;
import java.time.LocalTime;
import javax.swing.*;
import java.awt.*;

import model.Show;

public class ShowRecords {
    static String getShowId = "";
    static String getShowName = "";
    static LocalTime getRuntime;
    static Integer getPrice = 0;
    static String getStatus = "";

    public static void addShowToDB(String title, LocalTime runTime, Integer price, String status){

        String query = "INSERT INTO shows (title, runtime, show_price, status) "
                + "VALUES (?, ?, ?, ?)";

        String time = runTime.toString();
        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, title);
            pstmt.setString(2, time);
            pstmt.setInt(3, price);
            pstmt.setString(4, status);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Show added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void enterShowDetails() {
        JFrame inputFrame = new JFrame("Add Show");
        inputFrame.setSize(350, 250);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Show Title:");
        JTextField title = new JTextField(20);
        JLabel label2 = new JLabel("Enter Runtime:");
        JTextField time = new JTextField("HH:MM:SS", 20);
        JLabel label3 = new JLabel("Enter Price:");
        JTextField price = new JTextField(20);
        JLabel label4 = new JLabel("Select Status:");
        String[] showStatus = {"UPCOMING", "ONGOING", "COMPLETED"};
        JComboBox<String> status = new JComboBox<>(showStatus);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {

            inputFrame.dispose();

            getShowName = title.getText();
            getRuntime = LocalTime.parse(time.getText());
            getPrice = Integer.parseInt(price.getText());
            getStatus = (String)status.getSelectedItem();

            addShowToDB(getShowName, getRuntime, getPrice, getStatus);
        });

        inputFrame.add(label1);
        inputFrame.add(title);
        inputFrame.add(label2);
        inputFrame.add(time);
        inputFrame.add(label3);
        inputFrame.add(price);
        inputFrame.add(label4);
        inputFrame.add(status);
        inputFrame.add(submitButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static Show getShowById(String showId) {
        String query = "SELECT * FROM shows WHERE show_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, showId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Show(
                        rs.getString("title"),
                        rs.getTime("runtime").toLocalTime(),
                        rs.getInt("show_price"),
                        rs.getString("status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateShowInDB(String showId, String newTitle,
                                      LocalTime runtime, int price, String status) {

        StringBuilder sql = new StringBuilder("UPDATE customers SET ");
        String time = runtime.toString();
        String pr = Integer.toString(price);

        if (!newTitle.isEmpty()) {
            sql.append("title = ?");
        }

        if (!time.isEmpty()) {
            sql.append("runtime = ?");
        }

        if (!pr.isEmpty()) {
            sql.append("show_price = ?");
        }

        if (!status.isEmpty()) {
            sql.append("title = ?");
        }

        sql.append(" WHERE customer_id = ?");

        if (newTitle.isEmpty() && time.isEmpty() && pr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No fields to update.");
            return;
        }

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (!newTitle.isEmpty()) {
                pstmt.setString(paramIndex++, newTitle);
            }
            if (!time.isEmpty()) {
                pstmt.setString(paramIndex++, time);
            }
            if (!pr.isEmpty()) {
                pstmt.setString(paramIndex++, pr);
            }
            if (!status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            pstmt.setString(paramIndex, showId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Show updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Show ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query = "UPDATE shows SET title = ?, runtime = ?, "
                + "show_price = ?, status = ? WHERE show_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newTitle);
            pstmt.setTime(2, java.sql.Time.valueOf(runtime));
            pstmt.setInt(3, price);
            pstmt.setString(4, status);
            pstmt.setString(5, showId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Show updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Show not found or no changes made.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating show.");
        }
    }

    public static void updateShowDetails() {
        JFrame inputFrame = new JFrame("Update Show");
        inputFrame.setSize(350, 300);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Show ID to update: ");
        JTextField showID = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JLabel mainLabel = new JLabel("Leave blank if no update");
        mainLabel.setFont(new Font("Segoe UI", 0, 24));

        JLabel label2 = new JLabel("Enter NEW Show Title:");
        JTextField title = new JTextField(20);
        JLabel label3 = new JLabel("Enter NEW Runtime:");
        JTextField time = new JTextField("HH:MM:SS", 20);
        JLabel label4 = new JLabel("Enter NEW Price:");
        JTextField price = new JTextField(20);
        JLabel label5 = new JLabel("Select NEW Status:");
        String[] showStatus = {"UPCOMING", "ONGOING", "COMPLETED"};
        JComboBox<String> status = new JComboBox<>(showStatus);
        JButton submitButton = new JButton("Submit");

        inputFrame.add(label1);
        inputFrame.add(showID);
        inputFrame.add(searchButton);

        searchButton.addActionListener(e -> {
            inputFrame.add(mainLabel);
            inputFrame.add(label2);
            inputFrame.add(title);
            inputFrame.add(label3);
            inputFrame.add(time);
            inputFrame.add(label4);
            inputFrame.add(price);
            inputFrame.add(label5);
            inputFrame.add(status);
            inputFrame.add(submitButton);
            inputFrame.revalidate();
            inputFrame.repaint();

            submitButton.addActionListener(f -> {
                inputFrame.dispose();

                getShowId = showID.getText();
                getShowName = title.getText();
                getRuntime = LocalTime.parse(time.getText());
                getPrice = Integer.parseInt(price.getText());
                getStatus = (String)status.getSelectedItem();

                updateShowInDB(getShowId, getShowName, getRuntime, getPrice, getStatus);
            });
        });

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void deleteShow(){
        JFrame inputFrame = new JFrame("Delete Show");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Show ID to delete: ");
        JTextField showID = new JTextField(20);
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {

            inputFrame.dispose();

            getShowId = showID.getText();

            deleteShowInDB(getShowId);
        });

        inputFrame.add(label1);
        inputFrame.add(showID);
        inputFrame.add(deleteButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void deleteShowInDB(String showId) {
        String query = "DELETE FROM shows WHERE show_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, showId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Show deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Show ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayShowRecord(ResultSet rs) throws SQLException {
        String showId = rs.getString("show_id");
        String title = rs.getString("title");
        LocalTime runtime = LocalTime.parse(rs.getString("runtime"));
        String price = rs.getString("show_price");
        String status = rs.getString("STATUS");

        // System.out.println("Show ID: " + showId + " | Title: " + title);
        // System.out.println("Price: P" + price + " | Status: " + status);
        // System.out.println("Runtime: " + runtime);
        // System.out.println("-------------------------------------------------------");

        JOptionPane.showMessageDialog(
            null,                       
            "Show ID: " + showId + "\nTitle: " + title + "\nPrice: P" + price + "\nStatus: " + status + "\nRuntime: " + runtime, 
            "Record",                     
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void viewAllShows(){
        String query = "SELECT * FROM shows";
        connectTable(query);
    }

    public static void viewShowDetails(){

        String ID = JOptionPane.showInputDialog("Enter Show ID of Show");

        String query = "SELECT * FROM shows WHERE show_id = ?";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ID); 
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            //System.out.println("-----Details of " + ID + " ---------");
            while(rs.next()) {
                found = true;
                displayShowRecord(rs);
            }
            if(!found){
                JOptionPane.showMessageDialog(null, "Show not Found.");
            }
        } catch (Exception e){
             e.printStackTrace();
        }
    }

    public static void viewUpcomingShows(){
        String query = "SELECT * FROM shows WHERE status like 'UPCOMING'";
        connectTable(query);
    }

    public static void viewOngoingShows(){
        String query = "SELECT * FROM shows WHERE status like 'ONGOING'";
        connectTable(query);
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

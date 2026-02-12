package com.Records;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerRecords {
    static String getCusId = "";
    static String getFName = "";
    static String getLName = "";
    static String getPhoneNo = "";
    static String getEmailAdd = "";

    public static void AddCustomer() {
        JFrame inputFrame = new JFrame("Add Customer");
        inputFrame.setSize(350, 250);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter First Name:");
        JTextField fName = new JTextField(20);
        JLabel label2 = new JLabel("Enter Last Name:");
        JTextField lName = new JTextField(20);
        JLabel label3 = new JLabel("Enter Phone Number:");
        JTextField phoneNo = new JTextField(20);
        JLabel label4 = new JLabel("Enter Email Address:");
        JTextField emailAdd = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {

            inputFrame.dispose();

            getFName = fName.getText();
            getLName = lName.getText();
            getPhoneNo = phoneNo.getText();
            getEmailAdd = emailAdd.getText();

            addCustomerToDB(getFName, getLName, getPhoneNo, getEmailAdd);
        });

        inputFrame.add(label1);
        inputFrame.add(fName);
        inputFrame.add(label2);
        inputFrame.add(lName);
        inputFrame.add(label3);
        inputFrame.add(phoneNo);
        inputFrame.add(label4);
        inputFrame.add(emailAdd);
        inputFrame.add(submitButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void addCustomerToDB(String fName, String lName, String phoneNo, String emailAdd) {
        String query = "INSERT INTO customers (FIRST_NAME, LAST_NAME, PHONE_NUMBER, EMAIL_ADDRESS) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fName);
            pstmt.setString(2, lName);
            pstmt.setString(3, phoneNo);
            pstmt.setString(4, emailAdd);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Customer added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomer() {
        JFrame inputFrame = new JFrame("Update Customer");
        inputFrame.setSize(370, 300);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Customer ID to update: ");
        JTextField cusID = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JLabel mainLabel = new JLabel("Leave blank if no update");
        mainLabel.setFont(new Font("Segoe UI", 0, 24));

        JLabel label2 = new JLabel("Enter New First Name:");
        JTextField fName = new JTextField(20);
        JLabel label3 = new JLabel("Enter New Last Name:");
        JTextField lName = new JTextField(20);
        JLabel label4 = new JLabel("Enter New Phone Number:");
        JTextField phoneNo = new JTextField(20);
        JLabel label5 = new JLabel("Enter Email Address:");
        JTextField emailAdd = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        inputFrame.add(label1);
        inputFrame.add(cusID);
        inputFrame.add(searchButton);

        searchButton.addActionListener(e -> {
            inputFrame.add(mainLabel);
            inputFrame.add(label2);
            inputFrame.add(fName);
            inputFrame.add(label3);
            inputFrame.add(lName);
            inputFrame.add(label4);
            inputFrame.add(phoneNo);
            inputFrame.add(label5);
            inputFrame.add(emailAdd);
            inputFrame.add(submitButton);
            inputFrame.revalidate();
            inputFrame.repaint();

            submitButton.addActionListener(f -> {
                inputFrame.dispose();

                getCusId = cusID.getText();
                getFName = fName.getText();
                getLName = lName.getText();
                getPhoneNo = phoneNo.getText();
                getEmailAdd = emailAdd.getText();

                updateCustomerInDB(getCusId, getFName, getLName, getPhoneNo, getEmailAdd);
            });
        });

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
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
            JOptionPane.showMessageDialog(null, "No fields to update.");
            return;
        }

        try (Connection conn = dao.SQLConnect.getConnection();
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
                JOptionPane.showMessageDialog(null, "Customer updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Customer ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCustomer() {
        JFrame inputFrame = new JFrame("Delete Customer");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Customer ID to delete: ");
        JTextField cusID = new JTextField(20);
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {

            inputFrame.dispose();

            getCusId = cusID.getText();

            deleteCustomerInDB(getCusId);
        });

        inputFrame.add(label1);
        inputFrame.add(cusID);
        inputFrame.add(deleteButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void deleteCustomerInDB(String cusId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cusId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Customer deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Customer ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewCustomerByLName() {
        String query = "SELECT LAST_NAME, FIRST_NAME FROM customers ORDER BY LAST_NAME ASC";
        connectTable(query);
    }

    public static void listAllCustomerNames() {
        String query = "SELECT FIRST_NAME, LAST_NAME FROM customers";
        connectTable(query);
    }

    public static void listAllCustomers() {
        String query = "SELECT * FROM customers";
        connectTable(query);
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

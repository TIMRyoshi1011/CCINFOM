package com.Records;

import com.App;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;

import java.util.List;
import java.util.ArrayList;

public class StaffRecords {
    static String getStaffId = "";
    static String getFName = "";
    static String getLName = "";
    static String getPos = "";
    static String getStatus = "";
    static Integer getSalary = 0;

    // Add new staff
    public static void enterStaffDetails() {
        JFrame inputFrame = new JFrame("Add Staff");
        inputFrame.setSize(320, 250);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter First Name:");
        JTextField fName = new JTextField(20);
        JLabel label2 = new JLabel("Enter Last Name:");
        JTextField lName = new JTextField(20);
        JLabel label3 = new JLabel("Enter Position:");
        JTextField position = new JTextField(20);
        JLabel label4 = new JLabel("Enter Employment Status:");
        String[] staffStatus = {"ACTIVE", "INACTIVE"};
        JComboBox<String> status = new JComboBox<>(staffStatus);
        JLabel label5 = new JLabel("Enter Salary:");
        JTextField salary = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {

            inputFrame.dispose();

            getFName = fName.getText();
            getLName = lName.getText();
            getPos = position.getText();
            getStatus = (String)status.getSelectedItem();
            getSalary = Integer.parseInt(salary.getText());

            addStaffToDB(getFName, getLName, getPos, getStatus, getSalary);
        });

        inputFrame.add(label1);
        inputFrame.add(fName);
        inputFrame.add(label2);
        inputFrame.add(lName);
        inputFrame.add(label3);
        inputFrame.add(position);
        inputFrame.add(label4);
        inputFrame.add(status);
        inputFrame.add(label5);
        inputFrame.add(salary);
        inputFrame.add(submitButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    // private static String centerText(String text, int width) {
    //     if (text == null) text = "";
    //     if (text.length() >= width) return text.substring(0, width);
    //     int totalPadding = width - text.length();
    //     int left = totalPadding / 2;
    //     int right = totalPadding - left;
    //     return " ".repeat(left) + text + " ".repeat(right);
    // }

    public static void addStaffToDB(String firstName, String lastName, String position, String status,
            int salary) {
        String query = "INSERT INTO staff (FIRST_NAME, LAST_NAME, POSITION, EMPLOYMENT_STATUS, SALARY) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, position);
            pstmt.setString(4, status);
            pstmt.setInt(5, salary);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Staff member added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update staff details
    public static void updateStaffDetails() {
        JFrame inputFrame = new JFrame("Update Staff");
        inputFrame.setSize(275, 460);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Staff ID to update: ");
        JTextField staffID = new JTextField(20);
        JButton searchButton = new JButton("Search");

        JLabel mainLabel = new JLabel("Leave blank if no update");
        mainLabel.setFont(new Font("Segoe UI", 0, 24));

        JLabel label2 = new JLabel("Enter New First Name:");
        JTextField fName = new JTextField(20);
        JLabel label3 = new JLabel("Enter New Last Name:");
        JTextField lName = new JTextField(20);
        JLabel label4 = new JLabel("Enter New Position:");
        JTextField position = new JTextField(20);
        JLabel label5 = new JLabel("Select Status:");
        String[] staffStatus = {"ACTIVE", "INACTIVE"};
        JComboBox<String> status = new JComboBox<>(staffStatus);
        JLabel label6 = new JLabel("Enter New Salary:");
        JTextField salary = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        inputFrame.add(label1);
        inputFrame.add(staffID);
        inputFrame.add(searchButton);

        searchButton.addActionListener(e -> {
            inputFrame.add(mainLabel);
            inputFrame.add(label2);
            inputFrame.add(fName);
            inputFrame.add(label3);
            inputFrame.add(lName);
            inputFrame.add(label4);
            inputFrame.add(position);
            inputFrame.add(label5);
            inputFrame.add(status);
            inputFrame.add(label6);
            inputFrame.add(salary);
            inputFrame.add(submitButton);
            inputFrame.revalidate();
            inputFrame.repaint();

            submitButton.addActionListener(f -> {
                inputFrame.dispose();

                getStaffId = staffID.getText();
                getFName = fName.getText();
                getLName = lName.getText();
                getPos = position.getText();
                getStatus = (String)status.getSelectedItem();
                getSalary = Integer.parseInt(salary.getText());

                updateStaffInDB(getStaffId, getFName, getLName, getPos, getStatus, getSalary);
            });
        });

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void updateStaffInDB(String staffId, String firstName, String lastName, String position,
            String status,
            Integer salary) {
        StringBuilder query = new StringBuilder("UPDATE staff SET ");
        boolean hasUpdate = false;

        try {
            Connection conn = dao.SQLConnect.getConnection();

            if (!firstName.isEmpty()) {
                query.append("FIRST_NAME = ?, ");
                hasUpdate = true;
            }
            if (!lastName.isEmpty()) {
                query.append("LAST_NAME = ?, ");
                hasUpdate = true;
            }
            if (!position.isEmpty()) {
                query.append("POSITION = ?, ");
                hasUpdate = true;
            }
            if (!status.isEmpty()) {
                query.append("EMPLOYMENT_STATUS = ?, ");
                hasUpdate = true;
            }
            if (salary != null) {
                query.append("SALARY = ?, ");
                hasUpdate = true;
            }

            if (!hasUpdate) {
                JOptionPane.showMessageDialog(null, "No updates provided.");
                return;
            }

            query.setLength(query.length() - 2);
            query.append(" WHERE STAFF_ID = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!firstName.isEmpty())
                pstmt.setString(paramIndex++, firstName);
            if (!lastName.isEmpty())
                pstmt.setString(paramIndex++, lastName);
            if (!position.isEmpty())
                pstmt.setString(paramIndex++, position);
            if (!status.isEmpty())
                pstmt.setString(paramIndex++, status);
            if (salary != null)
                pstmt.setInt(paramIndex++, salary);

            pstmt.setString(paramIndex, staffId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Staff details updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null,"Staff ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete staff record
    public static void deleteStaff() {
        JFrame inputFrame = new JFrame("Delete Staff");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Staff ID to delete: ");
        JTextField staffID = new JTextField(20);
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {

            inputFrame.dispose();

            getStaffId = staffID.getText();

            deleteStaffInDB(getStaffId);
        });

        inputFrame.add(label1);
        inputFrame.add(staffID);
        inputFrame.add(deleteButton);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    public static void deleteStaffInDB(String staffId) {
        // // Check if Staff ID exists
        // String checkQuery = "SELECT 1 FROM staff WHERE STAFF_ID = ?";
        // try (Connection conn = Main.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
        //     checkStmt.setString(1, staffId);
        //     try (ResultSet rs = checkStmt.executeQuery()) {
        //         if (!rs.next()) {
        //             System.out.println("\nError: Staff ID not found.");
        //             return; // Exit the feature if Staff ID does not exist
        //         }
        //     }
        // } catch (SQLException e) {
        //     System.out.println("\nError checking Staff ID: " + e.getMessage());
        //     return;
        // }

        String query = "DELETE FROM staff WHERE STAFF_ID = ?";
        try {
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, staffId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Staff member deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Staff ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View staff by position
    public static void viewStaffByPosition() {
        // show all available positions
        JFrame inputFrame = new JFrame("View Staff by Position");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Available positions:");
        String allPositionsQuery =
                "SELECT DISTINCT POSITION FROM staff " +
                "WHERE POSITION IS NOT NULL AND POSITION <> '' " +
                "ORDER BY POSITION";

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        JComboBox<String> posts = new JComboBox<>(model);

        try (Connection conn = dao.SQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rsAll = stmt.executeQuery(allPositionsQuery)) {

            while (rsAll.next()) {
                String pos = rsAll.getString("POSITION");
                if (pos != null && !pos.isEmpty()) {
                    model.addElement(pos);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        JButton search = new JButton("Search");

        search.addActionListener(e -> {
            getStatus = (String)posts.getSelectedItem();

            inputFrame.dispose();

            String query = "SELECT * FROM staff WHERE POSITION = ?";
            connectTableifUnknown(query, getStatus);
        });

        inputFrame.add(label1);
        inputFrame.add(posts);
        inputFrame.add(search);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    // View staff by shift
    public static void viewStaffByShift() {
        JFrame inputFrame = new JFrame("View Staff By Employment Status");
        inputFrame.setSize(350, 100);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Select Status to Search: ");
        JButton active = new JButton("ACTIVE");
        JButton inactive = new JButton("INACTIVE");

        active.addActionListener(e -> {
            inputFrame.dispose();
            connectTable("SELECT STAFF_ID, CONCAT(FIRST_NAME, \" \", LAST_NAME) AS NAME, POSITION, EMPLOYMENT_STATUS, SALARY FROM staff WHERE EMPLOYMENT_STATUS = 'ACTIVE'");
        });

        inactive.addActionListener(e -> {
            inputFrame.dispose();
            connectTable("SELECT STAFF_ID, CONCAT(FIRST_NAME, \" \", LAST_NAME) AS NAME, POSITION, EMPLOYMENT_STATUS, SALARY FROM staff WHERE EMPLOYMENT_STATUS = 'INACTIVE'");
        });

        inputFrame.add(label1);
        inputFrame.add(active);
        inputFrame.add(inactive);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
        
    //     String query = "SELECT * FROM staff WHERE EMPLOYMENT_STATUS = ?";

    //     try {
    //         Connection conn = dao.SQLConnect.getConnection();
    //         PreparedStatement pstmt = conn.prepareStatement(query);
    //         pstmt.setString(1, status);
    //         ResultSet rs = pstmt.executeQuery();


    //         boolean found = false;
    //         while (rs.next()) {
    //             found = true;
    //             displayStaffRecord(rs);
    //         }

    //         if (!found) {
    //             JOptionPane.showMessageDialog(null, "No staff found for status: " + status);
    //         }

    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    }

    // View all staff salaries
    public static void viewAllStaffSalaries() {
        String query = "SELECT * FROM staff ORDER BY SALARY DESC";
        connectTable(query);
    }

    // List all staff members
    public static void listAllStaff() {
        String query = "SELECT * FROM staff";
        connectTable(query);
    }

    // View an individual staff member with their shift in the show & theater record
    public static void viewStaffWithShowDetails() {
        JFrame inputFrame = new JFrame("View Staff With Show Details");
        inputFrame.setSize(350, 110);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Staff ID: ");
        JTextField id = new JTextField(20);
        JButton search = new JButton("Search");

        search.addActionListener(e -> {
            inputFrame.dispose();
            getStaffId = id.getText();
        });

        inputFrame.add(label1);
        inputFrame.add(id);
        inputFrame.add(search);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    // View all staff for a specific show
    public static void viewStaffForShow() {
        JFrame inputFrame = new JFrame("View Staff For Specific Show");
        inputFrame.setSize(250, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        List<String> showIds = new ArrayList<>();
        List<String> theaterShowIds = new ArrayList<>();
        List<String> scheduleDescriptions = new ArrayList<>();
        List<String> showTitles = new ArrayList<>();

        JLabel label1 = new JLabel("Select a show:");
        String showsQuery = "SELECT SHOW_ID, TITLE FROM shows ORDER BY TITLE";

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        JComboBox<String> shows = new JComboBox<>(model);

        try (Connection conn = dao.SQLConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rsAll = stmt.executeQuery(showsQuery)) {

            while (rsAll.next()) {
                showIds.add(rsAll.getString("SHOW_ID"));
                showTitles.add(rsAll.getString("TITLE"));
                String pos = rsAll.getString("TITLE");
                if (pos != null && !pos.isEmpty()) {
                    model.addElement(pos);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        JButton search = new JButton("Search");

        search.addActionListener(e -> {

            int choice = -1;
            choice = shows.getSelectedIndex();

            String showId = showIds.get(choice);
            String showTitle = showTitles.get(choice);

            String schedulesQuery = "SELECT THEATER_SHOW_ID, THEATER_ID, RESERVATION_DATE, START_TIME, END_TIME " +
                                "FROM theater_shows WHERE SHOW_ID = ? ORDER BY RESERVATION_DATE, START_TIME"; 
                                
            try (Connection conn = dao.SQLConnect.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(schedulesQuery)) {
                ps.setString(1, showId); 
                ResultSet rsSchedules = ps.executeQuery();

            while (rsSchedules.next()) {
                String tsId = rsSchedules.getString("THEATER_SHOW_ID");
                String theaterId = rsSchedules.getString("THEATER_ID");
                String reservationDate = rsSchedules.getString("RESERVATION_DATE");
                String startTime = rsSchedules.getString("START_TIME");
                String endTime = rsSchedules.getString("END_TIME");

                String displayStart = (startTime != null && startTime.length() >= 5) ? startTime.substring(0, 5) : "";
                String displayEnd = (endTime != null && endTime.length() >= 5) ? endTime.substring(0, 5) : "";
                String timeRange = displayStart.isEmpty() ? displayEnd : (displayStart + (displayEnd.isEmpty() ? "" : "-" + displayEnd));

                String desc = "ID: " + tsId + " | Theater: " + theaterId + " | " + reservationDate + " " + timeRange;
                theaterShowIds.add(tsId);
                scheduleDescriptions.add(desc);
            }
    
            } catch (SQLException g) {
                g.printStackTrace();
                return;
            }
            connectTableifUnknown(schedulesQuery, showId);

            JFrame inputFrame2 = new JFrame("View Staff For Specific Show");
            inputFrame2.setSize(250, 150);
            inputFrame2.setLayout(new FlowLayout());
            inputFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            inputFrame2.setResizable(false);

            JLabel label2 = new JLabel("Select a show (Enter Number):");
            JTextField enterNo = new JTextField(20);
            JButton searchShow = new JButton("Search");

            inputFrame.dispose();

            searchShow.addActionListener(f -> {
                inputFrame2.dispose();

                int choice2 = Integer.parseInt(enterNo.getText());
                try {
                    if (choice2 < 0 || choice2 > scheduleDescriptions.size()) {
                        JOptionPane.showMessageDialog(null, "Invalid selection. Try again.");
                        choice2 = -1;
                        inputFrame2.setVisible(true);
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null,"Please enter a valid number.");
                }

                String theaterShowId = theaterShowIds.get(choice2 - 1);

                String finalQuery = "SELECT s.* " +
                       "FROM staff s " +
                       "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                       "WHERE sa.THEATER_SHOW_ID = ?";
                JOptionPane.showMessageDialog(null, "Staff for: " + showTitle + "\n" + scheduleDescriptions.get(choice2 - 1));
                connectTableifUnknown(finalQuery, theaterShowId);
            });

            inputFrame2.add(label2);
            inputFrame2.add(enterNo);
            inputFrame2.add(searchShow);

            inputFrame2.setLocationRelativeTo(null);
            inputFrame2.setVisible(true);
        });

        inputFrame.add(label1);
        inputFrame.add(shows);
        inputFrame.add(search);

        inputFrame.setLocationRelativeTo(null);
        inputFrame.setVisible(true);
    }

    // Helper method to display staff record
    // private static void displayStaffRecord(ResultSet rs) throws SQLException {
    //     //Main.header("Staff by Employment Status: " + status);
    //     String id = rs.getString("STAFF_ID");
    //     String firstName = rs.getString("FIRST_NAME");
    //     String lastName = rs.getString("LAST_NAME");
    //     String position = rs.getString("POSITION");
    //     String status = rs.getString("EMPLOYMENT_STATUS");
    //     int salary = rs.getInt("SALARY");
        
    //     System.out.println("ID: " + id);
    //     System.out.println("Name: " + firstName + " " + lastName);
    //     System.out.println("Position: " + position);
    //     System.out.println("Employment Status: " + status);
    //     System.out.println("Salary: ₱" + salary);
    // }
    
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

    private static void connectTableifUnknown(String query, String position) {
        try (
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, position);

            try (ResultSet rs = pstmt.executeQuery()) {

                // Get metadata for column names
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                String[] columnNames = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = meta.getColumnLabel(i + 1); // use getColumnLabel for alias support
                }

                // Create table model
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false; // make table read-only
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

                // Create JTable
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);

                App.addTable(scrollPane);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
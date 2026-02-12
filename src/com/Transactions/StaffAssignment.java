package com.Transactions;

import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class StaffAssignment {
    static String staffId = null;
    static String theaterShowId = null;

    public static void assignStaff() {
        JFrame inputFrame = new JFrame("SETTING STAFF ASSIGNMENT");
        inputFrame.setSize(350, 120);
        inputFrame.setLayout(new FlowLayout());
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setResizable(false);

        JLabel label1 = new JLabel("Enter Theater Show ID: ");
        JTextField showId = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        JFrame inputFrame2 = new JFrame("SETTING STAFF ASSIGNMENT");
        inputFrame2.setSize(350, 120);
        inputFrame2.setLayout(new FlowLayout());
        inputFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame2.setResizable(false);

        JLabel label2 = new JLabel("Enter Staff ID to assign: ");
        JTextField staffIdget = new JTextField(20);
        JButton submitButton2 = new JButton("Submit");

        try (Connection conn = dao.SQLConnect.getConnection()) {
            // Main.header("Scheduled Theater Shows");
            String showQuery = "SELECT ts.THEATER_SHOW_ID, s.TITLE, ts.START_TIME, ts.END_TIME, ts.RESERVATION_DATE "
                    + "FROM theater_shows ts "
                    + "JOIN shows s ON ts.SHOW_ID = s.SHOW_ID "
                    + "WHERE ts.SHOW_STATUS = 'SCHEDULED' ORDER BY ts.RESERVATION_DATE, ts.START_TIME";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(showQuery)) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "No scheduled theater shows found.");
                    return;
                }

                connectTable(showQuery);
            }

            inputFrame.add(label1);
            inputFrame.add(showId);
            inputFrame.add(submitButton);

            inputFrame.setLocationRelativeTo(null);
            inputFrame.setVisible(true);

            inputFrame2.add(label2);
            inputFrame2.add(staffIdget);
            inputFrame2.add(submitButton2);

            submitButton.addActionListener(e -> {
                theaterShowId = showId.getText();

                inputFrame.dispose();

                // try {
                //     if (!isScheduledShow(conn, theaterShowId)) {
                //         JOptionPane.showMessageDialog(null, "Error: Theater Show ID is invalid or not SCHEDULED.");
                //         return;
                //     }
                // } catch (SQLException ex) {
                //         ex.printStackTrace();
                // }
                
                try (Connection conn2 = dao.SQLConnect.getConnection()) {
                    step2(conn2, theaterShowId);
                } catch (SQLException f) {
                    f.printStackTrace();
                }

                inputFrame2.setLocationRelativeTo(null);
                inputFrame2.setVisible(true);

                submitButton2.addActionListener(g -> {

                    staffId = staffIdget.getText();
                    inputFrame2.dispose();

                    // try {
                    //     if (!isActiveStaff(conn, staffId)) {
                    //         JOptionPane.showMessageDialog(null, "Error: Staff ID is invalid or not ACTIVE.");
                    //         return;
                    //     }

                    //     if (assignmentExists(conn, staffId, theaterShowId)) {
                    //         JOptionPane.showMessageDialog(null, "Error: This staff is already assigned to the selected show.");
                    //         return;
                    //     }
                    // } catch (SQLException ex) {
                    //     ex.printStackTrace();
                    // }

                    try (Connection con = dao.SQLConnect.getConnection()) {
                        con.setAutoCommit(false);

                        insertAssignment(con, staffId, theaterShowId);

                        con.commit();

                        JOptionPane.showMessageDialog(
                            null,
                            "Staff assignment completed successfully!\nST" +
                            staffId + " assigned to " + theaterShowId
                        );

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Failed to assign staff.\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: Unable to complete the staff assignment transaction: " + e.getMessage());
        }
    }

    private static void step2(Connection conn, String theaterShowId) throws SQLException {

        String availableStaffQuery = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, s.POSITION "
                + "FROM staff s "
                + "WHERE s.EMPLOYMENT_STATUS = 'ACTIVE' "
                + "AND s.STAFF_ID NOT IN (SELECT STAFF_ID FROM staff_assignment WHERE THEATER_SHOW_ID = ?) "
                + "ORDER BY s.LAST_NAME, s.FIRST_NAME";

        try (PreparedStatement ps = conn.prepareStatement(availableStaffQuery)) {
            ps.setString(1, theaterShowId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null,
                            "No available staff for this show.");
                    return;
                }

                do {
                    // String staffId = rs.getString("STAFF_ID");
                    // String name = rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME");
                    // String position = rs.getString("POSITION");

                    connectTableifUnknown(availableStaffQuery, theaterShowId, null);
                } while (rs.next());
            }
        }
    }

    private static boolean assignmentExists(Connection conn, String staffId, String theaterShowId) throws SQLException {
        String query = "SELECT 1 FROM staff_assignment WHERE STAFF_ID = ? AND THEATER_SHOW_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            pstmt.setString(2, theaterShowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isActiveStaff(Connection conn, String staffId) throws SQLException {
        String query = "SELECT 1 FROM staff WHERE STAFF_ID = ? AND EMPLOYMENT_STATUS = 'ACTIVE'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isScheduledShow(Connection conn, String theaterShowId) throws SQLException {
        String query = "SELECT 1 FROM theater_shows WHERE THEATER_SHOW_ID = ? AND SHOW_STATUS = 'SCHEDULED'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, theaterShowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void insertAssignment(Connection conn, String staffId, String theaterShowId) throws SQLException {
        String query = "INSERT INTO staff_assignment (STAFF_ID, THEATER_SHOW_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, staffId);
            pstmt.setString(2, theaterShowId);
            pstmt.executeUpdate();
        }
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

    private static void connectTableifUnknown(String query, String missing, Integer missing2) {

        try (
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, missing);
            if (missing2 != null) {
                pstmt.setInt(2, missing2);
            }
            boolean hasData = false;
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    hasData = true;
                }

                if (hasData) {
                    TableDisplay frame = new TableDisplay();
                    frame.displayResultSet(rs);
                    frame.setTitle("Result");
                    frame.setVisible(true);
                }

                else 
                    JOptionPane.showMessageDialog(null, "No records found for that period.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

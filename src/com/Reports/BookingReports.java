package com.Reports;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.App;
import java.awt.*;

public class BookingReports {
    static JFrame displayFrame = new JFrame("Booking Status Report");

    static JLabel label1 = new JLabel("Select Report Period");
    static JButton daily = new JButton("Daily");
    static JButton month = new JButton("Monthly");
    static JButton year = new JButton("Yearly");

    static JLabel label2 = new JLabel("Enter Year: ");
    static JTextField enterYear = new JTextField(20);

    static JLabel label3 = new JLabel("Select Month: ");
    static Integer months[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    static JComboBox<Integer> selectMonth = new JComboBox<>(months);

    static JLabel label4 = new JLabel("Enter Day: ");
    static JTextField enterDate = new JTextField(20);

    static JButton submit = new JButton("Submit");

    static int yearNum = 0;
    static int monNum = 0;
    static String dayNum = "";

    public static void generateReport() {
        label1.setFont(new Font("Segoe UI", 0, 24));
        displayFrame.setSize(280, 250);
        displayFrame.setLayout(new FlowLayout());
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        displayFrame.setResizable(false);

        daily.addActionListener(e -> {
            displayFrame.add(label2);
            displayFrame.add(enterYear);
            displayFrame.add(label3);
            displayFrame.add(selectMonth);
            displayFrame.add(label4);
            displayFrame.add(enterDate);
            displayFrame.add(submit);
            displayFrame.revalidate();
            displayFrame.repaint();

            submit.addActionListener(f -> {
                displayFrame.dispose();
                yearNum = Integer.parseInt(enterYear.getText());
                monNum = (Integer)selectMonth.getSelectedItem();
                dayNum = enterDate.getText();

                String day = String.valueOf(yearNum) + "-" + String.valueOf(monNum) + "-" + dayNum; 
                generateDailyReport(day);
            });

        });

        month.addActionListener(e -> {

            displayFrame.add(label2);
            displayFrame.add(enterYear);
            displayFrame.add(label3);
            displayFrame.add(selectMonth);
            displayFrame.add(submit);
            displayFrame.revalidate();
            displayFrame.repaint();

            submit.addActionListener(f -> {
                displayFrame.dispose();
                yearNum = Integer.parseInt(enterYear.getText());
                monNum = (Integer)selectMonth.getSelectedItem();

                generateMonthlyReport(yearNum, monNum);
            });

        });

        year.addActionListener(e -> {

            displayFrame.add(label2);
            displayFrame.add(enterYear);
            displayFrame.add(submit);
            displayFrame.revalidate();
            displayFrame.repaint();

            submit.addActionListener(f -> {
                displayFrame.dispose();
                yearNum = Integer.parseInt(enterYear.getText());

                generateYearlyReport(yearNum);
            });

        });

        displayFrame.add(label1);
        displayFrame.add(daily);
        displayFrame.add(month);
        displayFrame.add(year);

        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
    }

    private static void generateMonthlyReport(int year, int month) {
        String query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE MONTH(booking_date) = ? AND YEAR(booking_date) = ? " +
                        "GROUP BY booking_status";

        connectTableifUnknown(query, month, year);
    }

    private static void generateDailyReport(String date) {
        String query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE booking_date = ? " +
                        "GROUP BY booking_status";

        connectTableifUnknownforDayOnly(query, date);
    }

    private static void generateYearlyReport(int year) {
        String  query = "SELECT booking_status, COUNT(*) AS count " +
                        "FROM booking " +
                        "WHERE YEAR(booking_date) = ? " +
                        "GROUP BY booking_status";

        connectTableifUnknown(query, year, null);
    }

    private static void connectTableifUnknown(String query, Integer missing, Integer missing2) {
        try (
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setInt(1, missing);
            if (missing2 != null) {
                pstmt.setInt(2, missing2);
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

    private static void connectTableifUnknownforDayOnly(String query, String missing) {
        try (
            Connection conn = dao.SQLConnect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, missing);

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

package com.Reports;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.App;
import java.awt.*;

public class StaffAssignmentReport {
    static JFrame displayFrame = new JFrame("STAFF ASSIGNMENT REPORT");

    static JLabel label1 = new JLabel("Select Report Period");
    static JButton month = new JButton("Monthly");
    static JButton qtr = new JButton("Quarterly");
    static JButton year = new JButton("Yearly");

    static JLabel label2 = new JLabel("Enter Year: ");
    static JTextField enterYear = new JTextField(20);

    static JLabel label3 = new JLabel("Select Quarter: ");
    static Integer qtrs[] = {1, 2, 3, 4};
    static JComboBox<Integer> selectQtr = new JComboBox<>(qtrs);

    static JLabel label4 = new JLabel("Select Month: ");
    static Integer months[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    static JComboBox<Integer> selectMonth = new JComboBox<>(months);

    static JButton submit = new JButton("Submit");

    static int yearNum = 0;
    static int qtrNum = 0;
    static int monNum = 0;

    public static void generateReport() {
        label1.setFont(new Font("Segoe UI", 0, 24));
        displayFrame.setSize(300, 250);
        displayFrame.setLayout(new FlowLayout());
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        displayFrame.setResizable(false);

        month.addActionListener(e -> {

            displayFrame.add(label2);
            displayFrame.add(enterYear);
            displayFrame.add(label4);
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

        qtr.addActionListener(e -> {

            displayFrame.add(label2);
            displayFrame.add(enterYear);
            displayFrame.add(label3);
            displayFrame.add(selectQtr);
            displayFrame.add(submit);
            displayFrame.revalidate();
            displayFrame.repaint();

            submit.addActionListener(f -> {
                displayFrame.dispose();
                yearNum = Integer.parseInt(enterYear.getText());
                qtrNum = (Integer)selectQtr.getSelectedItem();

                generateQuarterlyReport(yearNum, qtrNum);
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
        displayFrame.add(month);
        displayFrame.add(qtr);
        displayFrame.add(year);

        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
    }

    private static void generateMonthlyReport(int year, int month) {
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theaters th ON ts.THEATER_ID = th.THEATER_ID " + 
                "WHERE YEAR(ts.RESERVATION_DATE) = ? AND MONTH(ts.RESERVATION_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

        connectTableifUnknown(query, year, month);
    }

    private static void generateQuarterlyReport(int year, int quarter) {
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theaters th ON ts.THEATER_ID = th.THEATER_ID " + 
                "WHERE YEAR(ts.RESERVATION_DATE) = ? AND QUARTER(ts.RESERVATION_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

        connectTableifUnknown(query, year, quarter);
    }

    private static void generateYearlyReport(int year) {
        String query = "SELECT s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME, " +
                "COUNT(DISTINCT ts.THEATER_SHOW_ID) as total_shows, " +
                "SUM(TIMESTAMPDIFF(HOUR, ts.START_TIME, ts.END_TIME)) as total_hours " +
                "FROM staff s " +
                "JOIN staff_assignment sa ON s.STAFF_ID = sa.STAFF_ID " +
                "JOIN theater_shows ts ON sa.THEATER_SHOW_ID = ts.THEATER_SHOW_ID " +
                "JOIN theaters th ON ts.THEATER_ID = th.THEATER_ID " + 
                "WHERE YEAR(ts.RESERVATION_DATE) = ? " +
                "GROUP BY s.STAFF_ID, s.FIRST_NAME, s.LAST_NAME " +
                "ORDER BY total_hours DESC, total_shows DESC";

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
}

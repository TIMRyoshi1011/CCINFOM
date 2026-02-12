package com.Reports;

import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class FinanicalPerformanceReport {
    static JFrame displayFrame = new JFrame("FINANCIAL PERFORMANCE REPORT");

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
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? AND MONTH(b.booking_date) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date), MONTH(b.booking_date) " + 
                "ORDER BY s.title, year;";

        connectTableifUnknown(query, year, month);
    }

    private static void generateQuarterlyReport(int year, int quarter) {
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? " +
                "AND CEILING(MONTH(b.booking_date)/3) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date) " + 
                "ORDER BY s.title, year; ";

        connectTableifUnknown(query, year, quarter);
    }

    private static void generateYearlyReport(int year) {
        String query = "SELECT YEAR(b.booking_date) AS year, s.title, SUM(b.total_price) AS total_show_revenue, SUM(sf.salary) AS total_salary_cost " +
                "FROM shows s JOIN theater_shows ts ON ts.show_id = s.show_id " +
                "JOIN booking b ON b.theater_show_id = ts.theater_show_id " +
                "JOIN staff_assignment sa ON ts.THEATER_SHOW_ID = sa.THEATER_SHOW_ID " +
                "JOIN staff sf ON sa.STAFF_ID = sf.STAFF_ID " +
                "WHERE b.booking_status = 'CONFIRMED' AND YEAR(b.booking_date) = ? " +
                "GROUP BY s.title, YEAR(b.booking_date) " + 
                "ORDER BY s.title, year;";

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

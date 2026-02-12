/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
/**
 *
 * @author Marcus
 */
public class SQLConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/theatershows";
    private static final String USER = "root";
    private static String PASSWORD;
    
    public static Connection getConnection() {
        try{
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        }
        catch(Exception e){
            return null;
        }
    }

    public static void setPassword(String password) {
        PASSWORD = password;
    }
}

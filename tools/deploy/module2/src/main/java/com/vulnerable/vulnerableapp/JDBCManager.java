package com.vulnerable.vulnerableapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to connect to the database and execute dynamic queries to
 * illustrate SQL injection vulnerability.
 */
public class JDBCManager {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:mem:testdb";
    static final String USER = "sa";
    static final String PASS = "";

    /**
     * This method is used to execute a dynamic sql query.
     * 
     * @param vulnerableQuery
     */
    public static List<Employee> executeQuery(String vulnerableQuery) {

        Connection conn = null;
        Statement stmt = null;
        List<Employee> employees = new ArrayList<Employee>();

        try {

            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String sql = "Select * from " + vulnerableQuery;

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getLong(1));
                employee.setName(rs.getString(2));
                employee.setDesignation(rs.getString(3));
                employee.setExpertise(rs.getString(4));
                employees.add(employee);
                //System.out.println(rs.getString(1));
                //System.out.println(rs.getString(2));
                //System.out.println(rs.getString(3));
                //System.out.println(rs.getString(4));
            }

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return employees;
    }
}
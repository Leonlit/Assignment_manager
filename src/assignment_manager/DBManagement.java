/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class DBManagement {
    private final String HOST = "jdbc:derby://localhost:1527/Assignment_manager";
    private final String USER = "leonlit";
    private final String PASSWORD = "Nw>u)tp\\tvu4$Sb_";
    private ArrayList<ParsedData> data= new ArrayList<ParsedData>();
    
    public DBManagement() {
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement(); //creating a statement container
            String query = "SELECT * FROM LEONLIT.\"Assignments\"";
            ResultSet result = sql.executeQuery(query);
            System.out.println(result.toString());
            while (result.next()) {
                ParsedData temp = new ParsedData(result.getInt("ID"),result.getString("Title"),result.getString("DueDate"));
                data.add(temp);
            }
        }catch (SQLException ex) {
            System.out.println("Connection error when connecting to the server.\n" + ex.getMessage());
        }    
    }
    
    public ArrayList<ParsedData> getAllData () {
        return data;
    }
}
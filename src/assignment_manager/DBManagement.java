/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author User
 */
public class DBManagement {
    private final String HOST = "jdbc:derby://localhost:1527/Assignment_manager";
    private final String USER = "leonlit";
    private final String PASSWORD = "Nw>u)tp\\tvu4$Sb_";
    public ArrayList<ParsedData> data = new ArrayList<ParsedData>();
    
    public DBManagement() {
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement(); //creating a statement container
            String query = "SELECT * FROM LEONLIT.\"Assignments\"";
            ResultSet result = sql.executeQuery(query);
            System.out.println(result.toString());
            int counter = 0;
            while (result.next()) {
                ParsedData temp = new ParsedData(result.getInt("ID"),
                                                counter,
                                                result.getString("TITLE"),
                                                result.getString("DUEDATE"));
                data.add(temp);
                counter++;
            }
            Comparator<ParsedData> ascendingComparator = new Comparator<ParsedData>() {
                @Override
                public int compare(ParsedData a, ParsedData b) {
                    return a.daysLeft() - b.daysLeft();
                }
            };
            data.sort(ascendingComparator);

        }catch (SQLException ex) {
            System.out.println("Connection error when connecting to the server.\n" + ex.getMessage());
        }    
    }
    
    public int  addData (String title, String dueDate) {
        int stats = 1;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO LEONLIT.\"Assignments\""
                                                           + "(TITLE, DUEDATE) VALUES (?, ?)", 
                                                           Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title);
            pstmt.setString(2, dueDate);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int newID = generatedKeys.getInt(1);
                System.out.println("newID : " + newID);
                data.add(new ParsedData(newID, data.size(), title, dueDate));
                updateDataIndex();
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
            
        }catch (SQLException ex) {
            System.out.println("Connection error when connecting to the server.\n" + ex.getMessage());
            stats = -1;
        }
        return stats;
    }
    
    public int deleteData (int ID) {
        int num = 0;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement(); //creating a statement container
            String query = "DELETE FROM LEONLIT.\"Assignments\" WHERE ID=" + ID;
            num = sql.executeUpdate(query);
            System.out.println("Number of records deleted are: "+num);
        }catch(SQLException ex) {
             System.out.println("failed to delete record");
        }
        return num;
    }
    
    public void updateDataIndex () {
        for (int x = 0; x< data.size();x++) {
            data.get(x).updateIndex(x);
        }
    }
}
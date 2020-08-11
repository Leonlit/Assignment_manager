package org.assignment_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;

public class DBManagement {
    private final String HOST = "jdbc:derby://localhost:1527/Assignment_manager";
    private final String USER = "leonlit";
    private final String PASSWORD = "test";
    public ArrayList<ParsedData> data = new ArrayList<ParsedData>();
    
    public DBManagement() {
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement();
            String query = "SELECT * FROM Assignments";
            ResultSet result = sql.executeQuery(query);
            int counter = 0;
            while (result.next()) {
                ParsedData temp = new ParsedData(result.getInt("ID"),
                                                counter,
                                                result.getString("TITLE"),
                                                result.getString("DUEDATE"));
                data.add(temp);
                counter++;
            }
            updateDataIndex();
        }catch (SQLException ex) {
            showDBErr("Connection error when connecting to the server.\n\n" + ex.getMessage());
        }    
    }
    
    public int  addData (String title, String dueDate) {
        int stats = 1;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO Assignments"
                                                           + "(TITLE, DUEDATE) VALUES (?, ?)", 
                                                           Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title);
            pstmt.setString(2, dueDate);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating Reocrd failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int newID = generatedKeys.getInt(1);
                data.add(new ParsedData(newID, data.size(), title, dueDate));
                updateDataIndex();
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
            
        }catch (SQLException ex) {
            showDBErr("Connection error when connecting to the server.\n\n" + ex.getMessage());
            stats = -1;
        }
        return stats;
    }
    
    public int deleteData (int ID) {
        int stats = 0;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement(); //creating a statement container
            String query = "DELETE FROM Assignments WHERE ID=" + ID;
            stats = sql.executeUpdate(query);
            if (stats == 0) {
                throw new SQLException();
            }
            updateDataIndex();
        }catch(SQLException ex) {
            showDBErr("failed to delete record\n\n" + ex);
        }
        return stats;
    }
    
    public int editData (int ID, String newTitle, String newDueDate) {
        int stats = 0;
        try {
            Connection con = DriverManager.getConnection(HOST, USER, PASSWORD);
            Statement sql = con.createStatement(); //creating a statement container
            String query = "UPDATE Assignments SET TITLE='" + newTitle 
                            + "' ,DUEDATE='" + newDueDate + "' WHERE ID=" + ID;
            stats = sql.executeUpdate(query);
            if (stats == 0) {
                throw new SQLException();
            }
            updateDataIndex();
        }catch(SQLException ex) {
            showDBErr("failed to update record\n\n" + ex);
        }
        return stats;
    }
    
    public void updateDataIndex () {
        Comparator<ParsedData> ascendingComparator = new Comparator<ParsedData>() {
            @Override
            public int compare(ParsedData a, ParsedData b) {
                return a.daysLeft() - b.daysLeft();
            }
        };
        data.sort(ascendingComparator);
        
        for (int x = 0; x< data.size();x++) {
            data.get(x).updateIndex(x);
        }
    }
    
    private void showDBErr (String message) {
        ShowError.showError("Database error notice!!!", message);
    }
}
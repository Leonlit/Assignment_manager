package org.assignment_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManagement {
    //making database details constant
    private Connection conn;

    //the array list that's used to contain all the assignments that's stored in the database
    public ArrayList<ParsedData> data = new ArrayList<ParsedData>();
    
    //constructer used to get all data from DB an store them inside the data object
    public DBManagement(int year) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignment_manager.db");
            //making sure that the table exist before doing any database operation
            makeSureTableExist("Assignments");
            String query = "SELECT * FROM Assignments WHERE strftime('%Y', duedate)=?;";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, Integer.toString(year));
            ResultSet result = pstmt.executeQuery();
            int counter = 0;
            //after getting the result from the database, store the result into the ArrayList one by one
            while (result.next()) {
                //when storing the data into a ParsedData object, you'll need to 
                //have the ID of the assignment,
                //the current counter to be passed as current index of the assignment,
                //title of the assignment as well as the due date of the assingment
                ParsedData temp = new ParsedData(result.getInt("ID"),
                                                counter,
                                                result.getString("TITLE"),
                                                result.getString("DUEDATE"));
                data.add(temp);     //after all that add it into the ArrayList
                counter++;          //increase the counter value
            }
            //rearranging the data into orders as they are not in specific order when they're stored in the database
            updateDataIndex();
        }catch (SQLException ex) {
            showDBErr("Connection error when connecting to the server.\n\n" + ex.getMessage());
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }
            }
            catch(SQLException e){
                showDBErr(e.getMessage());
            }
        }
    }
    
    //Make sure the table exists in the database to avoid error.
    //If the table doest not exist, create the table.
    //  @param tableName   - the name of the table to check.
    private void makeSureTableExist (String tableName) throws SQLException {
        String creatTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName +" ("
                + "ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "TITLE LONG VARCHAR,"
                + "DUEDATE DATE)";
        Statement stmt = conn.createStatement();
        stmt.execute(creatTableQuery);
    }
    
    //adding data into the DB
    // @param title   - receive a string that contains the title of the assignment
    // @param dueDate - receive a string that contains the due date for the assignments
    // @return stats  - return a integer value to indicate if the operation is succesfull or not
    //                  1 means success 
    //                  -1 would mean the operaion failed
    public int addData (String title, String dueDate) {
        int stats = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignment_manager.db");
            makeSureTableExist("Assignments");
            //used perpared statement to avoid injections
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Assignments"
                                                           + "(TITLE, DUEDATE) VALUES (?, ?)", 
                                                           Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, title);
            pstmt.setString(2, dueDate);
            int affectedRows = pstmt.executeUpdate();
            
            //if affected errors are 0, means some unexpected things happened.
            if (affectedRows == 0) {
                throw new SQLException("Creating Record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    //getting the first key of the generated keys
                    int newID = generatedKeys.getInt(1);
                    //adding the newly added assignment object into the ArrayList, using the retrieved ID, and its current index in the ArrayList
                    //by using the current size of the ArrayList. Then, as well as the title and due date of the assignment
                    final int taskYear = Integer.parseInt(dueDate.substring(0, 4));
                    //if the task is in the current year, update the data and indexes
                    if(taskYear == MainController.currYear) {
                        data.add(new ParsedData(newID, data.size(), title, dueDate));
                        updateDataIndex();  //reposition all assignment objects according to their index (refers back to the Data class in Data.java)
                    }
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }catch (SQLException ex) {
                showDBErr("Unable to get the generated keys after performing PUT/INSERT operation");
            }
        }catch (SQLException ex) {
            showDBErr("Connection error when connecting to the server.\n\n" + ex.getMessage());
            //since we are adding data into the database from different page, we need to keep track of the operation
            //so that we can decide to close the window or keep it open after adding (refer to AddNewController.java)
            stats = -1;
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }
            }
            catch(SQLException e){
                showDBErr(e.getMessage());
            }
        }
        return stats;
    }
    
    public ParsedData getEarliest() {
        ParsedData earliestItem = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignment_manager.db");
            makeSureTableExist("Assignments");
            String sqlQuery = "SELECT * FROM Assignments ORDER BY DUEDATE LIMIT 1;";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            ResultSet result = pstmt.executeQuery();
            //after getting the result from the database, store the result into the ArrayList one by one
            if (result.next()) {
                earliestItem = new ParsedData(result.getInt("ID"), 0,
                                            result.getString("TITLE"),
                                            result.getString("DUEDATE"));
            }
        } catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(DBManagement.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }
            }
            catch(SQLException e){
                showDBErr(e.getMessage());
            }
        }
        return earliestItem;
    }
    
    //deleting data from the database
    // @param ID - the id of the data to be deleted from the database
    // @return stats  - return a integer value to indicate if the operation is succesfull or not
    //                  1 means success 
    //                  -1 would mean the operaion failed
    public int deleteData (int ID) {
        int stats = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignment_manager.db");
            makeSureTableExist("Assignments");
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Assignments WHERE ID=?"); //creating a statement container
            pstmt.setInt(1, ID);
            stats = pstmt.executeUpdate();;
            updateDataIndex();
        }catch(SQLException ex) {
            showDBErr("failed to delete record\n\n" + ex);
            stats = -1;
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }
            }
            catch(SQLException e){
                showDBErr(e.getMessage());
            }
        }
        return stats;
    }
    
    //editing the data from the database
    // @param ID         - the id of the data to be edited in the database
    // @param newTitle   - the String that contains the new title
    // @param newDueDate - the string that contains the new due date of the assignments
    // @return stats  - return a integer value to indicate if the operation is succesfull or not
    //                  1 means success 
    //                  -1 would mean the operaion failed
    public int editData (int ID, String newTitle, String newDueDate) {
        int stats = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:assignment_manager.db");
            makeSureTableExist("Assignments");
            PreparedStatement pstmt = conn.prepareStatement("UPDATE Assignments SET TITLE=? ,DUEDATE=? WHERE ID=?");
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newDueDate);
            pstmt.setInt(3, ID);
            stats = pstmt.executeUpdate();
            if (stats == 0) {
                throw new SQLException();
            }
            updateDataIndex();
        }catch(SQLException ex) {
            stats = -1;
            showDBErr("failed to update record\n\n" + ex);
        }catch (ClassNotFoundException ex) {
            showDBErr("Sqlite driver not found!!!\n\n" + ex.getMessage());
        }finally{
            try{
                if(conn != null) {
                    conn.close();
                }
            }
            catch(SQLException e){
                showDBErr(e.getMessage());
            }
        }
        return stats;
    }
    
    //updating the data index in the array list
    public void updateDataIndex () {
        //creating a comparator that compare the days left between two assignments
        //in the low to high order
        Comparator<ParsedData> ascendingComparator = new Comparator<ParsedData>() {
            @Override
            public int compare(ParsedData a, ParsedData b) {
                return a.daysLeft() - b.daysLeft();
            }
        };
        //use the comparator in the sort method
        data.sort(ascendingComparator);
        
        //since just now we're only updating the position of the elements, we need to also update the 
        //index stored in the object
        for (int x = 0; x< data.size();x++) {
            ParsedData currentItem = data.get(x);
            if (currentItem.getYear() != MainController.currYear) {
                data.remove(x);
            }
            currentItem.updateIndex(x);
        }
    }
    
    //there's a file called ShowError which incharge of the error pop-up
    private void showDBErr (String message) {
        ShowError.showError("Database error notice!!!", message);
    }
}
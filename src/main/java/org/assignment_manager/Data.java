package org.assignment_manager;

//The base class for carrying the assignments data
public class Data {
    private String dueDate[];
    private String title;
    private int ID;
    private int index;
    
    //constructing assignments object's data
    // @param ID      - the Id of the assignment
    // @param index   - the current index of the assignment, since we hve multiple ways to sort the assignments.
    // @param title   - the title of the assignment
    // @param dueData - the due date for the aasignment
    public Data (int ID, int index, String title, String dueDate) {
        this.ID = ID;
        this.index = index;
        this.title = title;
        //the due date String will need to be splitted because the format for the date are in yyyy/mm/dd
        this.dueDate = dueDate.split("-");
    }
    
    //getter to get the ID
    public int getID () {
        return ID;
    }
    
    //getter to get the day of the due date at a month
    public int getDay () {
        return Integer.parseInt(dueDate[2]);
    }
    
    //updating the current objects index to a new one (when new assignments are added, changed or deleted)
    public void updateIndex(int newIndex) {
        index = newIndex;
    }
    
    //getter for getting the index of the assignments (in the arraylist not in the DB)
    public int getIndex() {
        return index;
    }
    
    //getter to get the whole due date (change back from array to string)
    public String getDueDate () {
        return String.join("-", dueDate);
    }
    
    //get the month for the due date of the assignment
    public int getMonth () {
        return Integer.parseInt(dueDate[1]);
    }
    
    //getting the year of the assignment
    public int getYear () {
        return Integer.parseInt(dueDate[0]);
    }
    
    //getting the title of the assignment
    public String getTitle () {
        return title;
    }

}

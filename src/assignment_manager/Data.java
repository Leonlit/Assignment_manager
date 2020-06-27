/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

/**
 *
 * @author User
 */
public class Data {
    private String dueDate[];
    private String title;
    private int ID;
    private int index;
    
    public Data (){}
    
    public Data (int ID, int index, String title, String dueDate) {
        this.ID = ID;
        this.index = index;
        this.title = title;
        this.dueDate = dueDate.split("-");
    }
    
    public int getID () {
        return ID;
    }
    
    public int getDay () {
        return Integer.parseInt(dueDate[2]);
    }
    
    public void updateIndex(int newIndex) {
        index = newIndex;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getDueDate () {
        return String.join("-", dueDate);
    }
    
    public int getMonth () {
        return Integer.parseInt(dueDate[1]);
    }
    
    public int getYear () {
        return Integer.parseInt(dueDate[0]);
    }
    
    public String getTitle () {
        return title;
    }

}

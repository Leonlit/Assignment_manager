package org.assignment_manager;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditDataController implements Initializable {

    private DBManagement DBObj;                     //the object that has the method for handling database operation
    private ParsedData currItem;                    //the current item that're to be edited
    private Stage stage;                            //the stage object of the window for editing data
    private Stage confirmationStage;
    public static boolean confirmationWindowVisible = false;       //to set that only one confirmation window could be opened
    
    @FXML
    private TextField editTitle;
    
    @FXML
    private DatePicker editDate;

    //setting up the needed data for editing the assignment data
    // @param data  - a ParsedData object that are holding the current item to be edited
    // @param DB    - the object that are holding the required methods for handling database operation
    // @param stage - the object of a Stage class which are used to control the behavior of the window
    public void setupEditData (ParsedData data, DBManagement DB, Stage stage, Stage popUpStage) {
        this.DBObj = DB;
        this.stage = stage;
        this.currItem = data;
        this.confirmationStage = popUpStage;
        editTitle.setText(data.getTitle());
        //change the date time stored in the objecet data back to a format that we can display in the date picker 
        //creating a format for the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //change the format of the date to follow the created format above
        LocalDate date = LocalDate.parse(data.getDueDate(), formatter);
        //set the value of the date
        editDate.setValue(date);
    }
    
    //triggered when the edit data button is created
    @FXML
    public void editData () {
        String errText = "";
        String newDueDate = "";
        String warningText = "";
        
        final String title = editTitle.getText();
        //if the title is empty, need to add error text into errText variable
        if (title.equals("")) {
            errText += "Error: A title is needed!!!\n";
        }
        
        //getting the date for the due date of the assignment by using a try-catch statment
        try {
            //if there's no value in the date picker field, there will no value that could be taken
            //therefore when user make the date picker field empty a null exception will be triggered when the 
            //the we want to get the value
            newDueDate = editDate.getValue().toString();
            boolean taskSetToPassedDate = ParsedData.checkIfTaskSetToPassedDate(newDueDate);
            if (taskSetToPassedDate) {
                warningText += "\nWarning: You're setting the task to a date that's older than the current Date";
            } 
        }catch (NullPointerException err) {
            errText += "Error: A Due Date is needed for this assignment!!!\n";
        }

        if (title == currItem.getTitle() && newDueDate == currItem.getDueDate()) {
            warningText += "The data has not been changed, are you sure to submit the change?";
        }
        
        //if there's no error produced when checking user's provided data continue with adding the data into the database
        //by first asking for permission
        if(errText.length() == 0) {
            //creating a comfirmation pop-up to see if user really want to edit the data
            //only one confirmation window that could be created at a time
            if (!confirmationWindowVisible) {
                confirmationWindowVisible = true;
                DataManipulationConfirmationWindow test = new DataManipulationConfirmationWindow(DBObj, warningText, currItem,
                                 title, newDueDate, stage, 
                                 confirmationStage);
            }
        }else {
            //if there's error message in the errText variable, display the error in the confirmation pop-up window
            ShowError.showError("Error occured when editing records\n\n", errText);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
}

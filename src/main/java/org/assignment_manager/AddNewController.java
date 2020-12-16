package org.assignment_manager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddNewController implements Initializable {

    private Stage stage;                            //the object that store the methods for handling the window behaviour
    private DBManagement DB;                        //the object that store the methods that are used in DB handling 
    public static boolean confirmationWindowVisible = false;      //we set that only one comfirmation window could be created at one time
    private Stage confirmationPopUp;
    
    @FXML
    private TextField createNewTitle;
    
    @FXML
    private DatePicker createNewDueDate;
    
    //when the user pressed the button to add new data into the database
    //we need to check validity of the data before we can add it into the database
    @FXML
    public void addData () {
        String errorText = "";
        String newDueDate = "";
        String warningText = "";
        
        try {
            //we want to see if there's any value set in the due date section
            newDueDate = createNewDueDate.getValue().toString();
            boolean taskSetToPassedDate = ParsedData.checkIfTaskSetToPassedDate(newDueDate);
            System.out.println(newDueDate);
            if (taskSetToPassedDate) {
                warningText += "\nWarning: You're setting the task to a date that's older than the current Date";
            } 
        }catch (NullPointerException err) {
            //if there's no due date set in the date picker field, show a pop up message
            errorText += "Error: A Due Date is needed for this assignment!!!\n";
        }

        //then we get the title for the assignment
        //this operation does not need a try catch statement as empty value in the field means ""
        //which is a valid string
        String newTitle = createNewTitle.getText();

        //since no error will be produced when the value of the title text field is empty
        //we need to explicitly check for empty value
        if (newTitle.equals("")) {
            errorText += "Error: A title is needed!!!\n";
        }
        
        //if the length of the string is less than 1 means its empty, means that user has provided all the required data correctly
        if (errorText.length() == 0) {
            if (!confirmationWindowVisible) {
                confirmationWindowVisible = true;
                //since to use a varible value in the event handle need to be in constant
                DataManipulationConfirmationWindow test = new DataManipulationConfirmationWindow(DB, warningText, newTitle,
                            newDueDate, stage, confirmationPopUp);
            }
        }else {
            //but if the length of the errorText is more than 1, means that there's something wrong with the user input
            ShowError.showError("Error in adding records\n\n", errorText);
        }
    }

    //storin the required data
    // @param DB    - the object that contains method for handling database operations
    // @param stage - A Stage class object for managing the window
    public void setupAddingData (DBManagement DB, Stage stage, Stage confirmationPopUp) {
        this.stage = stage;
        this.DB = DB;
        this.confirmationPopUp = confirmationPopUp;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
}

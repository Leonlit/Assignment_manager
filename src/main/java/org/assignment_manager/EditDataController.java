package org.assignment_manager;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class EditDataController implements Initializable {

    private DBManagement DBObj;                     //the object that has the method for handling database operation
    private ParsedData currItem;                    //the current item that're to be edited
    private Stage stage;                            //the stage object of the window for editing data
    private boolean isManagingRecord = false;       //to set that only one confirmation window could be opened
    
    @FXML
    private TextField editTitle;
    
    @FXML
    private DatePicker editDate;

    //setting up the needed data for editing the assignment data
    // @param data  - a ParsedData object that are holding the current item to be edited
    // @param DB    - the object that are holding the required methods for handling database operation
    // @param stage - the object of a Stage class which are used to control the behavior of the window
    public void setupEditData (ParsedData data, DBManagement DB, Stage stage) {
        DBObj = DB;
        this.stage = stage;
        currItem = data;
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
        Stage editPageStage = new Stage();
        String errText = "";
        String newDueDate = "";
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
        }catch (NullPointerException err) {
            errText += "Error: A Due Date is needed for this assignment!!!\n";
        }
        boolean taskSetToPassedDate = ParsedData.checkIfTaskSetToPassedDate(newDueDate);
        
        if (taskSetToPassedDate) {
            errText += "\nWarning: You're changing the task to a date that's older than the current day";
        } 
        //if there's no error produced when checking user's provided data continue with adding the data into the database
        //by first asking for permission
        if(errText.length() < 1) {
            //creating a comfirmation pop-up to see if user really want to edit the data
            //only one confirmation window that could be created at a time
            if (!isManagingRecord) {
                isManagingRecord = true;
                final String DUEDATE = newDueDate;

                BorderPane layout= new BorderPane();
                VBox childs = new VBox();

                Label text = new Label("Confirm to edit \"" + currItem.getTitle() 
                                        + "\",\nDued in " + currItem.getDueDate() + "\n\nInto\n\n"
                                        + title + ",\nDued in " + DUEDATE + "?");

                Button confirm = new Button("Confirm");

                childs.getChildren().addAll(text, confirm);
                text.setPadding(new Insets(10, 10, 20, 20));
                childs.setAlignment(Pos.CENTER);

                layout.setCenter(childs);

                Scene newScene = new Scene(layout, 400, 250);

                editPageStage.setScene(newScene);
                editPageStage.setTitle("Data Edition Confirmation");
                editPageStage.show();

                //when the user confirmed that he/she want to edit the data
                confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        //construct a new ParsedData object so that it could replace the object stored in the global array list
                        ParsedData newData = new ParsedData(currItem.getID(), currItem.getIndex(), title, DUEDATE);
                        //by using the editData method in the DBManagement class, begin to edit the data in the database
                        int stats = DBObj.editData(currItem.getID(), title, DUEDATE);
                        //if the operation returned 1 after the operation, it means that the data has been edited succesfully in the database
                        //set the confirm button     
                        confirm.setVisible(false);
                        if (stats == 1 ) {
                            //change the message from the pop up window
                            text.setText("Succefully Edited the record!!!");
                            //replace the data in the global ArrayList that stores all the assignments
                            DBObj.data.set(currItem.getIndex(), newData);
                            //update the index of the object as maybe the edited data changed its due date
                            DBObj.updateDataIndex();
                        }else {
                            text.setText("Failed to edit the record in Database!!!");
                        }
                    }
                });

                //when the comfirmation window is closed
                editPageStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        //if the confirm button is not visible, it means that the operation was successfull
                        //so we need to also close the window for editing the data.
                        if (!confirm.isVisible()) {
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                            stage.close();
                        }
                        isManagingRecord = false;
                    }
                });
                
            }
        }else {
            //if there's error message in the errText variable, display the error in the confirmation pop-up window
            ShowError.showError("Error occured when editing records\n\n", errText);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

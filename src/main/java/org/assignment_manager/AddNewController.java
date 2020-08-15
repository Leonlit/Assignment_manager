package org.assignment_manager;

import java.net.URL;
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

public class AddNewController implements Initializable {

    private Stage stage;                            //the object that store the methods for handling the window behaviour
    private DBManagement DB;                        //the object that store the methods that are used in DB handling 
    private boolean managingOneRecord = false;      //we set that only one comfirmation window could be created at one time
    
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
        try {
            //we want to see if there's any value set in the due date section
            newDueDate = createNewDueDate.getValue().toString();
        }catch (NullPointerException err) {
            //if there's no due date set in the date picker field, show a pop up message
            errorText += "Error: A Due Date is needed for this assignment!!!\n";
        }
        
        //then we get the title for the assignment
        //this operation does not need a try catch statement as empty value in the field means ""
        //which is a valid string
        String newTitle = createNewTitle.getText();
        
        //showing error base on some conditions 
        //this app currently only support storing assignment for the current year only 
        if (errorText.length() < 1 && Integer.parseInt(newDueDate.substring(0,4)) != MainController.currYear) {
            errorText += "Error: This app only support saving task that's in the current year!!!\n";
        }
    

        //since no error will be produced when the value of the title text field is empty
        //we need to explicitly check for empty value
        if (newTitle.equals("")) {
            errorText += "Error: A title is needed!!!\n";
        }
        
        //if the length of the string is less than 1 means its empty, means that user has provided all the required data correctly
        if (errorText.length() < 1) {
            //
            if (!managingOneRecord) {
                managingOneRecord = true;
                Stage addPageNotice = new Stage();
                //since to use a varible value in the event handle need to be in constant
                final String TITLE = newTitle;
                final String DUEDATE = newDueDate;

                //setting up a confirmation window
                BorderPane layout= new BorderPane();
                VBox childs = new VBox();
                
                Label text = new Label("Confirm to create " + TITLE + ",\n that's due in " + DUEDATE +"?");
                text.setStyle("-fx-font:14px Georgia;"
                            + "-fx-font-weight:800;");
                Button confirm = new Button("Confirm");

                childs.getChildren().addAll(text, confirm); 
                text.setPadding(new Insets(10, 10, 20, 20));
                childs.setAlignment(Pos.CENTER);

                layout.setCenter(childs);

                Scene newScene = new Scene(layout, 400, 250);

                addPageNotice.setScene(newScene);
                addPageNotice.setTitle("Create new record Confirmation");
                addPageNotice.show();

                //when the confirm button is clicked, begin to add the data into the database
                confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        //using the addData method that's in the DBManagement.java to add data into Database
                        int result = DB.addData(TITLE, DUEDATE);
                        //if the returned value is 1, it means that the operation is succesfull
                        if (result == 0 ) {
                            //change the message displayed in the window
                           text.setText("Succefully Added the record!!!");
                       }else {
                            //if the returned value is not 1, it means that the operation failed
                            //so we need to change the message to give user a notice on the error
                            text.setText("Failed to add the record to Database!!!");
                        }
                        //hide the comfirm button so that user cannot re-click the button
                        confirm.setVisible(false);
                    }
                });
                
                //adding a event handler for the confirmation window to detect if the window is being closed by the user
                addPageNotice.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        //if the confirm button is not visible, it means that the user has either get an error or has successfully added the data
                        //into the database. so we can also close the window for creating a new assignment
                        if (!confirm.isVisible()) {
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                            stage.close();
                        }
                        managingOneRecord = false;
                    }
                });
            }
        }else {
            //but if the length of the errorText is more than 1, means that there's something wrong with the user input
            ShowError.showError("Error in adding records\n\n", errorText);
        }
    }

    //storin the required data
    // @param DB    - the object that contains method for handling database operations
    // @param stage - A Stage class object for managing the window
    public void setupAddingData (DBManagement DB, Stage stage) {
        this.stage = stage;
        this.DB = DB;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

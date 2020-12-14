package org.assignment_manager;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ConfirmationWindow {
    private Label warningLabel = new Label(), 
                errorLabel = new Label();
    private DBManagement DB;
    private String warningText, newDate, newTitle;
    private ParsedData currItem;
    private Stage confirmationStage, parentStage;
    
    public ConfirmationWindow(DBManagement DB, String warningText, ParsedData currItem,
                                String newTitle, String newDate, Stage parentStage, 
                                Stage popUpStage ) {
        this.currItem = currItem;
        this.warningText = warningText;
        this.newDate = newDate;
        this.newTitle = newTitle;
        this.DB = DB;
        this.parentStage = parentStage;
        this.confirmationStage = popUpStage;
        setupWindow(true);
    }
    
    public ConfirmationWindow(DBManagement DB, String warningText,String newTitle,
                            String newDate, Stage parentStage, Stage popUpStage
                            ) {
        this.warningText = warningText;
        this.newDate = newDate;
        this.newTitle = newTitle;
        this.DB = DB;
        this.parentStage = parentStage;
        this.confirmationStage = popUpStage;
        setupWindow(false);
    }
    
    private void setupWindow (boolean editPage) {
        BorderPane layout= new BorderPane();
        VBox childs = new VBox();
        Button confirm = new Button("Confirm");
        
        String winTitle = "Create new record Confirmation";

        String boxPadding = "-fx-padding: 10 20 30 20;";

        System.out.println("test");
        System.out.println(warningText);
        if (warningText.length() > 0) {
            System.out.println("test2");
            warningLabel.setText(warningText);
            warningLabel.setStyle("-fx-font:14px Georgia;"
            + "-fx-font-weight:800;"
            + "-fx-text-fill:red;"
            + "-fx-max-witdh: 100;"
            + boxPadding);
            warningLabel.setTextAlignment(TextAlignment.CENTER);
            warningLabel.setWrapText(true);
            childs.getChildren().add(warningLabel);
        }
        
        errorLabel.setText("Confirm to create " + this.newTitle + ",\n that's due in " + this.newTitle +"?");
        
        if (editPage) {
            winTitle = "Data Edition Confirmation";
            errorLabel.setText("Confirm to edit \"" + currItem.getTitle() 
                                + "\",\nDued in " + currItem.getDueDate() + "\n\nInto\n\n"
                                + newTitle + ",\nDued in " + newDate + "?");
        }
        
        errorLabel.setStyle("-fx-font:14px Georgia;"
                        + "-fx-font-weight:800;"
                        + "-fx-max-witdh: 100;"
                        + boxPadding);

        childs.getChildren().addAll(errorLabel, confirm);
        childs.setAlignment(Pos.CENTER);
        layout.setCenter(childs);
        Scene newScene = new Scene(layout, 400, 300);

        confirmationStage.setScene(newScene);
        confirmationStage.setTitle(winTitle);
        confirmationStage.show();

        //when the user confirmed that he/she want to edit the data
        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                //construct a new ParsedData object so that it could replace the object stored in the global array list
                if(editPage) {
                    editDataInDatabase(currItem, newTitle, newDate);
                }else {
                    addDataIntoDatabase();
                }
                confirm.setVisible(false);
            }
        });
        
        this.confirmationStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        //if the confirm button is not visible, it means that the user has either get an error or has successfully added the data
                        //into the database. so we can also close the window for creating a new assignment
                        if (!confirm.isVisible()) {
                            parentStage.fireEvent(new WindowEvent(parentStage,
                                        WindowEvent.WINDOW_CLOSE_REQUEST));
                            parentStage.close();
                            if (editPage) {
                                EditDataController.confirmationWindowVisible = false;
                            }else {

                                AddNewController.confirmationWindowVisible = false;
                            }
                        }
                    }
                });
    }
    
    
    private void editDataInDatabase (ParsedData currItem, String newTitle, String newDate) {
        ParsedData newData = new ParsedData(currItem.getID(), currItem.getIndex(), newTitle, newDate);
        //by using the editData method in the DBManagement class, begin to edit the data in the database
        int stats = this.DB.editData(currItem.getID(), newTitle, newDate);
        //if the operation returned 1 after the operation, it means that the data has been edited succesfully in the database
        //set the confirm button
        warningLabel.setText("");
        if (stats == 1 ) {
            //change the message from the pop up window
            errorLabel.setText("Succefully Edited the record!!!");
            //replace the data in the global ArrayList that stores all the assignments
            DB.data.set(currItem.getIndex(), newData);
            //update the index of the object as maybe the edited data changed its due date
            DB.updateDataIndex();
        }else {
            errorLabel.setText("Failed to edit the record in Database!!!");
        }
    }
    
    private void addDataIntoDatabase () {
        int result = DB.addData(newTitle, newDate);
        warningLabel.setText("");
        //if the returned value is 1, it means that the operation is succesfull
        if (result == 0 ) {
            //change the message displayed in the window
           errorLabel.setText("Succefully Added the record!!!");
        }else {
            //if the returned value is not 1, it means that the operation failed
            //so we need to change the message to give user a notice on the error
            errorLabel.setText("Failed to add the record to Database!!!");
        }
    }
}

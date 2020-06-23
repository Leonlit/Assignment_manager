/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

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

/**
 * FXML Controller class
 *
 * @author User
 */
public class EditDataController implements Initializable {

    private DBManagement DBObj;
    private ParsedData currItem;
    
    @FXML
    private TextField editTitle;
    
    @FXML
    private DatePicker editDate;
    
    public void setupEditData (ParsedData data, DBManagement DB) {
        DBObj = DB;
        currItem = data;
        editTitle.setText(data.getTitle());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDate date = LocalDate.parse(data.getDueDate(), formatter);
        editDate.setValue(date);
    }
    
    @FXML
    public void editData () {
        Stage editPageStage = new Stage();
        String errText = "";
        String newDueDate = "";
        final String title = editTitle.getText();
        
        if (title.equals("")) {
            errText += "Error: A title is needed!!!\n";
        }
        
        try {
            newDueDate = editDate.getValue().toString();
        }catch (NullPointerException err) {
            errText += "Error: A Due Date is needed for this assignment!!!\n";
        }
        
        if (errText.length() < 1 && Integer.parseInt(newDueDate.substring(0,4)) != MainController.currYear) {
            errText += "Error: This app only support saving task that's in the current year!!!\n";
        }
        
        if(errText.length() < 1) {
            final String dueDate = newDueDate;
        
            BorderPane layout= new BorderPane();
            VBox childs = new VBox();

            Label text = new Label("Confirm to edit \"" + currItem.getTitle() 
                                    + "\",\nDued in " + currItem.getDueDate() + "\n\nInto\n\n"
                                    + title + ",\nDued in " + dueDate + "?");
            
            Button confirm = new Button("Confirm");
            confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    ParsedData newData = new ParsedData(currItem.getID(), currItem.getIndex(), title, dueDate);
                    int stats = DBObj.editData(currItem.getID(), title, dueDate);
                    if (stats > 0 ) {
                       confirm.setDisable(true);
                       text.setText("Succefully Edited the record!!!");
                       DBObj.data.set(currItem.getIndex(), newData);
                   }
                }
            });
            childs.getChildren().addAll(text, confirm);
            text.setPadding(new Insets(10, 10, 20, 20));
            childs.setAlignment(Pos.CENTER);

            layout.setCenter(childs);

            Scene newScene = new Scene(layout, 400, 250);

            editPageStage.setScene(newScene);
            editPageStage.setTitle("Data Edition Confirmation");
            editPageStage.show();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

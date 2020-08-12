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

    private DBManagement DBObj;
    private ParsedData currItem;
    private Stage stage;
    private boolean isManagingRecord = false;
    
    @FXML
    private TextField editTitle;
    
    @FXML
    private DatePicker editDate;
    
    public void setupEditData (ParsedData data, DBManagement DB, Stage stage) {
        DBObj = DB;
        this.stage = stage;
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
            if (!isManagingRecord) {
                isManagingRecord = true;
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
                           confirm.setVisible(false);
                           text.setText("Succefully Edited the record!!!");
                           DBObj.data.set(currItem.getIndex(), newData);
                           DBObj.updateDataIndex();
                        }else {
                            confirm.setVisible(false);
                            text.setText("Failed to edit the record in Database!!!");
                        }
                    }
                });
                editPageStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        if (!confirm.isVisible()) {
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                            stage.close();
                        }
                        isManagingRecord = false;
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
        }else {
            ShowError.showError("Error occured when editing records\n\n", errText);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

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

    private Stage stage;
    private DBManagement DB;
    private boolean managingOneRecord = false;
    
    @FXML
    private TextField createNewTitle;
    
    @FXML
    private DatePicker createNewDueDate;
    
    //
    @FXML
    public void addData () {
        String errorText = "";
        String newDueDate = "";
        try {
            newDueDate = createNewDueDate.getValue().toString();
        }catch (NullPointerException err) {
            errorText += "Error: A Due Date is needed for this assignment!!!\n";
        }
        
        String newTitle = createNewTitle.getText();
        
        if (errorText.length() < 1 && Integer.parseInt(newDueDate.substring(0,4)) != MainController.currYear) {
            errorText += "Error: This app only support saving task that's in the current year!!!\n";
        }
        
        if (newTitle.equals("")) {
            errorText += "Error: A title is needed!!!\n";
        }
        
        if (errorText.length() < 1) {
            if (!managingOneRecord) {
                managingOneRecord = true;
                Stage addPageNotice = new Stage();
                final String title = newTitle;
                final String dueDate = newDueDate;

                BorderPane layout= new BorderPane();
                VBox childs = new VBox();

                Label text = new Label("Confirm to create " + title + ",\n that's due in " + dueDate +"?");
                text.setStyle("-fx-font:14px Georgia;"
                            + "-fx-font-weight:800;");
                Button confirm = new Button("Confirm");
                confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        int result = DB.addData(title, dueDate);
                        if (result > 0 ) {
                           text.setText("Succefully Added the record!!!");
                       }else {
                            text.setText("Failed to add the record to Database!!!");
                        }
                        confirm.setVisible(false);
                    }
                });
                
                addPageNotice.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        if (!confirm.isVisible()) {
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                            stage.close();
                        }
                        managingOneRecord = false;
                        
                    }
                });
                childs.getChildren().addAll(text, confirm);
                text.setPadding(new Insets(10, 10, 20, 20));
                childs.setAlignment(Pos.CENTER);

                layout.setCenter(childs);

                Scene newScene = new Scene(layout, 400, 250);

                addPageNotice.setScene(newScene);
                addPageNotice.setTitle("Create new record Confirmation");
                addPageNotice.show();
            }
        }else {
            ShowError.showError("Error in adding records\n\n", errorText);
        }
    }
    
    public void setupAddingData (DBManagement DB, Stage stage) {
        this.stage = stage;
        this.DB = DB;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author User
 */
public class AddNewController implements Initializable {

    @FXML
    private TextField createNewTitle;
    
    @FXML
    private DatePicker createNewDueDate;
    
    @FXML
    private void getData () {
        String errorText = "";
        String newDueDate = "";
        try {
            newDueDate = createNewDueDate.getValue().toString();
        }catch (NullPointerException err) {
            errorText += "Error: A Due Date is needed for this assignment!!!\n";
        }
        
        String newTitle = createNewTitle.getText();
        
        if (errorText.length() < 1 && Integer.parseInt(newDueDate.substring(0,4)) != MainController.currYear) {
            errorText += "Error: This app only support saving task that's in the same year!!!\n";
        }
        
        if (newTitle.equals("")) {
            errorText += "Error: A title is needed!!!\n";
        }
        
        if (errorText.length() < 1) {
            int success = MainController.DB.addData(newTitle, newDueDate);
        }else {
            System.out.println(newDueDate + ", " + newTitle + ", " + errorText);
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //createNewDueDate.getEditor()
        // TODO
    }    
    
}

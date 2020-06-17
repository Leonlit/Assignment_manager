/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import javafx.scene.Cursor;
import javafx.scene.text.Font;
import java.net.URL;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author User
 */
public class MainController implements Initializable {
    
    @FXML
    private Button addNew;
    
    @FXML
    private GridPane calendar;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        addNew.setText("Hello World!");
    }
    
    public void setupCalendar () {
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        int currMonth = Calendar.getInstance().get(Calendar.MONTH);
        YearMonth yearMonthObject = YearMonth.of(currYear, currMonth + 1);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        
        for (int x = 0; x < daysInMonth; x++) {

            Label temp = new Label("" + (x + 1));
            temp.setFont(new Font("Cambria", 20));
            temp.setCursor(Cursor.HAND);
            
            calendar.add(temp, x%8, x/8);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCalendar();
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import javafx.scene.Cursor;
import javafx.scene.text.Font;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.net.URL;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author User
 */
public class MainController implements Initializable {
    
    private int currMonthNumber = 0;
    
    @FXML
    private Button addNew;
    
    @FXML
    private GridPane calendar;
    
    @FXML
    private Label currMonth;
    
    @FXML
    private void addNewAssignment(ActionEvent event) throws Exception  {
        Stage addNew = new Stage();
        Parent design = FXMLLoader.load(getClass().getResource("addNew.fxml"));
        
        Scene addAssignment = new Scene(design);
        addNew.setScene(addAssignment);
        addNew.setTitle("Add New Assignment Into List");
        addNew.show();
    }
    
    @FXML
    public void nextMonth() {
        if (currMonthNumber < 12) {
            calendar.getChildren().clear();
            currMonthNumber++;
            showMonth(currMonthNumber, currMonth);
            drawCalendar(getDaysInMonth(currMonthNumber));
        }
    }
    
    @FXML
    public void prevMonth() {
        if (currMonthNumber > 1) {
            calendar.getChildren().clear();
            currMonthNumber--;
            showMonth(currMonthNumber, currMonth);
            drawCalendar(getDaysInMonth(currMonthNumber));
        }
    }
    
    private void showAssignmentOfDay (String day) {
        System.out.println(day);
    }
    
    @FXML
    public void viewAll () {
        
    }
    
    public void drawCalendar (int daysInMonth) {
        for (int x = 0; x < daysInMonth; x++) {

            Label temp = new Label("" + (x + 1));
            temp.setFont(new Font("Cambria", 20));
            temp.setCursor(Cursor.HAND);
            temp.setStyle("-fx-background-color: lightgreen;-fx-padding:10.0;-fx-border-width:1.0;-fx-border-color:black;");
            temp.setPrefHeight(45.0);
            temp.setPrefWidth(50.0);
            
            temp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    showAssignmentOfDay(((Label)e.getSource()).getText());
                }
            });
            
            calendar.add(temp, x%7, x/7);
        }
    }
    
    public int getDaysInMonth (int month) {
        int currYear = Calendar.getInstance().get(Calendar.YEAR);
        YearMonth yearMonthObject = YearMonth.of(currYear, month);
        return yearMonthObject.lengthOfMonth();
    }
    
    public int getMonth () {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
    
    public void setupCalendar () {
        currMonthNumber = getMonth();
        showMonth(currMonthNumber, currMonth);
        drawCalendar(getDaysInMonth(currMonthNumber));
    }
    
    public void showMonth (int month, Label obj) {
        String months[] = {"January", "February", "March",
                            "April", "May", "June", "July",
                            "August", "September", "October",
                            "November", "December"
                            };
        obj.setText(months[month - 1]);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCalendar();
    }    
    
}

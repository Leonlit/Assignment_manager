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

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
    
    private int currMonthNumber = 0;
    private int currYear = Calendar.getInstance().get(Calendar.YEAR);
    public static Stage addNew;
    private ArrayList<String> assignments = new ArrayList<String>(); 
    
    @FXML
    private GridPane calendar;
    
    @FXML
    private Label currMonth;
    
    @FXML
    private ScrollPane itemListPane;
    
    @FXML
    private void addNewAssignment(ActionEvent event) throws Exception  {
        addNew = new Stage();
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
            drawCalendar(ParseData.getDaysForMonth(currYear ,currMonthNumber));
        }
    }
    
    @FXML
    public void prevMonth() {
        if (currMonthNumber > 1) {
            calendar.getChildren().clear();
            currMonthNumber--;
            showMonth(currMonthNumber, currMonth);
            drawCalendar(ParseData.getDaysForMonth(currYear ,currMonthNumber));
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
    
    public void setupCalendar () {
        currMonthNumber = ParseData.getCurrMonth();
        showMonth(currMonthNumber, currMonth);
        drawCalendar(ParseData.getDaysForMonth(currYear, currMonthNumber));
    }
    
    public void showMonth (int month, Label obj) {
        String months[] = {"January", "February", "March",
                            "April", "May", "June", "July",
                            "August", "September", "October",
                            "November", "December"
                            };
        obj.setText(months[month - 1]);
    }
    
    String sample[] = {"22052020Test gsfsff fdsfs fdfsd fsfsdfdfdsfd sfdsdfTitle", "22062020Test gsfsff fdsfs fdfsd fsfsdfdfdsfd sfdsdfTitle", "22072020Test Title", "22082020Test Title"};
    
    public void setupItemList() {
        VBox widget = new VBox();
        String status = "";
        String extra = "green;";
        
        for (int x = 0;x<sample.length;x++) {
            ParseData parsedData = new ParseData(sample[x]);
            VBox container = new VBox();
            Label title = new Label (parsedData.getTitle());
            title.setStyle("-fx-font:22px Georgia;"
                            + "-fx-font-weight:800;");
            title.setWrapText(true);
            title.setMinWidth(180.0);
            Label dueDate = new Label(parsedData.getDay() + "/" + parsedData.getMonth() + "/" + parsedData.getYear());
            dueDate.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;");
            
            int daysLeftMore = parsedData.daysLeft();
            if (!parsedData.taskDued()) {    
                status = "left";
                if (daysLeftMore <= 3) {
                   extra = "darkorange;";
                }
            }else {
                status = "dued";
                extra = "red;";
            }
            Label daysLeft = new Label(daysLeftMore + " days " + status);
            daysLeft.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;"
                            + "-fx-text-fill:" + extra);
            
            container.getChildren().addAll(title, dueDate, daysLeft);
            container.setMaxHeight(100.0);
            container.setPadding(new Insets(10, 10, 20, 20));
            container.setStyle("-fx-border-color:black;"
                                + "-fx-border-width:1.0;");
            widget.getChildren().add(container);
        }
        itemListPane.setContent(widget);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupCalendar();
        setupItemList();
    }    
    
}

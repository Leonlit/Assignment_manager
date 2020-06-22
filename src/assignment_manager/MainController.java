/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import java.awt.BorderLayout;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class MainController implements Initializable {
    
    private int currMonthNumber = 0;
    public static int currYear = Calendar.getInstance().get(Calendar.YEAR);
    public static Stage addNew;
    public static boolean stageOpen = false;
    public static DBManagement DB;
    public Stage confirmation;
    //public static ArrayList<ParsedData> assignments = new ArrayList<ParsedData>();
    //public ArrayList<Integer> currItemListID = new ArrayList<Integer>();
    
    @FXML
    public GridPane calendar;
    
    @FXML
    public Label currMonth;
    
    @FXML
    public ScrollPane itemListPane;
    
    @FXML
    private void addNewAssignment(ActionEvent event) throws Exception  {
        if (!stageOpen) {
            addNew = new Stage();
            Parent design = FXMLLoader.load(getClass().getResource("addNew.fxml"));
            
            Scene addAssignment = new Scene(design);
            addNew.setScene(addAssignment);
            addNew.setTitle("Add New Assignment Into List");
            addNew.show();
            addNew.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent we) {
                    refreshMainPage();
                    MainController.stageOpen = false;
                }
            });
            stageOpen = true;
        }
    }
    
    @FXML
    private void viewAll () {
        
    }
    
    @FXML
    private void nextMonth() {
        if (currMonthNumber < 12) {
            calendar.getChildren().clear();
            currMonthNumber++;
            showMonth(currMonthNumber, currMonth);
            drawCalendar(ParsedData.getDaysForMonth(currYear ,currMonthNumber), getTaskForMonth(currMonthNumber));
        }
    }
    
    @FXML
    private void prevMonth() {
        if (currMonthNumber > 1) {
            calendar.getChildren().clear();
            currMonthNumber--;
            showMonth(currMonthNumber, currMonth);
            drawCalendar(ParsedData.getDaysForMonth(currYear ,currMonthNumber), getTaskForMonth(currMonthNumber));
        }
    }
    
    private void showAssignmentOfDay (String day) {
        System.out.println(day);
    }
    
    private ArrayList<ParsedData> getTaskForMonth(int month) {
        ArrayList<ParsedData> items = new ArrayList<ParsedData>();
        for (int x = 0; x< DB.data.size();x++) {
            if (DB.data.get(x).getMonth() == month) items.add(DB.data.get(x));
        }
        return items;
    }
    
    private void showMonth (int month, Label obj) {
        String months[] = {"January", "February", "March",
                            "April", "May", "June", "July",
                            "August", "September", "October",
                            "November", "December"
                            };
        obj.setText(months[month - 1]);
    }
    
    public static Button confirm ;
    public static Label text;
    private void deleteData (String ID, String title) {
        confirmation = new Stage();
        
        BorderLayout layout= new BorderLayout();
        VBox childs = new VBox();
        
        text = new Label("Confirm to delete " + title + "?");
        confirm = new Button("Confirm");
        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
               int stats = DB.deleteData(Integer.parseInt(ID));
               if (stats > 0 ) {
                   confirm.setDisable(true);
                   text.setText("Succefully Deleted the record!!!");
               }
            }
        });
        childs.getChildren().addAll(text, confirm);
        childs.setPadding(new Insets(10, 10, 20, 20));
        
        layout.addLayoutComponent(childs, BorderLayout.CENTER);
        
        Scene newScene = new Scene(layout, 250, 100);
        
        confirmation.setScene(newScene);
        confirmation.setTitle("Deletion Confirmation");
        confirmation.show();
    }
    
    private void drawCalendar (int daysInMonth, ArrayList<ParsedData> taskOnDay) {
        ArrayList <Integer> taskDays = new ArrayList<Integer>();
        for (int x = 0 ;x< taskOnDay.size();x++) {
            taskDays.add(taskOnDay.get(x).getDay());
        }
        
        for (int x = 0; x < daysInMonth; x++) {

            Label temp = new Label("" + (x + 1));
            temp.setFont(new Font("Cambria", 20));
            temp.setCursor(Cursor.HAND);
            temp.setStyle("-fx-background-color: lightgrey;"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;");
            temp.setPrefHeight(45.0);
            temp.setPrefWidth(50.0);
            String taskColor = "lightGreen";
            
            if (taskDays.contains(x+1)) {
                ParsedData currItem = taskOnDay.get(taskDays.indexOf(x+1));
                if (currItem.daysLeft() < 3) {
                    taskColor = "#FFA500";
                }
                if (currItem.daysLeft() <= 0) {
                    taskColor = "#DC143C";
                }
                temp.setStyle("-fx-background-color:" + taskColor + ";"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;");
                temp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        showAssignmentOfDay(((Label)e.getSource()).getText());
                    }
                });
                
            }
            calendar.add(temp, x%7, x/7);
        }
    }
    
    private void setupCalendar () {
        currMonthNumber = ParsedData.getCurrMonth();
        showMonth(currMonthNumber, currMonth);
        drawCalendar(ParsedData.getDaysForMonth(currYear, currMonthNumber), getTaskForMonth(currMonthNumber));
    }
        
    private void setupItemList(ArrayList<ParsedData> data) {
        VBox widget = new VBox();
        String status = "";
        String extra = "";
        
        for (int x = 0;x<data.size();x++) {
            extra = "green;";
            final ParsedData currData = data.get(x);
            VBox container = new VBox();
            Label title = new Label (currData.getTitle());
            title.setStyle("-fx-font:22px Georgia;"
                            + "-fx-font-weight:800;");
            title.setWrapText(true);
            title.setMinWidth(180.0);
            Label dueDate = new Label(currData.getDay() + "/" + currData.getMonth() + "/" + currData.getYear());
            dueDate.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;");
            
            int daysLeftMore = currData.daysLeft();
            if (!currData.taskDued()) {    
                status = "left";
                if (daysLeftMore == 0) {
                    status = "left - today!";
                    extra = "red;";
                }else if (daysLeftMore <= 3) {
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
            
            BorderPane subSection = new BorderPane();
            HBox subBtn = new HBox();
            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");
            deleteBtn.setId("" + currData.getID());
            subSection.setLeft(daysLeft);
            
            deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    deleteData(((Button)(e.getSource())).getId(), currData.getTitle());
                }
            });
            
            subBtn.setSpacing(10);
            
            subBtn.getChildren().addAll(editBtn, deleteBtn);
            subSection.setRight(subBtn);
            
            container.getChildren().addAll(title, dueDate, subSection);
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
        DB = new DBManagement();
        refreshMainPage();
    }
    
    private void refreshMainPage () {
        calendar.getChildren().clear();
        itemListPane.setContent(null);
        setupCalendar();
        setupItemList(DB.data);
    }
    
}

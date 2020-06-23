/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment_manager;

import java.io.IOException;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class MainController implements Initializable {
    
    private int currMonthNumber = 0;
    private boolean stageOpen = false;
    private boolean showingAll = false;
    private boolean editOpen = false;
    
    public static DBManagement DB;
    public static int currYear = Calendar.getInstance().get(Calendar.YEAR);
    
    @FXML
    private GridPane calendar;
    
    @FXML
    private Label currMonth, assignmentNumnber, noticeTitle, noticeDueDate, noticeDayLeft;
    
    @FXML
    private ScrollPane itemListPane;
    
    @FXML
    private void addNewAssignment(ActionEvent event) throws IOException  {
        if (!stageOpen) {
            Stage addNew = new Stage();
            Parent design = FXMLLoader.load(getClass().getResource("addNew.fxml"));
            
            Scene addAssignment = new Scene(design);
            addNew.setScene(addAssignment);
            addNew.setTitle("Add New Assignment Into List");
            addNew.show();
            addNew.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent we) {
                    refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
                    stageOpen = false;
                }
            });
            stageOpen = true;
        }
    }
    
    @FXML
    private void viewAll () {
        showingAll = true;
        refreshMainPage(DB.data, currMonthNumber);
    }
    
    @FXML
    private void showCurrMonth() {
        showingAll = false;
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
    }
    
    @FXML
    private void nextMonth() {
        if (currMonthNumber < 12) {
            calendar.getChildren().clear();
            currMonthNumber++;
            refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        }
    }
    
    @FXML
    private void prevMonth() {
        if (currMonthNumber > 1) {
            calendar.getChildren().clear();
            currMonthNumber--;
            refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        }
    }
    
    private void showAssignmentOfDay (int day) {
        showingAll = false;
        ArrayList<ParsedData> thisMonthTask = getTaskForMonth(currMonthNumber);
        ArrayList<ParsedData> certainDayTask = new ArrayList<ParsedData>();
        for (int x = 0;x<thisMonthTask.size();x++) {
            ParsedData currItem = thisMonthTask.get(x);
            if (currItem.getDay() == day) {
                certainDayTask.add(currItem);
            }
        }
        refreshMainPage(certainDayTask, currMonthNumber);
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
    
    private void drawCalendar (int daysInMonth, ArrayList<ParsedData> taskOnDay) {
        ArrayList <Integer> taskDays = new ArrayList<Integer>();
        for (int x = 0 ;x< taskOnDay.size();x++) {
            taskDays.add(taskOnDay.get(x).getDay());
        }
        
        for (int x = 0; x < daysInMonth; x++) {
            String taskColor = "";
            Label temp = new Label("" + (x + 1));
            temp.setFont(new Font("Cambria", 20));
            temp.setCursor(Cursor.HAND);
            
            temp.setStyle("-fx-background-color:lightgrey;"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;");
            temp.setPrefHeight(45.0);
            temp.setPrefWidth(50.0);
            
            if (x == ParsedData.getCurrDayOfMonth() && currMonthNumber == ParsedData.getCurrMonth()) {
                temp.setStyle(temp.getStyle() + "-fx-text-fill:blue;-fx-background-color:white;");
            }
            
            taskColor = "lightgreen";
            
            if (taskDays.contains(x+1)) {
                ParsedData currItem = taskOnDay.get(taskDays.indexOf(x+1));
                if (currItem.daysLeft() < 3) {
                    taskColor = "#FFA500";
                }
                if (currItem.taskDued() || currItem.daysLeft() == 0) {
                    taskColor = "#DC143C";
                }
                temp.setStyle("-fx-background-color:" + taskColor + ";"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;");
                temp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        showAssignmentOfDay(Integer.parseInt(((Label)e.getSource()).getText()));
                    }
                });
                
            }
            calendar.add(temp, x%7, x/7);
        }
    }
    
    private void setupCalendar (int month) {
        //the month, the label obj
        showMonth(month, currMonth);
        drawCalendar(ParsedData.getDaysForMonth(currYear, currMonthNumber), getTaskForMonth(month));
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
            
            Label daysLeft = new Label(Math.abs(daysLeftMore) + " days " + status);
            daysLeft.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;"
                            + "-fx-text-fill:" + extra);
            
            BorderPane subSection = new BorderPane();
            HBox subBtn = new HBox();
            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");
            deleteBtn.setId("" + currData.getID());
            subSection.setLeft(daysLeft);
            
            editBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    editData(currData);
                }
            });
            
            deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    deleteData(currData);
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
    
    private void deleteData (ParsedData data) {
        Stage confirmation = new Stage();
        
        BorderPane layout= new BorderPane();
        VBox childs = new VBox();
        
        Label text = new Label("Confirm to delete \"" + data.getTitle() + "\"?");
        Button confirm = new Button("Confirm");
        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
               int stats = DB.deleteData(data.getID());
               if (stats > 0 ) {
                   confirm.setDisable(true);
                   text.setText("Succefully Deleted the record!!!");
                   DB.data.remove(data.getIndex());
                   refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
               }
            }
        });
        childs.getChildren().addAll(text, confirm);
        text.setPadding(new Insets(10, 10, 20, 20));
        childs.setAlignment(Pos.CENTER);
        
        layout.setCenter(childs);
        
        Scene newScene = new Scene(layout, 250, 100);
        
        confirmation.setScene(newScene);
        confirmation.setTitle("Deletion Confirmation");
        confirmation.show();
    }
    
    private void editData (ParsedData data) {
        if (!editOpen) {
            try {
                Stage editData = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("editData.fxml"));
                Parent root = loader.load();

                EditDataController controller = loader.getController();
                //Pass whatever data you want. You can have multiple method calls here
                controller.setupEditData(data, DB);

                editData.setScene(new Scene(root));
                editData.setTitle("Add New Assignment Into List");
                editData.show();
                editData.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent we) {
                        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
                        editOpen = false;
                    }
                });
                editOpen = true;
            }catch (IOException ex) {
                ShowError.showError("Error when adding new record!!!\n\n",ex.getMessage());
            }
        }
    }
    
    private void assignmentAlert (ParsedData data) {
        noticeTitle.setText(data.getTitle());
        noticeDueDate.setText("" + data.getDay() + "/" + data.getMonth() + "/" + data.getYear());
        String extra = "black", text = "";
        if (data.taskDued()) {
            extra = "red";
            text = "Dued " + Math.abs(data.daysLeft()) + " days ago";
        }else {
            extra = "green";
            text = "Due in " + data.daysLeft();
        }
        noticeDayLeft.setStyle("-fx-text-fill:" + extra);
        noticeDayLeft.setText(text);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DB = new DBManagement();
        currMonthNumber = ParsedData.getCurrMonth();
        
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
    }
    
    private void refreshMainPage (ArrayList<ParsedData> data, int month) {
        calendar.getChildren().clear();
        itemListPane.setContent(null);
        setupCalendar(month);
        assignmentNumnber.setText("#" + DB.data.size());
        if (showingAll) {
            data = DB.data;
        }
        
        if (DB.data.size() != 0) {
            assignmentAlert(DB.data.get(0));
        }
        setupItemList(data);
    }
    
}

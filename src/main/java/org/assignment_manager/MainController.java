package org.assignment_manager;

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
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;

public class MainController implements Initializable {
    
    private int currMonthNumber = 0;
    private boolean stageOpen = false;      //limiting user so one window only can be opened for creating new assignment
    private boolean showingAll = false;     //did user choose to see all assignment available?
    private boolean editOpen = false;       //limiting only one edit assignment window to be created at one time
    private boolean openingDeleteWindow = false;
    
    public static DBManagement DB;
    public static int currPresentYear;
    public static int currYear;
    
    @FXML
    private GridPane calendar;
    
    @FXML
    private Label currMonth, assignmentNumnber, noticeTitle, noticeDueDate, noticeDayLeft, currYearLabel;
    
    @FXML
    private ScrollPane itemListPane;


    //when the add new assignment button is clicked    
    @FXML
    private void addNewAssignment(ActionEvent event) throws IOException  {
        if (!stageOpen) {
            Stage addNew = new Stage();
            Stage popUpStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addNew.fxml"));
            Parent root = loader.load();

            AddNewController controller = loader.getController();
            //passing database object, the stage object to the cotroller file and the stage for pop up
            controller.setupAddingData(DB, addNew, popUpStage);
            
            Scene addAssignment = new Scene(root);
            addNew.setScene(addAssignment);
            addNew.setTitle("Add New Assignment Into List");
            addNew.show();
            //when this window is closed, refresh the main page, just in case something changed
            addNew.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent we) {
                    popUpStage.close();
                    refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
                    stageOpen = false;
                }
            });
            stageOpen = true;
        }
    }
    
    //set the status to view all assignment available
    @FXML
    private void viewAll () {
        showingAll = true;
        refreshMainPage(DB.data, currMonthNumber);
    }
    
    //show the assignments that's due this month
    @FXML
    private void showCurrMonth() {
        showingAll = false;
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
    }
    
    //change calendar view to the next month
    @FXML
    private void nextMonth() {
        if (currMonthNumber < 12) {
            currMonthNumber++;
            refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        }else {
            currMonthNumber = 1;
            nextYear();
        }
    }
    
    //change calendar view to previos month
    @FXML
    private void prevMonth() {
        if (currMonthNumber > 1) {
            currMonthNumber--;
            refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        }else {
            currMonthNumber = 12;
            prevYear();
        }
    }
    
    //change calendar view to next year
    @FXML
    private void nextYear() {
        currYear++;
        DB = new DBManagement(currYear);
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        replaceCurrYearLabelText(currYear);
    }
    
    //change calendar view to previous year
    @FXML
    private void prevYear() {
        currYear--;
        DB = new DBManagement(currYear);
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
        replaceCurrYearLabelText(currYear);
    }
    
    //Show assignments for certain day
    // @param day - an int parameter that specify which day of the month to display the assignment
    private void showAssignmentOfDay (int day) {
        showingAll = false;
        //getting assignments available this month
        ArrayList<ParsedData> thisMonthTask = getTaskForMonth(currMonthNumber);
        
        ArrayList<ParsedData> certainDayTask = new ArrayList<ParsedData>();
        //looping through the result returned from getTaskForMonth
        //get the assignment for the specified day
        //refer to Data.java for more info regarding what getDay() does.
        for (int x = 0;x<thisMonthTask.size();x++) {
            ParsedData currItem = thisMonthTask.get(x);
            if (currItem.getDay() == day) {
                certainDayTask.add(currItem);
            }
        }
        refreshMainPage(certainDayTask, currMonthNumber);
    }
    
    //get the task for certain month
    // @param month - the month of the assignments that we want to view
    private ArrayList<ParsedData> getTaskForMonth(int month) {
        ArrayList<ParsedData> items = new ArrayList<ParsedData>();
        //by getting all the data from the assignment list, get item that has the month same with the specified one
        for (int x = 0; x< DB.data.size();x++) {
            if (DB.data.get(x).getMonth() == month) items.add(DB.data.get(x));
        }
        return items;
    }
    
    //by using the number of months, display the month using words.
    // @param month - integer that carrie which month is it
    private void showMonth (int month, Label obj) {
        String months[] = {"January", "February", "March",
                            "April", "May", "June", "July",
                            "August", "September", "October",
                            "November", "December"
                            };
        obj.setText(months[month - 1]);
    }
    
    //drawing out the calendar according to the available days and which day has assignment/s available
    private void drawCalendar (int daysInMonth, ArrayList<ParsedData> taskOnDay) {
        ArrayList <Integer> taskDays = new ArrayList<Integer>();
        //searching for which day that has assignments assigned to them
        for (int x = 0 ;x< taskOnDay.size();x++) {
            taskDays.add(taskOnDay.get(x).getDay());
        }
        
        //looping through the days of month to change the days backgrond color according to their stats
        for (int x = 0; x < daysInMonth; x++) {
            String taskColor = "";
            Label temp = new Label("" + (x + 1));
            temp.setFont(new Font("Cambria", 20));
            temp.setCursor(Cursor.HAND);
            
            //default design for all days item in the calendar GridPane
            temp.setStyle("-fx-background-color:lightgrey;"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;");
            temp.setPrefHeight(45.0);
            temp.setPrefWidth(50.0);
            boolean currPresentDay = false;
            
            //if the current index is the current day according to user machine, change the style of the grid item
            //to indicate the differences
            if (x == ParsedData.getCurrDayOfMonth() - 1 && 
                currMonthNumber == ParsedData.getCurrMonth() &&
                currYear == currPresentYear) {
                currPresentDay = true;
                temp.setStyle(temp.getStyle() + "-fx-text-fill:blue;"
                                                + "-fx-background-color:white;");
            }
            
            //now to find those task that's has been due or still available
            //light green will means that the task is still available and haven't due yet
            taskColor = "lightgreen";
            String extra = "";
            //if this statement is true, then it means that this particular day has one or more assignment
            //this is because we've stored the days that has assignment. so we can just skip the days that 
            //don't have any assignment
            if (taskDays.contains(x+1)) {
                ParsedData currItem = taskOnDay.get(taskDays.indexOf(x+1));
                //if the due date for the assignment is < 4, means is quite near
                //so we'll give it a orange color for its background color
                if (currItem.daysLeft() < 4) {
                    taskColor = "#ffad34";
                }
                //but if the task has already due, we'll give it a red color for its background color
                if (currItem.taskDued() || currItem.daysLeft() == 0) {
                    taskColor = "#DC143C";
                    extra = "-fx-text-fill:white;";
                }  
                
                temp.setStyle("-fx-background-color:" + taskColor + ";"
                            + "-fx-padding:10.0;"
                            + "-fx-border-width:1.0;"
                            + "-fx-border-color:black;"
                            + extra);
                
                if (currPresentDay) {
                    temp.setStyle(temp.getStyle() + "-fx-text-fill:red;"
                                                + "-fx-background-color:white;");
                }
                
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
    
    //setting up the calendar by calling the showMonth() and drawCalendar() method
    private void setupCalendar (int month) {
        showMonth(month, currMonth);     //the month, the label obj
        //
        drawCalendar(ParsedData.getDaysForMonth(currYear, currMonthNumber), getTaskForMonth(month));
    }
    
    //setting up the list of the assignment, the delete and edit button
    // @param data - the arrayList that contain all the details for the assignment that needs to be displayed
    //               since the program has two mode for showing Assignment, current month and all of the assignment.
    private void setupItemList(ArrayList<ParsedData> data) {
        VBox widget = new VBox();
        String status = "";
        String extra = "";
        
        //the assignments due date color will also be following the one as the calendar one's
        //where green means the due date is more than 3 days from today
        //orange means the assignment has less than or equal to 3 days more before meeting its due date
        //while lastly red means that the assignment has already due 
        //in here we'll also be changing the status of the first assignment according to it's due date
        for (int x = 0;x<data.size();x++) {
            extra = "green;";
            final ParsedData currData = data.get(x);
            VBox container = new VBox();
            Label title = new Label (currData.getTitle());
            title.setStyle("-fx-font:22px Georgia;"
                            + "-fx-font-weight:800;");
            title.setWrapText(true);
            title.setMinWidth(180.0);
            //constructing the label for the due date
            Label dueDate = new Label(currData.getDay() + "/" + currData.getMonth() + "/" + currData.getYear());
            dueDate.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;");
            
            String textColor[] = {"red;", "darkorange;"};
            String message = "";
            int daysLeftMore = currData.daysLeft();
            
            if (!currData.taskDued()) {
                if (daysLeftMore < 4) {
                   extra = textColor[1];
                }
                message = Math.abs(daysLeftMore) + " days left";
            }else{
                extra = textColor[0];
                message = "Due " + Math.abs(daysLeftMore) + " days ago";
                if (daysLeftMore == 0) {
                    message = "Due Today";
                }
            }
            
            //creating a label to change the status of the assignment at the left hand side of the window
            Label daysLeft = new Label(message);
            daysLeft.setStyle("-fx-font:15px System;"
                            + "-fx-font-weight:900;"
                            + "-fx-text-fill:" + extra);
            
            BorderPane subSection = new BorderPane();
            HBox subBtn = new HBox();
            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");
            deleteBtn.setId("" + currData.getID());

            //changing the status of the assignment on the left hand side of the screen
            subSection.setLeft(daysLeft);
            
            //setting up a on mouse click handler to call editData() method when triggered
            //to trigger the edit data window
            editBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    editData(currData);
                }
            });
            
            //setting a on mouse click handler to call deleteData() method when triggered to trigger the 
            //delete data window.
            deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (!openingDeleteWindow) {
                        openingDeleteWindow = true;
                        deleteData(currData);
                    }
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
    

    //triggers the delete data window that let user to delete data from the database    
    //When deleting the data we need to first ask the user if he really want to delete it.
    //if he or she really confirmed that the data can be deleted then proceed with the deletion process
    // @param data - a ParsedData object which contains the data we need to delete the data from database
    //              #refer to ParsedData.java and DBManagement.java for more information
    private void deleteData (ParsedData data) {
        Stage confirmation = new Stage();
        
        BorderPane layout= new BorderPane();
        VBox childs = new VBox();
        
        Label text = new Label("Confirm to delete \"" + data.getTitle() + "\"? \n[Double click the button to delete the Assignment]");
        Button confirm = new Button("Confirm");
        
        String boxPadding = "-fx-padding: 10 20 10 20;";
        text.setStyle("-fx-font:14px Georgia;"
                            + "-fx-font-weight:800;"
                            + "-fx-max-witdh: 100;"
                            + boxPadding);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrapText(true);
        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                //delete data from database using the data ID
                int stats = DB.deleteData(data.getID());
                if (stats == 0 ) {
                    text.setText("Succefully Deleted the record!!!");
                    //if the stats of the operation is 1 that means that the operation is succesfull
                    confirm.setDisable(true);
                    //then we also need to delete the data from the global ArrayList
                    DB.data.remove(data.getIndex());
                    //then refresh the page as the content of the arrayList has been changed
                    refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
               }
            }
        });
        
        confirmation.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                openingDeleteWindow = false;
            }
        });
        childs.getChildren().addAll(text, confirm);
        text.setPadding(new Insets(10, 10, 20, 20));
        childs.setAlignment(Pos.CENTER);
        
        layout.setCenter(childs);
        
        Scene newScene = new Scene(layout, 300, 200);
        
        confirmation.setScene(newScene);
        confirmation.setTitle("Deletion Confirmation");
        confirmation.show();
    }
    
    //construct a new window for editing the existing data by opening the editData.fxml
    //When users want to edit the data for an assignment, we need to get the newly provided title 
    //as well as the new due date. If any of them are empty, the previous data would be used
    // @param data - a ParsedData object which contains the data we need to edit the data from database
    //              #refer to ParsedData.java and DBManagement.java for more information
    private void editData (ParsedData data) {
        if (!editOpen) {
            try {
                Stage editData = new Stage();
                Stage popUp = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("editData.fxml"));
                Parent root = loader.load();

                EditDataController controller = loader.getController();
                //passing neccesarry data to the editData.fxml controller
                //we pass in the object that has the data that we want to delete, the database object that 
                //contains method to handle database and lastly the stage object(handling events)
                controller.setupEditData(data, DB, editData, popUp);

                editData.setScene(new Scene(root));
                editData.setTitle("Edit Assignment Records");
                editData.show();
                editData.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    //so no matter what is changed on the assignment data, we need to refresh the assignment list
                    //as well as change the editOpen to false as we exited the previous window.
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
        String color = "black", text = "";
        if (data.daysLeft() < 0) {
            color = "red";
            text = "Due " + Math.abs(data.daysLeft()) + " days ago";
        }else if (data.daysLeft() == 0) {
            color = "red";
            text = "Due Today!!!";
        }else {
            if (data.daysLeft() < 4) {
                color = "darkorange";
            }else {
                color = "green";
            }
            text = "Due in " + data.daysLeft() + "days";
        }
        
        noticeDayLeft.setStyle("-fx-text-fill:" + color);
        noticeDayLeft.setText(text);
    }
    
    //when there's no assingment, display this message on the page status
    //as well as make empty the due date and days left lable.
    public void assignmentAlert () {
        noticeTitle.setText("No Assignment available");
        noticeDueDate.setText("");
        noticeDayLeft.setText("");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initialising a database object for dealing with database operation like delete, add, update.
        currPresentYear = Calendar.getInstance().get(Calendar.YEAR);
        currYear = currPresentYear;
        DB = new DBManagement(currYear);
        currMonthNumber = ParsedData.getCurrMonth();
        replaceCurrYearLabelText(currYear);
        //refresh the main page
        refreshMainPage(getTaskForMonth(currMonthNumber), currMonthNumber);
    }
    
    private void replaceCurrYearLabelText(int year) {
        currYearLabel.setText(Integer.toString(year));
    }
    
    //this mehtod refresh everthing in the main window
    private void refreshMainPage (ArrayList<ParsedData> data, int month) {
        //clearing the grid pane, so that we can rewrite the pane content
        calendar.getChildren().clear();
        //then set the content of the itemListPanel to null
        itemListPane.setContent(null);
        //then we setup the calendar
        setupCalendar(month);
        //displaying the number of assignments available
        assignmentNumnber.setText("#" + DB.data.size());
        if (showingAll) {
            data = DB.data;
        }
        try {
            //try and get the first assignment from the ArrayList
            assignmentAlert(DB.getEarliest());
        }catch (NullPointerException | IndexOutOfBoundsException ex) {
            //no rows in the table
            assignmentAlert();
        }finally {
            //setup the item list 
            setupItemList(data);
        }
    }
    
}

package org.assignment_manager;

import java.time.YearMonth;
import java.util.Calendar;


//since we'll be using many different aspect of a data to store, display, edit, delete and add
//we need a class that holds more advanced data like times left before a assignment is due
public class ParsedData extends Data{
    private boolean isDued;
    
    // @param ID      - the id of the assignment in integer
    // @param index   - the index of the assignment in a arraylist
    // @param title   - the title for the assignment in string
    // @param duedate - due date for the assignment in string
    public ParsedData (int ID, int index, String title, String dueDate) {
        super(ID, index, title, dueDate);
    }
        
    public Boolean taskDued () {
        return isDued;
    }
    
    public int daysLeft () {
        int days = 0;                        //holding the number of the days left assignment
        int theMonth = getMonth();           //the assignment due date month
        int theDay = getDay();               //the day of the assignment due
        int currMonth = getCurrMonth();      //current month number
        int currDay = getCurrDayOfMonth();   //current day of the month
        int monthGap = theMonth - currMonth; // excluding current month
        int presentYear = MainController.currPresentYear;
        int taskYear = getYear();
        int yearGap = taskYear - presentYear;
        
        //if the assignment due this month
        if (yearGap == 0) {
            if (monthGap == 0) {
                //get the days left 
                days = theDay - currDay;
                //determine if it's already due
                if (days < 0) isDued = true;
            }else if (monthGap > 0) {
                //if the month gap is more than 0, need to calculate the days left for the month/s
                //by calling the getDaysForMonth() method to get how many days this month have
                int currMonthDays = getDaysForMonth(getYear(), currMonth);
                //month need to be decreased 1 because need to exclude the last month of the date
                //we only need to delete one as for example the due date for the assignment is at 
                //august while the current date is still at june. 8-6 = 2 altough its 3 months in total
                //then we also added the days left for the last month
                days = getDaysForTheseMonth(currMonth, monthGap - 1) + theDay;
                //adding the current month days left, since maybe few days have passed
                days += currMonthDays - currDay;
            }else {
                //if the month gap is less than 0 it means that the assignment is already due
                //the system will then assign the current day number to the variable days
                //and then get the days for previos month/s
                days = currDay;
                days += getDaysForTheseMonth(currMonth, monthGap) - theDay;
                days = days * -1;
            }
        }else {
            days = getDaysFromYearGap(presentYear, yearGap);
        }
        if (days <= 0) {
            isDued = true;
        }
        return days;
    }
    
    private int getDaysFromYearGap (int presentYear, int yearGap) {
        int days = 0;
        int currMonth = getCurrMonth();
        int taskDueMonth = getMonth();
        int taskDueAtDay = getDay();
        int yearRange = Math.abs(yearGap);
        if (yearGap < 0) {
            days -= getDaysRangeInPresentYear(presentYear, currMonth, getCurrDayOfMonth(), true);
            for (int index = 0;index< yearRange;index++) {
                if (index == yearRange - 1 ) {
                    days -= getDaysRangeInPresentYear(presentYear - (index + 1), taskDueMonth, taskDueAtDay, false);
                }else {
                    days -= getDaysFromYear(presentYear - (index + 1));
                }
            }
        }else if (yearGap > 0) {
            days += getDaysRangeInPresentYear(presentYear, currMonth, getCurrDayOfMonth(), false);
            for (int index = 0;index< yearRange;index++) {
                if (index == yearRange - 1 ) {
                    days += getDaysRangeInPresentYear(presentYear - (index + 1), taskDueMonth, taskDueAtDay, true);
                }else {
                    days += getDaysFromYear(presentYear + (index + 1));
                }
            }
        }
        return days;
    }
    
    //get days for a year  (leap and normal year)
    private int getDaysFromYear (int year) {
        boolean leapYear;
        if(year % 4 == 0){
            if( year % 100 == 0){
                // year is divisible by 400, hence the year is a leap year
                if ( year % 400 == 0) {
                    leapYear = true;
                }else{
                    leapYear = false;
                }
            }else {
                leapYear = true;
            }
        }else {
            leapYear = false;
        }
        return leapYear ? 366 : 365;
    }
    
    
    private int getDaysRangeInPresentYear (int presentYear, int currMonth, int comparedWithDay ,boolean passedDay) {
        int days = 0;
        if (passedDay) {
            days += comparedWithDay;
            for (int index=1;index < currMonth;index++){
                days += getDaysForMonth(presentYear, index);
            }
        }else {
            days += getDaysForMonth(presentYear, currMonth) - comparedWithDay;
            for (int index=currMonth + 1;index <= 12;index++){
                days += getDaysForMonth(presentYear, index);
            }
        }
        return days;
    }
    
    public static int getCurrDayOfMonth () {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getCurrMonth () {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
    
    public static int getDaysForMonth (int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth(); 
    }
    
    //getting the days of certain month/s
    private int getDaysForTheseMonth (int currMonth, int offset) {
        int totalDays = 0;
        int year = getYear();
        if (offset >= 0) {
            //getting the available days for month/s to come
            for (int x = 1;x<=offset;x++) {
                totalDays += getDaysForMonth(year, currMonth + x);
            }
        }else {
            //when the assignment is due
            for (int x = -1;x>=offset;x--) {
                totalDays += getDaysForMonth(year, currMonth + x);
            }
        }
        return totalDays;
    }
    
    public static boolean checkIfTaskSetToPassedDate (String dueDate) {
        boolean result = false;
        int presentDayOfMonth = ParsedData.getCurrDayOfMonth();
        int presentMonth = ParsedData.getCurrMonth();
        int newTaskYear = Integer.parseInt(dueDate.substring(0, 4));
        int newTaskMonth = Integer.parseInt(dueDate.substring(5, 7));
        int newTaskDay = Integer.parseInt(dueDate.substring(8, 10));
        if (newTaskYear <= MainController.currPresentYear) {
            if (newTaskYear < MainController.currPresentYear) {
                result = true;
            }else if (newTaskMonth < presentMonth) {
                result = true;
            }else if (newTaskDay < presentDayOfMonth) {
                result = true;
            }
        }
        return result;
    }
}

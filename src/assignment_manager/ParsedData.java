package assignment_manager;

import java.time.YearMonth;
import java.util.Calendar;

public class ParsedData {
    private String dueDate[];
    private String title;
    private int ID;
    private int index;
    private boolean isDued;
    
    public ParsedData (int ID, int index, String title, String dueDate) {
        this.dueDate = dueDate.split("-");
        this.title = title;
        this.ID = ID;
        this.index = index;
    }
    
    public int getID () {
        return ID;
    }
    
    public int getDay () {
        return Integer.parseInt(dueDate[2]);
    }
    
    public void updateIndex(int newIndex) {
        index = newIndex;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getDueDate () {
        return String.join("-", dueDate);
    }
    
    public int getMonth () {
        return Integer.parseInt(dueDate[1]);
    }
    
    public int getYear () {
        return Integer.parseInt(dueDate[0]);
    }
    
    public String getTitle () {
        return title;
    }
    
    public Boolean taskDued () {
        return isDued;
    }
    
    public int daysLeft () {
        int days = 0;
        int theMonth = getMonth();
        int theDay = getDay();
        int currMonth = getCurrMonth();
        int currDay = getCurrDayOfMonth();
        int monthGap = theMonth - currMonth; // excluding current month
        
        if (monthGap == 0) {
            days = theDay - currDay;
            if (days < 0) isDued = true;
        }else if (monthGap > 0) {
            int currMonthDays = getDaysForMonth(getYear(), currMonth);
            //month decrease 1 because need to exlude the last month of the date
            days = getDaysForTheseMonth(currMonth, monthGap - 1) + theDay; 
            days += currMonthDays - currDay;
        }else {
            days = currDay;
            days += getDaysForTheseMonth(currMonth, monthGap) - theDay;
            isDued = true;
            days = days * -1;
        }
        return days;
    }
    
    private int getCurrDayOfMonth () {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getCurrMonth () {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
    
    public static int getDaysForMonth (int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth(); 
    }
    
    private int getDaysForTheseMonth (int currMonth, int offset) {
        int totalDays = 0;
        int year = getYear();
        if (offset >= 0) {
            for (int x = 1;x<=offset;x++) {
                totalDays += getDaysForMonth(year, currMonth + x);
            }
        }else {
            for (int x = -1;x>=offset;x--) {
                totalDays += getDaysForMonth(year, currMonth + x);
            }
        }
        return totalDays;
    }
}

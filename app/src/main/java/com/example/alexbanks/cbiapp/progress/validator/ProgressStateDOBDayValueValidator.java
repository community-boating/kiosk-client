package com.example.alexbanks.cbiapp.progress.validator;

import com.example.alexbanks.cbiapp.input.CustomInputManager;
import com.example.alexbanks.cbiapp.progress.ProgressState;
import com.example.alexbanks.cbiapp.progress.newguest.ProgressStateNewGuestDOB;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProgressStateDOBDayValueValidator implements ProgressStateValueValidator {
    @Override
    public String isValueValid(String value) {
        Integer dayValue;
        try{
            dayValue=Integer.parseInt(value);
        }catch(Exception e){
            return "Enter a valid day";
        }
        if(dayValue <= 0)
            return "Enter a valid day (starting at 1)";
        //TODO this is also pretty janky, make it better at some point
        ProgressState active = CustomInputManager.getActiveProgressState();
        String year = active.get(ProgressStateNewGuestDOB.KEY_DOB_YEAR);
        String month = active.get(ProgressStateNewGuestDOB.KEY_DOB_MONTH);
        Integer yearValue;
        Integer monthValue;
        try{
            yearValue=Integer.parseInt(year);
            monthValue=Integer.parseInt(month);
        }catch(Exception e){
            return null;
        }
        if(monthValue > 0) {
            Calendar dayCheck = new GregorianCalendar(yearValue, monthValue - 1, 1);
            int maxDay = dayCheck.getActualMaximum(Calendar.DAY_OF_MONTH);
            if(dayValue > maxDay)
                return "Enter a valid day (this day never occurred)";
            Calendar calendar = new GregorianCalendar(yearValue, monthValue - 1, dayValue);
            Calendar current = Calendar.getInstance();
            int y = current.get(Calendar.YEAR);
            y -= 18;
            current.set(Calendar.YEAR, y);
            if(calendar.after(current))
                return "You must be over 18";
        }
        return null;
    }
}

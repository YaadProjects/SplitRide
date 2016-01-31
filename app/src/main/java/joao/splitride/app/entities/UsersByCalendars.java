package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 24-01-2016.
 */
@ParseClassName("UsersByCalendar")
public class UsersByCalendars extends ParseObject{

    public UsersByCalendars(){

    }

    public void setUserID(String userID){
        put("UserID", userID);
    }

    public void setCalendarID(String calendarID){

        put("CalendarID", calendarID);
    }

    public void setDefault(boolean isDefault){
        put("Default", isDefault);
    }

    public String getUserID(){

        return getString("UserID");
    }

    public String getCalendarID(){

        return getString("CalendarID");
    }

    public boolean getDefault(){
        return getBoolean("Default");
    }
}

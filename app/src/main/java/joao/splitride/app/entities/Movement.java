package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by joaoferreira on 29/04/16.
 */
@ParseClassName("Movements")
public class Movement extends ParseObject {

    public Movement() {

    }

    public String getFromUserID() {

        return getString("FromUserID");
    }

    public void setFromUserID(String userID) {

        put("FromUserID", userID);
    }

    public String getToUserID() {

        return getString("ToUserID");
    }

    public void setToUserID(String userID) {

        put("ToUserID", userID);
    }

    public int getValue() {

        return getInt("Value");
    }

    public void setValue(int value) {

        put("Value", value);
    }

    public String getCalendarID() {

        return getString("CalendarID");
    }

    public void setCalendarID(String calendarId) {

        put("CalendarID", calendarId);
    }
}

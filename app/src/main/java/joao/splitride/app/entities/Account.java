package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by joaoferreira on 30/10/16.
 */
@ParseClassName("Accounts")
public class Account extends ParseObject {

    public Account() {

    }

    public String getMonth() {
        return getString("Month");
    }

    public void setMonth(String month) {
        put("Month", month);
    }

    public String getReceiverID() {
        return getString("ReceiverID");
    }

    public void setReceiverID(String receiverID) {
        put("ReceiverID", receiverID);
    }

    public String getOwerID() {
        return getString("OwerID");
    }

    public void setOwerID(String owerID) {
        put("OwerID", owerID);
    }

    public double getValue() {
        return getDouble("Value");
    }

    public void setValue(double value) {
        put("Value", value);
    }

    public String getCalendarID() {
        return getString("CalendarID");
    }

    public void setCalendarID(String calendarID) {
        put("CalendarID", calendarID);
    }
}

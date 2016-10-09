package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 30-07-2016.
 */
@ParseClassName("PassengersInTrip")
public class PassengersInTrip extends ParseObject{

    public PassengersInTrip(){

    }

    public String getTripID(){
        return this.getString("TripID");
    }

    public void setTripID(String id){
        put("TripID", id);
    }

    public String getPassengerID(){
        return this.getString("PassengerID");
    }

    public void setPassengerID(String id){
        put("PassengerID", id);
    }

    public String getSegmentID() {
        return this.getString("SegmentID");
    }

    public void setSegmentID(String id) {
        put("SegmentID", id);
    }

    public String getCalendarId(){
        return this.getString("CalendarID");
    }

    public void setCalendarID(String id){
        put("CalendarID", id);
    }
}

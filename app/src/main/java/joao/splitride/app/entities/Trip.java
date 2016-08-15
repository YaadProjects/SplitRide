package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 12-08-2016.
 */
@ParseClassName("Trips")
public class Trip extends ParseObject{

    public Trip(){

    }

    public void setDriverID(String driverID){
        put("DriverID", driverID);
    }

    public void setVehicleID(String vehicleID){
        put("VehicleID", vehicleID);
    }

    public void setRoundTrip(boolean roundtrip){
        put("RoundTrip", roundtrip);
    }

    public void setDate(String date){
        put("Date", date);
    }

    public void setCalendarID(String calendarID){
        put("CalendarID", calendarID);
    }

    public String getDriverID(){
        return getString("DriverID");
    }

    public String getVehicleID(){
        return getString("VehicleID");
    }

    public int getRoundTrip(){
        return getInt("RoundTrip");
    }

    public String getDate() {
        return getString("Date");
    }

    public String getCalendarID(){
        return getString("CalendarID");
    }
}

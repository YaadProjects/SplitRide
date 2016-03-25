package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by joaoferreira on 25/03/16.
 */
@ParseClassName("Vehicles")
public class Vehicle extends ParseObject {

    public Vehicle() {

    }

    public String getVehicleName() {

        return getString("VehicleName");
    }

    public void setVehicleName(String name) {

        put("VehicleName", name);
    }

    public double getVehicleConsumption() {

        return getDouble("VehicleConsumption");
    }

    public void setVehicleConsumption(double consumption) {

        put("VehicleConsumption", consumption);
    }

    public String getUserID() {

        return getString("UserID");
    }

    public void setUserID(String userID) {

        put("UserID", userID);
    }

    public String getCalendarID() {

        return getString("CalendarID");
    }

    public void setCalendarID(String calendarID) {

        put("CalendarID", calendarID);
    }


}

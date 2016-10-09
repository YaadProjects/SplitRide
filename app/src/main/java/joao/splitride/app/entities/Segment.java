package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 03-12-2015.
 */
@ParseClassName("Segments")
public class Segment extends ParseObject {

    public Segment() {

    }

    public String getName() {

        return getString("Name");
    }

    public void setName(String name){

        put("Name", name);
    }

    public String getCalendarID() {

        return getString("calendarID");
    }

    public void setCalendarID(String id) {

        put("calendarID", id);
    }

    public double getCost(){
        return getDouble("Cost");
    }

    public void setCost(double cost) {
        put("Cost", cost);
    }

    public double getDistance(){
        return getDouble("Distance");
    }

    public void setDistance(double distance) {
        put("Distance", distance);
    }
}

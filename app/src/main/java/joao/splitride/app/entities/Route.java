package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 03-12-2015.
 */
@ParseClassName("Routes")
public class Route extends ParseObject{

    public Route(){

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
}

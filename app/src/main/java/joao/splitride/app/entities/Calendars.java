package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 25-01-2016.
 */
@ParseClassName("Calendars")
public class Calendars extends ParseObject{

    public Calendars(){

    }

    public void setName(String name){
        put("Name", name);
    }

    public String getName(){
        return getString("Name");
    }

}

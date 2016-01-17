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

    public void setName(String name){

        put("Name", name);
    }

    public String getName(){

        return getString("Name");
    }
}

package joao.splitride.app.entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Joao on 17-01-2016.
 */
@ParseClassName("User")
public class User extends ParseObject{

    public User(){

    }

    public void setUsername(String username){

        put("username", username);
    }

    public void setEmail(String email){
        put("email", email);
    }

    public String getUsername(){

        return getString("username");
    }

    public String getEmail(){

        return getString("email");
    }
}

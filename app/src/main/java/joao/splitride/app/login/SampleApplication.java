package joao.splitride.app.login;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import joao.splitride.app.entities.ComposedRoute;
import joao.splitride.app.entities.Route;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.entities.User;

/**
 * Created by Joao on 22-11-2015.
 */
public class SampleApplication extends Application {


    public void onCreate(){
        super.onCreate();
        //Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Segment.class);
        ParseObject.registerSubclass(Route.class);
        ParseObject.registerSubclass(ComposedRoute.class);
        ParseObject.registerSubclass(User.class);
        Parse.initialize(this, "pg54vFFRG4h8UeR48iQrgER3E0Gz8PTLyOaOMqMJ", "1oGI3bsT0YzUBFQLQ4BRsYH5OHLytj47i1a6qqVp");
    }
}

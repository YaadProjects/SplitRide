package joao.splitride.app.login;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.Movement;
import joao.splitride.app.entities.PassengersInTrip;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.entities.Trip;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.entities.Vehicle;

/**
 * Created by Joao on 22-11-2015.
 */
public class SampleApplication extends Application {


    public void onCreate(){
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Segment.class);
        ParseObject.registerSubclass(Calendars.class);
        ParseObject.registerSubclass(UsersByCalendars.class);
        ParseObject.registerSubclass(Vehicle.class);
        ParseObject.registerSubclass(Movement.class);
        ParseObject.registerSubclass(PassengersInTrip.class);
        ParseObject.registerSubclass(Trip.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("pg54vFFRG4h8UeR48iQrgER3E0Gz8PTLyOaOMqMJ")
                .clientKey("1oGI3bsT0YzUBFQLQ4BRsYH5OHLytj47i1a6qqVp")
                .server("https://parseapi.back4app.com")

                .build());


        //Parse.initialize(this, "pg54vFFRG4h8UeR48iQrgER3E0Gz8PTLyOaOMqMJ", "1oGI3bsT0YzUBFQLQ4BRsYH5OHLytj47i1a6qqVp");
    }
}

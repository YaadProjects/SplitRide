package joao.splitride.app.login;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import joao.splitride.R;
import joao.splitride.app.entities.Account;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.Movement;
import joao.splitride.app.entities.PassengersInTrip;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.entities.Trip;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.entities.Vehicle;


public class SampleApplication extends Application {

    public void onCreate(){
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key), getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Segment.class);
        ParseObject.registerSubclass(Calendars.class);
        ParseObject.registerSubclass(UsersByCalendars.class);
        ParseObject.registerSubclass(Vehicle.class);
        ParseObject.registerSubclass(Movement.class);
        ParseObject.registerSubclass(PassengersInTrip.class);
        ParseObject.registerSubclass(Trip.class);
        ParseObject.registerSubclass(Account.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("pg54vFFRG4h8UeR48iQrgER3E0Gz8PTLyOaOMqMJ")
                .clientKey("1oGI3bsT0YzUBFQLQ4BRsYH5OHLytj47i1a6qqVp")
                .server("https://parseapi.back4app.com")
                .build());

        ParseFacebookUtils.initialize(SampleApplication.this);

        //Parse.initialize(this, "pg54vFFRG4h8UeR48iQrgER3E0Gz8PTLyOaOMqMJ", "1oGI3bsT0YzUBFQLQ4BRsYH5OHLytj47i1a6qqVp");
    }
}

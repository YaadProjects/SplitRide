package joao.splitride.app.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Route;
import joao.splitride.app.entities.UsersByCalendars;

/**
 * Created by joaoferreira on 31/05/16.
 */
public class PassengersByTrips extends AppCompatActivity implements View.OnClickListener {

    private Spinner route, passenger;
    private SharedPreferences sharedPreferences;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengers_list_layout);

        route = (Spinner) findViewById(R.id.routeSpinner);
        passenger = (Spinner) findViewById(R.id.passengerSpinner);
        save = (Button) findViewById(R.id.savePassengers);
        cancel = (Button) findViewById(R.id.cancelPassengers);

        sharedPreferences = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber informação.");
        progressDialog.show();

        ParseQuery<Route> routeQuery = ParseQuery.getQuery("Routes");
        routeQuery.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        routeQuery.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> objects, ParseException e) {

                ArrayList<String> routesName = new ArrayList<String>();

                for (Route r : objects) {
                    routesName.add(r.getName());
                }

                ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(PassengersByTrips.this, android.R.layout.simple_dropdown_item_1line, routesName);
                route.setAdapter(arrayadapter);
            }
        });

        final ParseQuery<UsersByCalendars> usersQuery = ParseQuery.getQuery("UsersByCalendar");
        usersQuery.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        usersQuery.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                ArrayList<String> usernames = new ArrayList<String>();

                for (UsersByCalendars uc : objects) {

                    ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                    queryUser.whereEqualTo("objectId", uc.getUserID());

                    try {
                        ParseUser user = queryUser.getFirst();
                        usernames.add(user.getUsername());

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                }

                ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(PassengersByTrips.this, android.R.layout.simple_dropdown_item_1line, usernames);
                passenger.setAdapter(arrayadapter);

                progressDialog.dismiss();

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Passengers by trips");
        setSupportActionBar(toolbar);


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.savePassengers:
                break;

            case R.id.cancelPassengers:
                finish();
                break;
        }
    }
}

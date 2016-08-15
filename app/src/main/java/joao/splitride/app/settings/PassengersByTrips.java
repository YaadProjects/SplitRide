package joao.splitride.app.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Button save, cancel, addPassenger;
    private ArrayList<String> routesName, usernames;
    private ListView passengerList;
    private ArrayList<HashMap<String, String>> pass = new ArrayList<HashMap<String, String>>();
    private SimpleAdapter simpleAdapter;
    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengers_list_layout);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        route = (Spinner) findViewById(R.id.routeSpinner);
        passenger = (Spinner) findViewById(R.id.passengerSpinner);
        addPassenger = (Button) findViewById(R.id.addPassenger);
        passengerList = (ListView) findViewById(R.id.passengerList);
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

                routesName = new ArrayList<String>();

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

                usernames = new ArrayList<String>();

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

        Bundle b = getIntent().getExtras();

        ArrayList<String> passengersNames = b.getStringArrayList("passengersNames");
        ArrayList<String> passengersRoutes = b.getStringArrayList("passengersRoutes");

        pass = new ArrayList<HashMap<String, String>>();

        for(int i=0; i<passengersNames.size(); i++){
            HashMap<String, String> item = new HashMap<String, String>();

            item.put("line1", passengersNames.get(i));
            item.put("line2", passengersRoutes.get(i));

            pass.add(item);
        }

        simpleAdapter = new SimpleAdapter(this, pass, R.layout.custom_passengers_layout,
                new String[]{"line1", "line2"},
                new int[]{R.id.line_a, R.id.line_b});

        passengerList.setAdapter(simpleAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Passengers by trips");
        setSupportActionBar(toolbar);


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addPassenger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.savePassengers:

                ArrayList<String> passengersNames = new ArrayList<>();
                ArrayList<String> passengersRoutes = new ArrayList<>();

                for(HashMap<String, String> passenger: pass){

                    passengersNames.add(passenger.get("line1"));
                    passengersRoutes.add(passenger.get("line2"));
                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("passengersNames", passengersNames);
                intent.putStringArrayListExtra("passengersRoutes", passengersRoutes);
                setResult(1, intent);
                finish();

                break;

            case R.id.cancelPassengers:
                finish();
                break;

            case R.id.addPassenger:

                String routeN = routesName.get(route.getSelectedItemPosition());
                String passengerN = usernames.get(passenger.getSelectedItemPosition());

                boolean alreadyExists = false;

                for(HashMap<String, String> passengers: pass){

                    if(passengers.containsValue(passengerN))
                        alreadyExists = true;
                }


                if(!alreadyExists){
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("line1", passengerN);
                    item.put("line2", routeN);

                    pass.add(item);

                    simpleAdapter = new SimpleAdapter(this, pass, R.layout.custom_passengers_layout,
                            new String[]{"line1", "line2"},
                            new int[]{R.id.line_a, R.id.line_b});

                    passengerList.setAdapter(simpleAdapter);
                }else{
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, passengerN + " is already in the list", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

                break;
        }
    }
}

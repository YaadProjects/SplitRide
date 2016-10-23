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
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.MultiSelectionSpinner;
import joao.splitride.app.custom.PassengerListAdapter;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.entities.UsersByCalendars;


public class PassengersByTrips extends AppCompatActivity implements View.OnClickListener {

    private Spinner passenger;
    private MultiSelectionSpinner segment;
    private SharedPreferences sharedPreferences;
    private Button save, cancel, addPassenger;
    private ArrayList<String> segmentsName, usernames;
    private ListView passengerList;
    private ArrayList<HashMap<String, String>> pass = new ArrayList<HashMap<String, String>>();
    private PassengerListAdapter passengerListAdapter;
    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengers_list_layout);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        segment = (MultiSelectionSpinner) findViewById(R.id.segmentSpinner);
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

        ParseQuery<Segment> segmentQuery = ParseQuery.getQuery("Segments");
        segmentQuery.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        segmentQuery.findInBackground(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> objects, ParseException e) {

                segmentsName = new ArrayList<>();

                for (Segment r : objects) {
                    segmentsName.add(r.getName());
                }

                segment.setItems(segmentsName);
                segment.setSelection(0);
            }
        });

        Bundle b = getIntent().getExtras();

        ArrayList<String> passengersNames = b.getStringArrayList("passengersNames");
        ArrayList<String> passengersSegments = b.getStringArrayList("passengersSegments");
        final String driverName = b.getString("driverName");

        final ParseQuery<UsersByCalendars> usersQuery = ParseQuery.getQuery("UsersByCalendar");
        usersQuery.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        usersQuery.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                usernames = new ArrayList<>();

                for (UsersByCalendars uc : objects) {

                    ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                    queryUser.whereEqualTo("objectId", uc.getUserID());

                    try {
                        ParseUser user = queryUser.getFirst();
                        if (!user.getUsername().equalsIgnoreCase(driverName))
                            usernames.add(user.getUsername());

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                }

                ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(PassengersByTrips.this, android.R.layout.simple_dropdown_item_1line, usernames);
                passenger.setAdapter(arrayadapter);

                progressDialog.dismiss();

            }
        });


        pass = new ArrayList<>();

        for(int i=0; i<passengersNames.size(); i++){
            HashMap<String, String> item = new HashMap<String, String>();

            item.put("line1", passengersNames.get(i));
            item.put("line2", passengersSegments.get(i));

            pass.add(item);
        }

        passengerListAdapter = new PassengerListAdapter(this, R.layout.custom_passengers_layout, pass);
        passengerList.setAdapter(passengerListAdapter);


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
                ArrayList<String> passengersSegments = new ArrayList<>();

                for(HashMap<String, String> passenger: pass){

                    passengersNames.add(passenger.get("line1"));
                    passengersSegments.add(passenger.get("line2"));
                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("passengersNames", passengersNames);
                intent.putStringArrayListExtra("passengersSegments", passengersSegments);
                setResult(1, intent);
                finish();

                break;

            case R.id.cancelPassengers:
                finish();
                break;

            case R.id.addPassenger:

                String segmentN = segment.getSelectedItemsAsString();
                String passengerN = usernames.get(passenger.getSelectedItemPosition());


                boolean alreadyExists = false;

                for(HashMap<String, String> passengers: pass){

                    if(passengers.containsValue(passengerN))
                        alreadyExists = true;
                }

                if (segmentN.equalsIgnoreCase("")) {
                    Snackbar snackbar = Snackbar.make(parentLayout, "You haven't selected a segment", Snackbar.LENGTH_LONG);

                    snackbar.show();
                } else if (!alreadyExists) {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("line1", passengerN);
                    item.put("line2", segmentN);

                    pass.add(item);

                    passengerListAdapter = new PassengerListAdapter(this, R.layout.custom_passengers_layout, pass);
                    passengerList.setAdapter(passengerListAdapter);
                }else{
                    Snackbar snackbar = Snackbar.make(parentLayout, passengerN + " is already in the list", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

                break;
        }
    }


    public void spinnerClick(View v) {

        String spinner = v.getTag().toString();

        switch (spinner) {
            case "passengerSpinner":
                passenger.performClick();
                break;

            case "segmentSpinner":
                segment.performClick();
                break;
        }

    }
}

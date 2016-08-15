package joao.splitride.app.settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.PassengersInTrip;
import joao.splitride.app.entities.Route;
import joao.splitride.app.entities.Trip;
import joao.splitride.app.entities.Vehicle;

/**
 * Created by joaoferreira on 28/05/16.
 */
public class AddEditTrip extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout parentLayout;
    private Button save, cancel, passengers;
    private ImageButton calendar;
    private EditText date;
    private SharedPreferences sharedPreferences;
    private Spinner drivers, vehicles;
    private CheckBox roundtrip;
    private ArrayList<String> passengerNames = new ArrayList<>(), passengerRoutes = new ArrayList<>();
    private ArrayList<String> usernames = new ArrayList<String>(), vehiclesNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_layout);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        save = (Button) findViewById(R.id.saveTrip);
        cancel = (Button) findViewById(R.id.cancelTrip);
        passengers = (Button) findViewById(R.id.passengerListButton);
        calendar = (ImageButton) findViewById(R.id.calendarButton);
        drivers = (Spinner) findViewById(R.id.driverSpinner);
        vehicles = (Spinner) findViewById(R.id.vehicleSpinner);
        date = (EditText) findViewById(R.id.date);
        roundtrip = (CheckBox) findViewById(R.id.ida_volta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trips");
        setSupportActionBar(toolbar);

        date.setKeyListener(null);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber informação.");
        progressDialog.show();

        sharedPreferences = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        final ParseQuery<Vehicle> vehicleQuery = ParseQuery.getQuery("Vehicles");
        vehicleQuery.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        vehicleQuery.findInBackground(new FindCallback<Vehicle>() {
            @Override
            public void done(List<Vehicle> objects, ParseException e) {



                for (Vehicle v : objects) {
                    vehiclesNames.add(v.getVehicleName());

                    ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                    queryUser.whereEqualTo("objectId", v.getUserID());

                    try {
                        ParseUser user = queryUser.getFirst();
                        usernames.add(user.getUsername());

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                }

                ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(AddEditTrip.this, android.R.layout.simple_dropdown_item_1line, usernames);
                drivers.setAdapter(arrayadapter);

                ArrayAdapter<String> arrayadapter2 = new ArrayAdapter<String>(AddEditTrip.this, android.R.layout.simple_dropdown_item_1line, vehiclesNames);
                vehicles.setAdapter(arrayadapter2);

                progressDialog.dismiss();

            }
        });


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        passengers.setOnClickListener(this);
        calendar.setOnClickListener(this);

    }

    private void saveTrip(String date, String driver, String vehicle, boolean roundtrip, final ArrayList<String> passengers, final ArrayList<String> routes){

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("username", driver);

        ParseQuery<Vehicle> queryVehicle = ParseQuery.getQuery("Vehicles");
        queryVehicle.whereEqualTo("VehicleName", vehicle);
        queryVehicle.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        try {
            String driverID = queryUser.getFirst().getObjectId();
            String vehicleID = queryVehicle.getFirst().getObjectId();

            final Trip trip = new Trip();

            trip.setCalendarID(sharedPreferences.getString("calendarID", ""));
            trip.setDate(date);
            trip.setDriverID(driverID);
            trip.setVehicleID(vehicleID);
            trip.setRoundTrip(roundtrip);


            trip.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e==null){

                        for(int i=0; i<passengers.size(); i++){
                            ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                            queryUser.whereEqualTo("username", passengers.get(i));

                            ParseQuery<Route> queryRoute = ParseQuery.getQuery("Routes");
                            queryRoute.whereEqualTo("Name", routes.get(i));
                            queryRoute.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));


                            try {
                                String passengerID = queryUser.getFirst().getObjectId();
                                String routeID = queryRoute.getFirst().getObjectId();

                                PassengersInTrip passengersInTrip = new PassengersInTrip();

                                passengersInTrip.setPassengerID(passengerID);
                                passengersInTrip.setRouteID(routeID);
                                passengersInTrip.setTripID(trip.getObjectId());
                                passengersInTrip.setCalendarID(sharedPreferences.getString("calendarID", ""));

                                passengersInTrip.saveInBackground();

                            }catch (ParseException e1){
                                e1.printStackTrace();
                            }

                        }

                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveTrip:

                if(!date.getText().toString().equalsIgnoreCase("DD/MM/YYYY") && !passengerNames.isEmpty()){
                    saveTrip(date.getText().toString(), usernames.get(drivers.getSelectedItemPosition()), vehiclesNames.get(vehicles.getSelectedItemPosition()), roundtrip.isChecked(), passengerNames, passengerRoutes);
                    finish();
                }else{
                    String message = "";

                    if(date.getText().toString().equalsIgnoreCase("DD/MM/YYYY"))
                        message = "You must indicate a date";
                    else message = "You must indicate, at least, one passenger.";

                    Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);

                    snackbar.show();

                }


                break;

            case R.id.cancelTrip:
                finish();
                break;

            case R.id.passengerListButton:
                Intent intent = new Intent(AddEditTrip.this, PassengersByTrips.class);

                intent.putStringArrayListExtra("passengersNames", passengerNames);
                intent.putStringArrayListExtra("passengersRoutes", passengerRoutes);
                startActivityForResult(intent, 1);

                break;

            case R.id.calendarButton:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditText date;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            date = (EditText) getActivity().findViewById(R.id.date);

            int year, month, day;
            String date_inserted = date.getText().toString();

            if (!date_inserted.equalsIgnoreCase("DD/MM/YYYY")) {
                String[] parts = date_inserted.split("/");

                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                year = Integer.parseInt(parts[2]);

            } else {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            date = (EditText) getActivity().findViewById(R.id.date);

            date.setText(day + "/" + (month + 1) + "/" + year);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == 1){

            passengerNames = data.getStringArrayListExtra("passengersNames");
            passengerRoutes = data.getStringArrayListExtra("passengersRoutes");

            Log.w("application", passengerNames.toString());
            Log.w("application", passengerRoutes.toString());
        }
    }
}

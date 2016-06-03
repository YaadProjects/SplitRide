package joao.splitride.app.settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import joao.splitride.R;
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

                ArrayList<String> usernames = new ArrayList<String>();
                ArrayList<String> vehiclesNames = new ArrayList<String>();

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveTrip:
                break;

            case R.id.cancelTrip:
                finish();
                break;

            case R.id.passengerListButton:
                Intent intent = new Intent(AddEditTrip.this, PassengersByTrips.class);
                startActivity(intent);
                //finish();
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
}

package joao.splitride.app.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.entities.Vehicle;

public class AddEditVehicle extends AppCompatActivity implements OnClickListener {

    private Button ok, cancel;
    private LinearLayout parentLayout;
    private EditText name, consumption;
    private Spinner owner;
    private Intent editVehicle;
    private String vehicle_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_layout);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        name = (EditText) findViewById(R.id.vehicle_name);
        consumption = (EditText) findViewById(R.id.vehicle_comsumption);
        owner = (Spinner) findViewById(R.id.owner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vehicles");
        setSupportActionBar(toolbar);

        sharedPreferences = this.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        editVehicle = getIntent();

        if (editVehicle.getStringExtra("name") != null) {

            ok.setText(R.string.edit);

            name.setText("" + editVehicle.getStringExtra("name"));
            consumption.setText("" + editVehicle.getDoubleExtra("consumption", 0.0));

            ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(AddEditVehicle.this, android.R.layout.simple_dropdown_item_1line, editVehicle.getStringArrayListExtra("usernames"));

            owner.setAdapter(arrayadapter);
            owner.setSelection(arrayadapter.getPosition(editVehicle.getStringExtra("owner")));

            vehicle_id = editVehicle.getStringExtra("id");

        } else {
            ok.setText(R.string.add);

            ParseQuery<UsersByCalendars> query_users_calendars = ParseQuery.getQuery("UsersByCalendar");
            query_users_calendars.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

            query_users_calendars.findInBackground(new FindCallback<UsersByCalendars>() {
                @Override
                public void done(List<UsersByCalendars> objects, ParseException e) {

                    if (e == null) {

                        ArrayList<String> usernames = new ArrayList<String>();

                        for (UsersByCalendars user : objects) {

                            ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                            query_users.whereEqualTo("objectId", user.getUserID());

                            try {
                                ParseUser parseUser = query_users.getFirst();
                                usernames.add(parseUser.getUsername());

                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                        }

                        ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(AddEditVehicle.this, android.R.layout.simple_dropdown_item_1line, usernames);
                        owner.setAdapter(arrayadapter);
                    }
                }
            });
        }


        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok:
                String vehicle_name = name.getText().toString();
                String vehicle_consumption = consumption.getText().toString();
                String owner_username = owner.getSelectedItem().toString();

                if (vehicle_name.length() == 0 || vehicle_consumption.length() == 0) {
                    Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    if (ok.getText().toString().equalsIgnoreCase("add"))
                        saveVehicle(vehicle_name, Double.parseDouble(vehicle_consumption), owner_username);
                    else
                        editVehicle(vehicle_name, Double.parseDouble(vehicle_consumption), owner_username);
                }

                break;

            case R.id.cancel:
                finish();
                break;
        }

    }

    public void saveVehicle(String name, double consumption, String username) {


        ParseQuery<Vehicle> query_vehicle = ParseQuery.getQuery("Vehicles");
        query_vehicle.whereEqualTo("VehicleName", name);
        query_vehicle.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        try {

            Vehicle veh = query_vehicle.getFirst();

            if (veh != null)
                Snackbar.make(parentLayout, "This vehicle name has already in use. Please pick another.", Snackbar.LENGTH_LONG).show();

        } catch (ParseException e) {

            if (e.getCode() == 101) {
                ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                query_users.whereEqualTo("username", username);

                try {
                    ParseUser owner = query_users.getFirst();

                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehicleName(name);
                    vehicle.setVehicleConsumption(consumption);
                    vehicle.setUserID(owner.getObjectId());
                    vehicle.setCalendarID(sharedPreferences.getString("calendarID", ""));

                    vehicle.saveInBackground();
                    finish();

                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            } else e.printStackTrace();

        }

    }

    private void editVehicle(String name, double consumption, String owner) {

        ParseQuery<Vehicle> query_vehicle = ParseQuery.getQuery("Vehicles");

        query_vehicle.whereEqualTo("VehicleName", name);
        query_vehicle.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        try {

            Vehicle veh = query_vehicle.getFirst();

            if (veh != null)
                Snackbar.make(parentLayout, "This vehicle name has already in use. Please pick another.", Snackbar.LENGTH_LONG).show();

        } catch (ParseException e) {

            if (e.getCode() == 101) {

                Vehicle vehicle = new Vehicle();

                vehicle.setObjectId(vehicle_id);
                vehicle.setVehicleName(name);
                vehicle.setVehicleConsumption(consumption);

                ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                query_users.whereEqualTo("username", owner);

                try {
                    ParseUser user = query_users.getFirst();

                    vehicle.setUserID(user.getObjectId());

                    vehicle.saveInBackground();
                    finish();
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }

            } else e.printStackTrace();

        }
    }


}

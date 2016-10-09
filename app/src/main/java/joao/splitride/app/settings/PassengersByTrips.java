package joao.splitride.app.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.MultiSelectionSpinner;
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
    private SimpleAdapter simpleAdapter;
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

        Bundle b = getIntent().getExtras();

        ArrayList<String> passengersNames = b.getStringArrayList("passengersNames");
        ArrayList<String> passengersSegments = b.getStringArrayList("passengersSegments");

        pass = new ArrayList<HashMap<String, String>>();

        for(int i=0; i<passengersNames.size(); i++){
            HashMap<String, String> item = new HashMap<String, String>();

            item.put("line1", passengersNames.get(i));
            item.put("line2", passengersSegments.get(i));

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

                    simpleAdapter = new SimpleAdapter(this, pass, R.layout.custom_passengers_layout,
                            new String[]{"line1", "line2"},
                            new int[]{R.id.line_a, R.id.line_b});

                    passengerList.setAdapter(simpleAdapter);
                }else{
                    Snackbar snackbar = Snackbar.make(parentLayout, passengerN + " is already in the list", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

                break;
        }
    }

    public void editElementOnClickHandler(View v) {

        // Determinar quem e o utilizador a ser editado
        LinearLayout line_layout = (LinearLayout) v.getParent();
        final TextView text_test = (TextView) line_layout.findViewById(R.id.line_b);

        final String segment_selected = text_test.getText().toString();
        int pos = segmentsName.indexOf(segment_selected);

        String[] segments_array = new String[segmentsName.size()];

        for (int i = 0; i < segmentsName.size(); i++) {

            segments_array[i] = segmentsName.get(i);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle("Select the segment")
                .setSingleChoiceItems(segments_array, pos, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        text_test.setText(segmentsName.get(selectedPosition));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();

    }

    public void removeElementOnClickHandler(View v) {

        LinearLayout rl = (LinearLayout) v.getParent();
        TextView tv = (TextView) rl.findViewById(R.id.line_a);
        String name = tv.getText().toString();

        HashMap<String, String> item = null;

        for (HashMap<String, String> pair : pass) {

            if (pair.containsValue(name))
                item = pair;
        }

        if (item != null)
            pass.remove(item);

        simpleAdapter = new SimpleAdapter(this, pass, R.layout.custom_passengers_layout,
                new String[]{"line1", "line2"},
                new int[]{R.id.line_a, R.id.line_b});

        passengerList.setAdapter(simpleAdapter);

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

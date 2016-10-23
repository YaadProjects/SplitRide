package joao.splitride.app.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import joao.splitride.R;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.UsersByCalendars;


public class AddEditCalendar extends AppCompatActivity implements View.OnClickListener {

    private Button ok, cancel;
    private RelativeLayout parentLayout;
    private EditText name;
    private CheckBox checkBox;
    private Intent editCalendar;
    private String calendarID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_calendar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Calendars");
        setSupportActionBar(toolbar);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        name = (EditText) findViewById(R.id.calendar_name);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        editCalendar = getIntent();

        if(editCalendar.getStringExtra("name") != null){

            ok.setText(R.string.edit);

            name.setText(""+editCalendar.getStringExtra("name"));
            calendarID = editCalendar.getStringExtra("id");

            if (editCalendar.getBooleanExtra("default", false))
                checkBox.toggle();


        }else{
            ok.setText(R.string.add);
        }

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ok:   String calendar_name = name.getText().toString();
                            boolean checked = checkBox.isChecked();

                if (calendar_name.length() == 0) {
                    Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    if (ok.getText().toString().equalsIgnoreCase("Add"))
                        saveCalendar(calendar_name, checked);
                    else
                        editCalendar(calendar_name, checked);
                }


                            break;

            case R.id.cancel:   finish();
                                break;
        }
    }

    private void saveCalendar(String name, final boolean default_calendar){


        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<Calendars> same_name = ParseQuery.getQuery("Calendars");
        same_name.whereEqualTo("Name", name);

        try {

            Calendars cal = same_name.getFirst();

            if (cal != null)
                Snackbar.make(parentLayout, "This calendar name has already in use. Please pick another.", Snackbar.LENGTH_LONG).show();

        } catch (ParseException e) {

            if (e.getCode() == 101) {

                final Calendars calendar = new Calendars();
                calendar.setName(name);

                calendar.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            final UsersByCalendars usersByCalendars = new UsersByCalendars();
                            usersByCalendars.setCalendarID(calendar.getObjectId());
                            usersByCalendars.setDefault(default_calendar);

                            final String userID = sharedPreferences.getString("userID", "");

                            usersByCalendars.setUserID(userID);

                            usersByCalendars.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (default_calendar) {

                                            ParseQuery<UsersByCalendars> previous_default = ParseQuery.getQuery("UsersByCalendar");
                                            previous_default.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));
                                            previous_default.whereEqualTo("UserID", userID);

                                            try {
                                                UsersByCalendars uc = previous_default.getFirst();
                                                uc.setDefault(false);
                                                uc.saveInBackground();

                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("calendarID", usersByCalendars.getCalendarID());
                                            editor.apply();
                                        }

                                    }
                                }
                            });
                            setResult(1);
                            finish();
                        } else {
                            Log.d("error", e.toString());
                        }

                    }
                });

            }
        }

    }


    private void editCalendar(final String name, final boolean default_calendar) {

        boolean can_finish = true;

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        if (!name.equalsIgnoreCase(editCalendar.getStringExtra("name"))) {


            ParseQuery<Calendars> same_cal = ParseQuery.getQuery("Calendars");
            same_cal.whereEqualTo("Name", name);

            try {
                Calendars calendar = same_cal.getFirst();

                if (calendar != null) {
                    Snackbar.make(parentLayout, "This calendar name has already in use. Please pick another.", Snackbar.LENGTH_LONG).show();
                    can_finish = false;
                }


            } catch (ParseException e) {

                if (e.getCode() == 101) {
                    ParseQuery<Calendars> query_cal = ParseQuery.getQuery("Calendars");
                    query_cal.whereEqualTo("objectId", calendarID);

                    try {
                        Calendars cal = query_cal.getFirst();
                        cal.setName(name);
                        cal.saveInBackground();

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }

        String default_calendarID = sharedPreferences.getString("calendarID", "");

        if (editCalendar.getBooleanExtra("default", false) != default_calendar) {

            if (!calendarID.equalsIgnoreCase(default_calendarID)) {

                if (default_calendar) {

                    ParseQuery<UsersByCalendars> query_uc_actual = ParseQuery.getQuery("UsersByCalendar");
                    query_uc_actual.whereEqualTo("CalendarID", calendarID);
                    query_uc_actual.whereEqualTo("UserID", sharedPreferences.getString("userID", ""));

                    try {
                        UsersByCalendars actual_default = query_uc_actual.getFirst();
                        actual_default.setDefault(true);

                        actual_default.saveInBackground();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    ParseQuery<UsersByCalendars> query_uc_previous = ParseQuery.getQuery("UsersByCalendar");
                    query_uc_previous.whereEqualTo("CalendarID", default_calendarID);
                    query_uc_previous.whereEqualTo("UserID", sharedPreferences.getString("userID", ""));

                    try {
                        UsersByCalendars previous_default = query_uc_previous.getFirst();
                        previous_default.setDefault(false);

                        previous_default.saveInBackground();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("calendarID", calendarID);
                    editor.apply();
                } else {

                    ParseQuery<UsersByCalendars> query_uc_actual = ParseQuery.getQuery("UsersByCalendar");
                    query_uc_actual.whereEqualTo("CalendarID", calendarID);
                    query_uc_actual.whereEqualTo("UserID", sharedPreferences.getString("userID", ""));

                    try {
                        UsersByCalendars actual_default = query_uc_actual.getFirst();
                        actual_default.setDefault(false);

                        actual_default.saveInBackground();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                ParseQuery<UsersByCalendars> query_uc_previous = ParseQuery.getQuery("UsersByCalendar");
                query_uc_previous.whereEqualTo("CalendarID", default_calendarID);
                query_uc_previous.whereEqualTo("UserID", sharedPreferences.getString("userID", ""));

                try {
                    UsersByCalendars previous_default = query_uc_previous.getFirst();
                    previous_default.setDefault(default_calendar);

                    previous_default.saveInBackground();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (can_finish) {
            setResult(1);
            finish();
        }
    }
}

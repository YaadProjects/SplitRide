package joao.splitride.app.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;

import joao.splitride.R;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.UsersByCalendars;

/**
 * Created by Joao on 26-01-2016.
 */
public class AddEditCalendar extends AppCompatActivity implements View.OnClickListener {

    private Button ok, cancel;
    private EditText name;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_calendar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Calendars");
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.calendar_name);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ok:   String calendar_name = name.getText().toString();
                            boolean checked = checkBox.isChecked();

                            saveCalendar(calendar_name, checked);
                            finish();
                            break;

            case R.id.cancel:   finish();
                                break;
        }
    }

    private void saveCalendar(String name, final boolean default_calendar){

        final Calendars calendar = new Calendars();
        calendar.setName(name);

        calendar.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    final UsersByCalendars usersByCalendars = new UsersByCalendars();
                    usersByCalendars.setCalendarID(calendar.getObjectId());
                    usersByCalendars.setDefault(default_calendar);

                    final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                    String userID = sharedPreferences.getString("userID", "");
                    usersByCalendars.setUserID(userID);

                    usersByCalendars.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                if (default_calendar){
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("calendarID", usersByCalendars.getCalendarID());
                                    editor.commit();
                                }

                            }
                        }
                    });
                } else {
                    Log.d("error", e.toString());
                }

            }
        });

    }
}

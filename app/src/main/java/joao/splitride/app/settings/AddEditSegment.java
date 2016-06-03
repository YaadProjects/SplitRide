package joao.splitride.app.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Segment;

/**
 * Created by Joao on 28-11-2015.
 */
public class AddEditSegment extends AppCompatActivity implements View.OnClickListener{

    private Button ok, cancel;
    private LinearLayout parentLayout;
    private EditText name, distance, cost;
    private String segment_id;
    private Intent editSegment;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_segment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        name = (EditText) findViewById(R.id.segment_name);
        distance = (EditText) findViewById(R.id.segment_distance);
        cost = (EditText) findViewById(R.id.segment_cost);

        sharedPreferences = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        editSegment = getIntent();

        if(editSegment.getIntExtra("distance", -1) != -1 || editSegment.getDoubleExtra("cost", -1) != -1){

            ok.setText(R.string.edit);

            name.setText(""+editSegment.getStringExtra("name"));
            distance.setText(""+editSegment.getIntExtra("distance", 0));
            cost.setText(""+editSegment.getDoubleExtra("cost", 0));
            segment_id = editSegment.getStringExtra("id");
        }else{
            ok.setText(R.string.add);
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ok:   String getName = name.getText().toString();
                            String getDistance = distance.getText().toString();
                            String getCost = cost.getText().toString();

                            if(ok.getText().toString().equalsIgnoreCase("Add")){
                                saveSegment(getName, getDistance, getCost);
                            }else{
                                editSegment(getName, getDistance, getCost);
                            }

                            break;

            case R.id.cancel:   finish();

                                break;
        }
    }


    private void saveSegment(final String name, final String distance, final String cost) {

        if (distance.length() == 0 || cost.length() == 0 || name.length() == 0) {
            // Show the error message
            Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                    .show();
        } else {
            ParseQuery<Segment> query = ParseQuery.getQuery("Segments");
            query.whereEqualTo("Name", name);

            query.findInBackground(new FindCallback<Segment>() {
                @Override
                public void done(List<Segment> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() != 0) {
                            Snackbar.make(parentLayout, getResources().getString(R.string.name_in_use), Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            Segment segment = new Segment();
                            segment.setName(name);
                            segment.setDistance(Integer.parseInt(distance));
                            segment.setCost(Double.parseDouble(cost));
                            segment.setCalendarID(sharedPreferences.getString("calendarID", ""));

                            segment.saveInBackground();

                        }

                    } else {
                        // something went wrong
                        Snackbar.make(parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            setResult(1);
            finish();
        }

    }


    private void editSegment(final String name, final String distance, final String cost) {

        if (distance.length() == 0 || cost.length() == 0 || name.length() == 0) {
            // Show the error message
            Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                    .show();
        } else {
            ParseQuery<Segment> query = ParseQuery.getQuery("Segments");

            query.getInBackground(segment_id, new GetCallback<Segment>() {
                @Override
                public void done(Segment object, ParseException e) {
                    if (e == null) {
                        object.setName(name);
                        object.setDistance(Integer.parseInt(distance));
                        object.setCost(Double.parseDouble(cost));
                        object.setCalendarID(sharedPreferences.getString("calendarID", ""));

                        object.saveInBackground();

                    } else {
                        // something went wrong
                        Snackbar.make(parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            });

            setResult(1);
            finish();
        }
    }
}

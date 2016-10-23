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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;

import joao.splitride.R;
import joao.splitride.app.entities.Segment;

public class AddEditSegment extends AppCompatActivity implements OnClickListener {

    private Button ok, cancel;
    private RelativeLayout parentLayout;
    private EditText name, distance, cost;
    private Intent editSegment;
    private String segment_id;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> current_segments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_segment_layout);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        name = (EditText) findViewById(R.id.segment_name);
        distance = (EditText) findViewById(R.id.segment_distance);
        cost = (EditText) findViewById(R.id.segment_cost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Segments");
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        editSegment = getIntent();


        if (editSegment.getStringExtra("name") != null) {

            ok.setText(R.string.edit);

            name.setText("" + editSegment.getStringExtra("name"));
            distance.setText("" + editSegment.getDoubleExtra("distance", 0.0));
            cost.setText("" + editSegment.getDoubleExtra("cost", 0.0));
            segment_id = editSegment.getStringExtra("id");

        }else{
            ok.setText(R.string.add);
        }

        current_segments = editSegment.getStringArrayListExtra("current_segments");

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ok:
                String segment_name = name.getText().toString();
                String distance_input = distance.getText().toString();
                String cost_input = cost.getText().toString();

                if (segment_name.length() == 0 || distance_input.length() == 0 || cost_input.length() == 0) {
                    Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                            .show();
                } else if (current_segments.contains(segment_name)) {
                    Snackbar.make(parentLayout, "This segment name is already being used in this calendar.", Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    double distance_value = Double.valueOf(distance_input);
                    double cost_value = Double.valueOf(cost_input);


                    if (ok.getText().toString().equalsIgnoreCase("add"))
                        saveSegments(segment_name, distance_value, cost_value);
                    else
                        editSegment(segment_name, distance_value, cost_value);
                }

                break;

            case R.id.cancel:	finish();
                break;
        }

    }

    public void saveSegments(String name, double distance, double cost) {

        Segment segment = new Segment();
        segment.setName(name);
        segment.setDistance(distance);
        segment.setCost(cost);
        segment.setCalendarID(sharedPreferences.getString("calendarID", ""));

        segment.saveInBackground();
        setResult(1);
        finish();
    }

    private void editSegment(final String name, final double distance, final double cost) {

        ParseQuery<Segment> query = ParseQuery.getQuery("Segments");

        query.getInBackground(segment_id, new GetCallback<Segment>() {
            @Override
            public void done(Segment object, ParseException e) {

                if(e == null){
                    object.setName(name);
                    object.setDistance(distance);
                    object.setCost(cost);
                    object.setCalendarID(sharedPreferences.getString("calendarID", ""));
                    object.saveInBackground();

                    setResult(1);
                    finish();
                }
            }
        });
    }





}

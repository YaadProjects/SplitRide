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
import android.widget.LinearLayout;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import joao.splitride.R;
import joao.splitride.app.entities.Route;

public class AddEditRoute extends AppCompatActivity implements OnClickListener{

    private Button ok, cancel;
    private LinearLayout parentLayout;
    private EditText name, distance, cost;
    private Intent editRoute;
    private String route_id;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route_layout);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        name = (EditText) findViewById(R.id.route_name);
        distance = (EditText) findViewById(R.id.route_distance);
        cost = (EditText) findViewById(R.id.route_cost);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Routes");
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        editRoute = getIntent();


        if(editRoute.getStringExtra("name") != null){

            ok.setText(R.string.edit);

            name.setText(""+editRoute.getStringExtra("name"));
            distance.setText(""+editRoute.getDoubleExtra("distance", 0.0));
            cost.setText(""+editRoute.getDoubleExtra("cost", 0.0));
            route_id = editRoute.getStringExtra("id");

        }else{
            ok.setText(R.string.add);
        }


        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ok:   String route_name = name.getText().toString();
                            String distance_input = distance.getText().toString();
                            String cost_input = cost.getText().toString();

                            if(route_name.length() == 0 || distance_input.length() == 0 || cost_input.length() == 0){
                                    Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                                    .show();
                            }else{
                                double distance_value = Double.valueOf(distance_input);
                                double cost_value = Double.valueOf(cost_input);


                                if (ok.getText().toString().equalsIgnoreCase("add"))
                                    saveRoutes(route_name, distance_value, cost_value);
                                else
                                    editRoute(route_name, distance_value, cost_value);
                            }

                            break;

            case R.id.cancel:	finish();
                                break;
        }

    }

    public void saveRoutes(final String name, double distance, double cost){

        Route route = new Route();
        route.setName(name);
        route.setDistance(distance);
        route.setCost(cost);
        route.setCalendarID(sharedPreferences.getString("calendarID", ""));

        route.saveInBackground();
        setResult(1);
        finish();
    }

    private void editRoute(final String name, final double distance, final double cost){

        ParseQuery<Route> query = ParseQuery.getQuery("Routes");

        query.getInBackground(route_id, new GetCallback<Route>() {
            @Override
            public void done(Route object, ParseException e) {

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

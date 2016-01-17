package joao.splitride.app.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.NoButtonListAdapter;
import joao.splitride.app.entities.ComposedRoute;
import joao.splitride.app.entities.Route;
import joao.splitride.app.entities.Segment;

public class AddEditRoute extends AppCompatActivity implements OnClickListener{

    private Button ok, cancel;
    private LinearLayout parentLayout;
    private EditText name;
    private ListView route_segments;
    private List<Segment> savedSegments = new ArrayList<Segment>(), allSegments;
    private Intent editRoute;
    private String route_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route_layout);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        name = (EditText) findViewById(R.id.route_name);
        route_segments = (ListView) findViewById(R.id.segments_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        editRoute = getIntent();

        ParseQuery<Segment> segmentParseQuery = new ParseQuery<Segment>("Segments");
        segmentParseQuery.findInBackground(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> objects, ParseException e) {
                allSegments = objects;
            }
        });

        if(editRoute.getStringExtra("name") != null){

            ok.setText(R.string.edit);

            name.setText(""+editRoute.getStringExtra("name"));
            route_id = editRoute.getStringExtra("id");
            savedSegments = editRoute.getParcelableArrayListExtra("segments");

            NoButtonListAdapter adapter = new NoButtonListAdapter(AddEditRoute.this, R.layout.custom_line_list_view_no_button, savedSegments);

            route_segments.setAdapter(adapter);
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

                            if(route_name.length() == 0 || savedSegments.size() == 0){
                                    Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                                    .show();
                            }else{
                                if(ok.getText().toString().equalsIgnoreCase("ok"))
                                    saveRoutes(route_name);
                                else
                                    editRoute(route_name);
                            }

                            break;

            case R.id.cancel:	finish();
                                break;
        }

    }

    public void saveRoutes(final String name){

        Route route = new Route();
        route.setName(name);

        route.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {


                    ParseQuery<Route> query = ParseQuery.getQuery("Routes");
                    query.whereEqualTo("Name", name);

                    query.findInBackground(new FindCallback<Route>() {
                        @Override
                        public void done(List<Route> objects, ParseException e) {
                            for (Segment s : savedSegments) {

                                ComposedRoute composedRoute = new ComposedRoute();

                                composedRoute.setRouteId(objects.get(0).getObjectId());
                                composedRoute.setSegmentId(s.getObjectId());

                                composedRoute.saveInBackground();
                                finish();

                            }
                        }

                    });
                }
            }

        });
    }

    private void editRoute(final String name){

        ParseQuery<Route> query = ParseQuery.getQuery("Routes");

        query.getInBackground(route_id, new GetCallback<Route>() {
            @Override
            public void done(Route object, ParseException e) {

                if(e == null){
                    object.setName(name);
                    object.saveInBackground();

                    ParseQuery<ComposedRoute> composed_query = ParseQuery.getQuery("ComposedRoutes");
                    composed_query.whereEqualTo("RouteID", route_id);

                    composed_query.findInBackground(new FindCallback<ComposedRoute>() {
                        @Override
                        public void done(List<ComposedRoute> objects, ParseException e) {

                            if (e == null) {

                                for (ComposedRoute cr : objects) {
                                    cr.deleteInBackground();
                                }

                                for(Segment s: savedSegments){

                                    ParseQuery segmentQuery = ParseQuery.getQuery("Segments");
                                    segmentQuery.whereEqualTo("Name", s.getName());
                                    try {
                                        ComposedRoute composedRoute = new ComposedRoute();

                                        composedRoute.setRouteId(route_id);
                                        composedRoute.setSegmentId(segmentQuery.getFirst().getObjectId());
                                        composedRoute.saveInBackground();

                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            }
                        }
                    });


                    finish();
                }
            }
        });
    }

    public void addSegmentsHandler(View v){

        ParseQuery<Segment> query = ParseQuery.getQuery("Segments");

        query.findInBackground(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> segmentsList, ParseException error) {
                if (error == null) {
                    final String[] items = new String[segmentsList.size()];

                    for (int i = 0; i < segmentsList.size(); i++) {

                        items[i] = segmentsList.get(i).getName();
                    }

                    final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

                    boolean[] checkeditems = new boolean[items.length];
                    Arrays.fill(checkeditems, false);

                    for (Segment s : savedSegments) {

                        if (Arrays.asList(items).indexOf(s.getName()) >= 0) {
                            selectedItems.add(Arrays.asList(items).indexOf(s.getName()));
                            checkeditems[Arrays.asList(items).indexOf(s.getName())] = true;
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddEditRoute.this);
                    AlertDialog dialog;

                    builder.setTitle(R.string.select_segments);
                    builder.setMultiChoiceItems(items, checkeditems, new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                // write your code when user checked the checkbox
                                selectedItems.add(indexSelected);
                                savedSegments.add(allSegments.get(indexSelected));
                            } else if (selectedItems.contains(indexSelected)) {
                                // Else, if the item is already in the array, remove it
                                // write your code when user Uchecked the checkbox

                                selectedItems.remove(Integer.valueOf(indexSelected));

                                ArrayList<Segment> aux = new ArrayList<Segment>();
                                for(Segment s : savedSegments){
                                    if(!s.getName().equalsIgnoreCase(allSegments.get(indexSelected).getName()))
                                        aux.add(s);
                                }
                                savedSegments = aux;

                            }
                        }
                    })
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    NoButtonListAdapter adapter = new NoButtonListAdapter(AddEditRoute.this, R.layout.custom_line_list_view_no_button, savedSegments);

                                    route_segments.setAdapter(adapter);

                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Your code when user clicked on Cancel

                                }
                            });

                    dialog = builder.create();
                    dialog.show();

                } else {
                    Log.d("Erro", error.getMessage());
                }
            }
        });


    }
}

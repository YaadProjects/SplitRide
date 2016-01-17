package joao.splitride.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.RouteListAdapter;
import joao.splitride.app.entities.ComposedRoute;
import joao.splitride.app.entities.Route;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.settings.AddEditRoute;

/**
 * Created by Joao on 03-12-2015.
 */
public class RoutesFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView routes_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        routes_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);


        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<Route> query = ParseQuery.getQuery("Routes");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber rotas.");
        progressDialog.show();

        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {
                    RouteListAdapter adapter = new RouteListAdapter(getContext(), R.layout.custom_line_list_view, routesList);

                    routes_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<Route> query = ParseQuery.getQuery("Routes");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {
                    RouteListAdapter adapter = new RouteListAdapter(getContext(), R.layout.custom_line_list_view, routesList);

                    routes_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("Error", error.getMessage());
                }
            }
        });
    }


    public void removeOnClickHandler(View v) {

        final Route route = (Route) v.getTag();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Delete route");
        dialog.setMessage("Are you sure you want to delete this route?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ParseQuery<Route> query = ParseQuery.getQuery("Routes");
                query.whereEqualTo("objectId", route.getObjectId());

                query.findInBackground(new FindCallback<Route>() {
                    @Override
                    public void done(List<Route> objects, ParseException e) {
                        if (e == null) {
                            // object will be your game score
                            Route object = objects.get(0);

                            ParseQuery<ComposedRoute> query = ParseQuery.getQuery("ComposedRoutes");
                            query.whereEqualTo("RouteID", object.getObjectId());

                            query.findInBackground(new FindCallback<ComposedRoute>() {
                                @Override
                                public void done(List<ComposedRoute> objects, ParseException e) {
                                    if (e == null) {

                                        for (ComposedRoute cr : objects) {
                                            cr.deleteInBackground();
                                        }
                                    }
                                }
                            });

                            object.deleteInBackground();
                            onRefresh();
                        } else {
                            // something went wrong
                            //Snackbar.make(parentLayout, getResources().getString(R.string.all_fields_mandatory), Snackbar.LENGTH_LONG)
                            //        .show();
                            Log.d("Error", e.getMessage().toString());
                        }
                    }
                });
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        dialog.show();

    }


    public void editOnClickHandler(View v){

        final Route route = (Route) v.getTag();

        final Intent intent = new Intent(getActivity(), AddEditRoute.class);
        intent.putExtra("id", route.getObjectId());
        intent.putExtra("name", route.getName());

        ParseQuery<ComposedRoute> query = ParseQuery.getQuery("ComposedRoutes");

        query.findInBackground(new FindCallback<ComposedRoute>() {
            @Override
            public void done(List<ComposedRoute> composedRoutesList, ParseException error) {
                if (error == null) {

                    final ArrayList<Segment> segmentsID = new ArrayList<Segment>();

                    for (ComposedRoute cr : composedRoutesList) {
                        ParseQuery<Segment> query_segment = ParseQuery.getQuery("Segments");
                        query_segment.whereEqualTo("objectId", cr.getSegmentId());
                        try {
                            segmentsID.add(query_segment.getFirst());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    intent.putExtra("segments", segmentsID);
                    startActivity(intent);

                } else {
                    Log.d("Erro", error.getMessage());
                }
            }
        });



    }


}

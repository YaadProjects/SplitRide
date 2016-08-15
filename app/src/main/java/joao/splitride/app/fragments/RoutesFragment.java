package joao.splitride.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.RouteListAdapter;
import joao.splitride.app.entities.Route;
import joao.splitride.app.settings.AddEditRoute;

/**
 * Created by Joao on 03-12-2015.
 */
public class RoutesFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView routes_list;
    private RouteListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        routes_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<Route> query = ParseQuery.getQuery("Routes");
        query.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber rotas.");
        progressDialog.show();

        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {
                    routes_list.setAdapter(new RouteListAdapter<Route>(getContext(), R.layout.listview_item, R.id.position, routesList));

                    routes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((SwipeLayout)(routes_list.getChildAt(position - routes_list.getFirstVisiblePosition()))).open(true);
                        }
                    });
                    routes_list.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.e("ListView", "OnTouch");
                            return false;
                        }
                    });
                    routes_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getContext(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    routes_list.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            Log.e("ListView", "onScrollStateChanged");
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        }
                    });

                    routes_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.e("ListView", "onItemSelected:" + position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Log.e("ListView", "onNothingSelected:");
                        }
                    });


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
        query.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {


                    //routes_list.setAdapter(adapter);
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
        intent.putExtra("distance", route.getDistance());
        intent.putExtra("cost", route.getCost());

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }


}

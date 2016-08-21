package joao.splitride.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.TripListAdapter;
import joao.splitride.app.entities.Trip;
import joao.splitride.app.entities.Vehicle;

/**
 * Created by Joao on 03-12-2015.
 */
public class TripsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView trips_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private String date;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);

        trips_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        date = getArguments().getString("date");

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<Trip> query = ParseQuery.getQuery("Trips");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));
        query.whereEqualTo("Date", date);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber viagens.");
        progressDialog.show();

        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> vehiclesList, ParseException error) {
                if (error == null) {

                    trips_list.setAdapter(new TripListAdapter<Trip>(getContext(), R.layout.listview_item, R.id.line_name, vehiclesList));

                    progressDialog.dismiss();
                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });


//       vehicles_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                ((SwipeLayout)(vehicles_list.getChildAt(position - vehicles_list.getFirstVisiblePosition()))).open(true);
//
//                LinearLayout bottom = (LinearLayout) ((SwipeLayout)(vehicles_list.getChildAt(position - vehicles_list.getFirstVisiblePosition()))).findViewWithTag("Bottom2");
//
//                bottom.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        final Vehicle vehicle = (Vehicle) v.getTag();
//
//                        final Intent intent = new Intent(getActivity(), AddEditVehicle.class);
//                        intent.putExtra("id", vehicle.getObjectId());
//                        intent.putExtra("name", vehicle.getVehicleName());
//                        intent.putExtra("consumption", vehicle.getVehicleConsumption());
//                        intent.putExtra("owner", vehicle.getUserID());
//
//                        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
//                        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));
//
//                        query.findInBackground(new FindCallback<UsersByCalendars>() {
//                            @Override
//                            public void done(List<UsersByCalendars> usersByCalendarsList, ParseException error) {
//                                if (error == null) {
//
//                                    ArrayList<String> usernames = new ArrayList<String>();
//
//                                    for (UsersByCalendars uc : usersByCalendarsList) {
//                                        ParseQuery<ParseUser> query_users = ParseUser.getQuery();
//                                        query_users.whereEqualTo("objectId", uc.getUserID());
//
//                                        try {
//                                            ParseUser parseUser = query_users.getFirst();
//                                            usernames.add(parseUser.getUsername());
//
//                                            if (parseUser.getObjectId().equalsIgnoreCase(vehicle.getUserID()))
//                                                intent.putExtra("owner", parseUser.getUsername());
//
//                                        } catch (ParseException e1) {
//                                            e1.printStackTrace();
//                                        }
//                                    }
//
//                                    intent.putExtra("usernames", usernames);
//                                    startActivityForResult(intent, 1);
//
//                                } else {
//                                    Log.d("Erro", error.getMessage());
//                                }
//                            }
//                        });
//                    }
//                });
//
//                bottom.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final Vehicle vehicle = (Vehicle) v.getTag();
//
//                        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
//                        dialog.setTitle("Delete vehicle");
//                        dialog.setMessage("Are you sure you want to delete this vehicle?");
//                        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//                                ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
//                                query.whereEqualTo("objectId", vehicle.getObjectId());
//
//                                try {
//                                    Vehicle vehicle_delete = query.getFirst();
//
//                                    vehicle_delete.deleteInBackground();
//                                    onRefresh();
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//
//                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//
//                            }
//                        });
//
//                        dialog.show();
//
//                    }
//                });
//
//
//
//                Log.w("applications", "será que aqui funciona");
//            }
//        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<Vehicle>() {
            @Override
            public void done(List<Vehicle> vehiclesList, ParseException error) {
//                if (error == null) {
//                    VehicleListAdapter adapter = new VehicleListAdapter(getContext(), R.layout.listview_item, vehiclesList);
//
//                    vehicles_list.setAdapter(adapter);
//                    swipeRefreshLayout.setRefreshing(false);
//
//                } else {
//                    Log.d("Error", error.getMessage());
//                }
            }
        });
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

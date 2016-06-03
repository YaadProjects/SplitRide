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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.VehicleListAdapter;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.entities.Vehicle;
import joao.splitride.app.settings.AddEditVehicle;

/**
 * Created by Joao on 03-12-2015.
 */
public class VehiclesFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView vehicles_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        vehicles_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber veiculos.");
        progressDialog.show();

        query.findInBackground(new FindCallback<Vehicle>() {
            @Override
            public void done(List<Vehicle> vehiclesList, ParseException error) {
                if (error == null) {
                    VehicleListAdapter adapter = new VehicleListAdapter(getContext(), R.layout.custom_line_list_view, vehiclesList);

                    vehicles_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<Vehicle>() {
            @Override
            public void done(List<Vehicle> vehiclesList, ParseException error) {
                if (error == null) {
                    VehicleListAdapter adapter = new VehicleListAdapter(getContext(), R.layout.custom_line_list_view, vehiclesList);

                    vehicles_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("Error", error.getMessage());
                }
            }
        });
    }


    public void removeOnClickHandler(View v) {

        final Vehicle vehicle = (Vehicle) v.getTag();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Delete vehicle");
        dialog.setMessage("Are you sure you want to delete this vehicle?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
                query.whereEqualTo("objectId", vehicle.getObjectId());

                try {
                    Vehicle vehicle_delete = query.getFirst();

                    vehicle_delete.deleteInBackground();
                    onRefresh();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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


    public void editOnClickHandler(View v) {

        final Vehicle vehicle = (Vehicle) v.getTag();

        final Intent intent = new Intent(getActivity(), AddEditVehicle.class);
        intent.putExtra("id", vehicle.getObjectId());
        intent.putExtra("name", vehicle.getVehicleName());
        intent.putExtra("consumption", vehicle.getVehicleConsumption());
        intent.putExtra("owner", vehicle.getUserID());

        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> usersByCalendarsList, ParseException error) {
                if (error == null) {

                    ArrayList<String> usernames = new ArrayList<String>();

                    for (UsersByCalendars uc : usersByCalendarsList) {
                        ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                        query_users.whereEqualTo("objectId", uc.getUserID());

                        try {
                            ParseUser parseUser = query_users.getFirst();
                            usernames.add(parseUser.getUsername());

                            if (parseUser.getObjectId().equalsIgnoreCase(vehicle.getUserID()))
                                intent.putExtra("owner", parseUser.getUsername());

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    intent.putExtra("usernames", usernames);
                    startActivityForResult(intent, 1);

                } else {
                    Log.d("Erro", error.getMessage());
                }
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

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
import joao.splitride.app.custom.VehicleListAdapter;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.entities.Vehicle;


public class VehiclesFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView vehicles_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);

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

                    vehicles_list.setAdapter(new VehicleListAdapter<Segment>(getContext(), R.layout.listview_item, R.id.line_name, vehiclesList));

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
                    VehicleListAdapter adapter = new VehicleListAdapter(getContext(), R.layout.listview_item, R.id.line_name, vehiclesList);

                    vehicles_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("Error", error.getMessage());
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

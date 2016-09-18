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
            public void done(List<Trip> tripList, ParseException error) {
                if (error == null) {

                    trips_list.setAdapter(new TripListAdapter<Trip>(getContext(), R.layout.listview_item, R.id.line_name, tripList));

                    progressDialog.dismiss();


                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });


    }



    @Override
    public void onRefresh() {
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<Trip> query = ParseQuery.getQuery("Trips");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));
        query.whereEqualTo("Date", date);

        query.findInBackground(new FindCallback<Trip>() {
            @Override
            public void done(List<Trip> tripList, ParseException error) {
                if (error == null) {
                    trips_list.setAdapter(new TripListAdapter<Trip>(getContext(), R.layout.listview_item, R.id.line_name, tripList));

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

        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }


}

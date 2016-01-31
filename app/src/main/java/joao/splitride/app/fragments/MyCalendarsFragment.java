package joao.splitride.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.UsersByCalendars;

/**
 * Created by Joao on 17-01-2016.
 */
public class MyCalendarsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView calendars_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendars, container, false);

        calendars_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        SharedPreferences sharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");

        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("UserID", userID);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber calendários.");
        progressDialog.show();

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> calendarsList, ParseException error) {
                if (error == null) {

                    ArrayList<String> calendarID = new ArrayList<String>();

                    for(UsersByCalendars calendar : calendarsList){
                        calendarID.add(calendar.getCalendarID());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, calendarID);
                    calendars_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> calendarsList, ParseException error) {
                if (error == null) {

                    ArrayList<String> calendarID = new ArrayList<String>();

                    for(UsersByCalendars calendar : calendarsList){
                        calendarID.add(calendar.getCalendarID());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, calendarID);

                    calendars_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("Error", error.getMessage());
                }
            }
        });
    }
}

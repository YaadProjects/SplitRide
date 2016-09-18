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

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.CalendarListAdapter;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.UsersByCalendars;


public class MyCalendarsFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView calendars_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userID;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);

        calendars_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        sharedPreferences = this.getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
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

                    ArrayList<Calendars> calendarID = new ArrayList<Calendars>();

                    for (UsersByCalendars calendar : calendarsList) {

                        ParseQuery<Calendars> cal_query = ParseQuery.getQuery("Calendars");
                        cal_query.whereEqualTo("objectId", calendar.getCalendarID());

                        try {

                            calendarID.add(cal_query.getFirst());

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    CalendarListAdapter adapter = new CalendarListAdapter(getContext(), R.layout.listview_item, R.id.line_name, calendarID);
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
        userID = sharedPreferences.getString("userID", "");
        query.whereEqualTo("UserID", userID);

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> calendarsList, ParseException error) {
                if (error == null) {

                    ArrayList<Calendars> calendarID = new ArrayList<Calendars>();

                    for(UsersByCalendars calendar : calendarsList){

                        ParseQuery<Calendars> cal_query = ParseQuery.getQuery("Calendars");
                        cal_query.whereEqualTo("objectId", calendar.getCalendarID());

                        try {

                            calendarID.add(cal_query.getFirst());

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                    CalendarListAdapter adapter = new CalendarListAdapter(getContext(), R.layout.listview_item, R.id.line_name, calendarID);
                    calendars_list.setAdapter(adapter);

                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("score", "Error: " + error.getMessage());
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

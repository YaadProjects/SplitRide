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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.CalendarListAdapter;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.settings.AddEditCalendar;

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
        View rootView = inflater.inflate(R.layout.fragment_my_calendars, container, false);

        calendars_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        SharedPreferences sharedPreferences = this.getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
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

                    CalendarListAdapter adapter = new CalendarListAdapter(getContext(), R.layout.custom_line_list_view, calendarID);
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

                    CalendarListAdapter adapter = new CalendarListAdapter(getContext(), R.layout.listview_item, calendarID);
                    calendars_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });
    }

    public void removeOnClickHandler(View v) {

        final Calendars calendar = (Calendars) v.getTag();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Delete route");
        dialog.setMessage("Are you sure you want to delete this calendar?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ParseQuery<Calendars> query = ParseQuery.getQuery("Calendars");
                query.whereEqualTo("objectId", calendar.getObjectId());

                query.getFirstInBackground(new GetCallback<Calendars>() {
                    @Override
                    public void done(Calendars object, ParseException e) {
                        if (e == null) {

                            ParseQuery<UsersByCalendars> query2 = ParseQuery.getQuery("UsersByCalendar");
                            query2.whereEqualTo("CalendarID", object.getObjectId());

                            query2.findInBackground(new FindCallback<UsersByCalendars>() {
                                @Override
                                public void done(List<UsersByCalendars> objects, ParseException e) {
                                    if (e == null) {

                                        for (UsersByCalendars uc : objects) {
                                            uc.deleteInBackground();
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

        final Calendars calendars = (Calendars) v.getTag();

        final Intent intent = new Intent(getActivity(), AddEditCalendar.class);
        intent.putExtra("id", calendars.getObjectId());
        intent.putExtra("name", calendars.getName());


        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("CalendarID", calendars.getObjectId());

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                if (e == null) {

                    SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
                    String userID = sharedPreferences.getString("userID", "");

                    for (UsersByCalendars uc : objects) {

                        if (uc.getUserID().equalsIgnoreCase(userID))
                            intent.putExtra("default", uc.getDefault());
                    }

                    startActivity(intent);

                } else {
                    Log.d("Error", e.getMessage());
                }
            }
        });


    }

}

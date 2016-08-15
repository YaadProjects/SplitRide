package joao.splitride.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import joao.splitride.app.custom.UserListAdapter;
import joao.splitride.app.entities.UsersByCalendars;

/**
 * Created by Joao on 17-01-2016.
 */
public class UsersFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView users_list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_users, container, false);

        users_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);


        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber utilizadores.");
        progressDialog.show();

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                if (e == null) {

                    ArrayList<ParseUser> users = new ArrayList<ParseUser>();

                    for (UsersByCalendars uc : objects) {

                        ParseQuery<ParseUser> query_user = ParseUser.getQuery();
                        query_user.whereEqualTo("objectId", uc.getUserID());

                        try {
                            ParseUser user = query_user.getFirst();

                            users.add(user);

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    UserListAdapter adapter = new UserListAdapter(getContext(), R.layout.custom_line_list_view_delete_only, users);
                    users_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else e.printStackTrace();

            }
        });


    }


    @Override
    public void onRefresh() {

        ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
        query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<UsersByCalendars>() {
            @Override
            public void done(List<UsersByCalendars> objects, ParseException e) {

                if (e == null) {

                    ArrayList<ParseUser> users = new ArrayList<ParseUser>();

                    for (UsersByCalendars uc : objects) {

                        ParseQuery<ParseUser> query_user = ParseUser.getQuery();
                        query_user.whereEqualTo("objectId", uc.getUserID());

                        try {
                            ParseUser user = query_user.getFirst();

                            users.add(user);

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    UserListAdapter adapter = new UserListAdapter(getContext(), R.layout.listview_item, users);
                    users_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                } else e.printStackTrace();

            }
        });
    }

    public void removeOnClickHandler(View v) {
        final ParseUser user = (ParseUser) v.getTag();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Delete route");
        dialog.setMessage("Are you sure you want to delete this user from this calendar?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
                query.whereEqualTo("UserID", user.getObjectId());

                query.findInBackground(new FindCallback<UsersByCalendars>() {
                    @Override
                    public void done(List<UsersByCalendars> objects, ParseException e) {

                        if (e == null) {

                            for (UsersByCalendars object : objects) {
                                if (object.getCalendarID().equalsIgnoreCase(sharedPreferences.getString("calendarID", "")))
                                    object.deleteInBackground();

                            }

                            onRefresh();
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
}
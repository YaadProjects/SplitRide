package joao.splitride.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
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

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

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

                    UserListAdapter adapter = new UserListAdapter(getContext(), R.layout.custom_line_list_view, users);
                    users_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else e.printStackTrace();

            }
        });


    }


    @Override
    public void onRefresh() {

    }
}

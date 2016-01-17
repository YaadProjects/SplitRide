package joao.splitride.app.fragments;

import android.app.ProgressDialog;
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
import joao.splitride.app.entities.User;

/**
 * Created by Joao on 17-01-2016.
 */
public class UsersFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView users_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);

        users_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);


        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<User> query = ParseQuery.getQuery("Users");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber utilizadores.");
        progressDialog.show();

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> usersList, ParseException error) {
                if (error == null) {
                    //NoButtonListAdapter adapter = new RouteListAdapter(getContext(), R.layout.custom_line_list_view, usersList);

                    //users_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {

    }
}

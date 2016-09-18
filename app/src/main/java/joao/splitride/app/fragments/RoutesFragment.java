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
import joao.splitride.app.custom.RouteListAdapter;
import joao.splitride.app.entities.Route;


public class RoutesFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView routes_list;
    private RouteListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);

        routes_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<Route> query = ParseQuery.getQuery("Routes");
        query.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber rotas.");
        progressDialog.show();

        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {
                    mAdapter = new RouteListAdapter<Route>(getContext(), R.layout.listview_item, R.id.line_name, routesList);
                    routes_list.setAdapter(mAdapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("score", "Error: " + error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<Route> query = ParseQuery.getQuery("Routes");
        query.whereEqualTo("calendarID", sharedPreferences.getString("calendarID", ""));

        query.findInBackground(new FindCallback<Route>() {
            @Override
            public void done(List<Route> routesList, ParseException error) {
                if (error == null) {
                    routes_list.setAdapter(new RouteListAdapter<Route>(getContext(), R.layout.listview_item, R.id.line_name, routesList));

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

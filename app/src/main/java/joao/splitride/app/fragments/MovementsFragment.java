package joao.splitride.app.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.MovementRecyclerAdapter;
import joao.splitride.app.entities.Movement;

/**
 * Created by Joao on 17-01-2016.
 */
public class MovementsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private TextView balance;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private double payed = 0.0, received = 0.0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movements, container, false);

        balance = (TextView) rootView.findViewById(R.id.balance);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        sharedPreferences = getContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber movimentos.");
        progressDialog.show();


        ParseQuery<Movement> query_movements = ParseQuery.getQuery("Movements");
        query_movements.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query_movements.findInBackground(new FindCallback<Movement>() {
            @Override
            public void done(List<Movement> objects, ParseException e) {

                setBalance(objects);

                if (e == null) {
                    mAdapter = new MovementRecyclerAdapter(objects, getContext());
                    mRecyclerView.setAdapter(mAdapter);

                    progressDialog.dismiss();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }


    @Override
    public void onRefresh() {

        ParseQuery<Movement> query_movements = ParseQuery.getQuery("Movements");
        query_movements.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query_movements.findInBackground(new FindCallback<Movement>() {
            @Override
            public void done(List<Movement> objects, ParseException e) {

                if (e == null) {

                    setBalance(objects);

                    mAdapter = new MovementRecyclerAdapter(objects, getContext());
                    mRecyclerView.setAdapter(mAdapter);

                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void setBalance(List<Movement> movements) {
        for (Movement m : movements) {
            if (sharedPreferences.getString("userID", "").equalsIgnoreCase(m.getFromUserID()))
                payed += m.getValue();
            else if (sharedPreferences.getString("userID", "").equalsIgnoreCase(m.getToUserID()))
                received += m.getValue();
        }

        balance.setText("" + (payed - received));
    }


}
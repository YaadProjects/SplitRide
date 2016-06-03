package joao.splitride.app.fragments;


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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.MyRecyclerViewAdapter;
import joao.splitride.app.entities.Movement;

/**
 * Created by Joao on 17-01-2016.
 */
public class MovementsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movements, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new MyRecyclerViewAdapter(getDataSet());
        //mRecyclerView.setAdapter(mAdapter);

        sharedPreferences = getContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        ParseQuery<Movement> query_movements = ParseQuery.getQuery("Movements");
        query_movements.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query_movements.findInBackground(new FindCallback<Movement>() {
            @Override
            public void done(List<Movement> objects, ParseException e) {

                if (e == null) {
                    mAdapter = new MyRecyclerViewAdapter(objects);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });

        //swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        //    swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }


    @Override
    public void onRefresh() {


    }


}
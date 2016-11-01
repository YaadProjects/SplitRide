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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.custom.AccountRecyclerAdapter;
import joao.splitride.app.entities.Account;

/**
 * Created by joaoferreira on 30/10/16.
 */

public class AccountsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        sharedPreferences = getContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber contas.");
        progressDialog.show();


        ParseQuery<Account> query_movements = ParseQuery.getQuery("Accounts");
        query_movements.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

        query_movements.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> objects, ParseException e) {

                if (e == null) {
                    mAdapter = new AccountRecyclerAdapter(objects, getContext());
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

    }
}

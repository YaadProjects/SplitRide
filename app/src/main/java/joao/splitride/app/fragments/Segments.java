package joao.splitride.app.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import joao.splitride.app.custom.SegmentListAdapter;
import joao.splitride.app.entities.Segment;
import joao.splitride.app.settings.AddEditSegment;

public class Segments extends ListFragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView segments_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_segments, container, false);

        segments_list = (ListView) rootView.findViewById(android.R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);


        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ParseQuery<Segment> query = ParseQuery.getQuery("Segments");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Por favor espere");
        progressDialog.setMessage("A receber segmentos.");
        progressDialog.show();

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> segmentsList, ParseException error) {
                if (error == null) {
                    SegmentListAdapter adapter = new SegmentListAdapter(getContext(), R.layout.custom_line_list_view, segmentsList);

                    segments_list.setAdapter(adapter);

                    progressDialog.dismiss();

                } else {
                    Log.d("Erro", error.getMessage());
                }
            }
        });

    }


    @Override
    public void onRefresh() {
        ParseQuery<Segment> query = ParseQuery.getQuery("Segments");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> segmentsList, ParseException error) {
                if (error == null) {
                    SegmentListAdapter adapter = new SegmentListAdapter(getContext(), R.layout.custom_line_list_view, segmentsList);

                    segments_list.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.d("Error", error.getMessage());
                }
            }
        });
    }

    public void removeOnClickHandler(View v){

        final Segment segment = (Segment) v.getTag();

        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Delete Segment");
        dialog.setMessage("Are you sure you want to delete this segment?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ParseQuery<Segment> query = ParseQuery.getQuery("Segments");
                query.whereEqualTo("objectId", segment.getObjectId());
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                query.findInBackground(new FindCallback<Segment>() {
                    @Override
                    public void done(List<Segment> objects, ParseException e) {
                        if (e == null) {
                            // object will be your game score
                            Segment object = objects.get(0);
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

        final Segment segment = (Segment) v.getTag();

        Intent intent = new Intent(getActivity(), AddEditSegment.class);
        intent.putExtra("id", segment.getObjectId());
        intent.putExtra("name", segment.getName());
        intent.putExtra("distance", segment.getDistance());
        intent.putExtra("cost", segment.getCost());

        startActivity(intent);

    }
}

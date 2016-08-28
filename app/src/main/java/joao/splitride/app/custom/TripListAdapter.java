package joao.splitride.app.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.PassengersInTrip;
import joao.splitride.app.entities.Trip;
import joao.splitride.app.settings.AddEditTrip;

/**
 * Created by Joao on 15-08-2016.
 */
public class TripListAdapter<T> extends ArraySwipeAdapter implements View.OnClickListener {
    private List<Trip> items;
    private int layoutResourceId;
    private Context context;

    public TripListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public TripListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public TripListAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public TripListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public TripListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public TripListAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);

        this.layoutResourceId = resource;
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        TripListAdapter.TripHolder holder = null;

//        if(position < items.size()){
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new TripHolder();
        holder.trip = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.trip);
        holder.remove.setTag(holder.trip);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        holder.edit.setOnClickListener(this);
        holder.remove.setOnClickListener(this);

        row.setTag(holder);

        setupItem(holder);
//        }


        return row;
    }

    private void setupItem(TripHolder holder) {

        ParseQuery<ParseUser> query_user = ParseUser.getQuery();
        query_user.whereEqualTo("objectId", holder.trip.getDriverID());

        try {
            holder.name.setText("Driver: "+ query_user.getFirst().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                final Intent intent = new Intent(context, AddEditTrip.class);

                intent.putExtra("date", ((Trip) v.getTag()).getDate());
                intent.putExtra("driver", ((Trip) v.getTag()).getDriverID());
                intent.putExtra("vehicle", ((Trip) v.getTag()).getVehicleID());
                intent.putExtra("roundtrip", ((Trip) v.getTag()).getRoundTrip());

                ParseQuery<PassengersInTrip> query_pass = ParseQuery.getQuery("PassengersInTrip");
                query_pass.whereEqualTo("TripID", ((Trip) v.getTag()).getObjectId());

                query_pass.findInBackground(new FindCallback<PassengersInTrip>() {
                    @Override
                    public void done(List<PassengersInTrip> objects, ParseException e) {

                        ArrayList<String> passengers_names = new ArrayList<String>();
                        ArrayList<String> passengers_routes = new ArrayList<String>();

                        for (PassengersInTrip p : objects) {

                            passengers_names.add(p.getPassengerID());
                            passengers_routes.add(p.getRouteID());
                        }

                        intent.putExtra("passengers_names", passengers_names);
                        intent.putExtra("passengers_routes", passengers_routes);

                        context.startActivity(intent);
                    }
                });


                break;

            case R.id.delete:
                final Trip trip = (Trip) v.getTag();

                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Delete Trip");
                dialog.setMessage("Are you sure you want to delete this trip?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        trip.deleteInBackground();
                        items.remove(trip);
                        notifyDataSetChanged();

                        ParseQuery<PassengersInTrip> query = ParseQuery.getQuery("PassengersIntrip");
                        query.whereEqualTo("TripID", trip.getObjectId());

                        query.findInBackground(new FindCallback<PassengersInTrip>() {
                            @Override
                            public void done(List<PassengersInTrip> objects, ParseException e) {

                                for (PassengersInTrip p : objects) {
                                    p.deleteInBackground();
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

                break;
        }
    }

    private List<Trip> updateList(Trip trip) {

        List<Trip> new_list = new ArrayList<Trip>();

        for (Trip t : items) {
            if (t.getObjectId().equalsIgnoreCase(trip.getObjectId()) == false)
                new_list.add(t);
        }

        return new_list;
    }

    public static class TripHolder {
        Trip trip;
        TextView name;
        ImageView edit, remove;
    }
}

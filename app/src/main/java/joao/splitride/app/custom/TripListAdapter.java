package joao.splitride.app.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Trip;

/**
 * Created by Joao on 15-08-2016.
 */
public class TripListAdapter<T> extends ArraySwipeAdapter {
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

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new TripHolder();
        holder.trip = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.trip);
        holder.remove.setTag(holder.trip);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(TripHolder holder) {

        ParseQuery<ParseUser> query_user = ParseUser.getQuery();
        query_user.whereEqualTo("objectId", holder.trip.getDriverID());

        holder.name.setText("Driver: " + holder.trip.getDriverID());

        /*try {
            holder.name.setText("Driver: "+ query_user.getFirst().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }

    public static class TripHolder {
        Trip trip;
        TextView name;
        ImageView edit, remove;
    }
}

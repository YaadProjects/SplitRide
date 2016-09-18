package joao.splitride.app.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Route;
import joao.splitride.app.settings.AddEditRoute;


public class RouteListAdapter<T> extends ArraySwipeAdapter implements View.OnClickListener {
    private List<Route> items;
    private int layoutResourceId;
    private Context context;

    public RouteListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public RouteListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public RouteListAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public RouteListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public RouteListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public RouteListAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);

        this.layoutResourceId = resource;
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        RouteHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new RouteHolder();
        holder.route = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.route);
        holder.remove.setTag(holder.route);

        holder.name = (TextView)row.findViewById(R.id.line_name);

        holder.edit.setOnClickListener(this);
        holder.remove.setOnClickListener(this);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(RouteHolder holder) {
        holder.name.setText(holder.route.getName());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                final Route route = (Route) v.getTag();

                final Intent intent = new Intent(context, AddEditRoute.class);
                intent.putExtra("id", route.getObjectId());
                intent.putExtra("name", route.getName());
                intent.putExtra("distance", route.getDistance());
                intent.putExtra("cost", route.getCost());

                ((Activity) context).startActivityForResult(intent, 1);
                break;

            case R.id.delete:
                final Route route2 = (Route) v.getTag();

                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Delete route");
                dialog.setMessage("Are you sure you want to delete this route?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        ParseQuery<Route> query = ParseQuery.getQuery("Routes");
                        query.whereEqualTo("objectId", route2.getObjectId());

                        query.findInBackground(new FindCallback<Route>() {
                            @Override
                            public void done(List<Route> objects, ParseException e) {
                                if (e == null) {
                                    // object will be your game score
                                    Route object = objects.get(0);
                                    object.deleteInBackground();

                                    items.remove(route2);
                                    notifyDataSetChanged();
                                } else {
                                    Log.d("Error", e.getMessage());
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


    public static class RouteHolder{
        Route route;
        TextView name;
        ImageView edit, remove;
    }
}

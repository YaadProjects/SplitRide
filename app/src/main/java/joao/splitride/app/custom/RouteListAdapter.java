package joao.splitride.app.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Route;

/**
 * Created by Joao on 15-08-2016.
 */
public class RouteListAdapter<T> extends ArraySwipeAdapter {
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
        //holder.edit_route = (ImageButton)row.findViewById(R.id.edit);
        //holder.remove_route = (ImageButton)row.findViewById(R.id.remove);
        //holder.edit_route.setTag(holder.route);
        //holder.remove_route.setTag(holder.route);

        holder.name = (TextView)row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(RouteHolder holder) {
        holder.name.setText(holder.route.getName());
    }

    public static class RouteHolder{
        Route route;
        TextView name;
    }
}

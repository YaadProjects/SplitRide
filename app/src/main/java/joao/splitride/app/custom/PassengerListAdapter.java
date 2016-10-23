package joao.splitride.app.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;

import java.util.HashMap;
import java.util.List;

import joao.splitride.R;

/**
 * Created by joaoferreira on 15/10/16.
 */

public class PassengerListAdapter extends ArraySwipeAdapter implements View.OnClickListener {

    private List<HashMap<String, String>> items;
    private int layoutResourceId;
    private Context context;

    public PassengerListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public PassengerListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.layoutResourceId = resource;
        this.context = context;
        this.items = objects;
    }

    public PassengerListAdapter(Context context, int resource, int textViewResourceId, List objects) {
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
        PassengerRouteHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new PassengerRouteHolder();
        holder.passenger_route = items.get(position);

        holder.remove = (ImageView) row.findViewById(R.id.removeElement);
        holder.remove.setTag(holder.passenger_route);

        holder.linea = (TextView) row.findViewById(R.id.line_a);
        holder.lineb = (TextView) row.findViewById(R.id.line_b);

        holder.remove.setOnClickListener(this);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(PassengerRouteHolder holder) {
        holder.linea.setText(holder.passenger_route.get("line1"));
        holder.lineb.setText(holder.passenger_route.get("line2"));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.removeElement:

                final HashMap<String, String> passenger = (HashMap<String, String>) v.getTag();

                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Remove passenger");
                dialog.setMessage("Are you sure you want to remove this passenger?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        items.remove(passenger);
                        notifyDataSetChanged();

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

    public static class PassengerRouteHolder {
        HashMap<String, String> passenger_route;
        TextView linea, lineb;
        ImageView remove;
    }
}

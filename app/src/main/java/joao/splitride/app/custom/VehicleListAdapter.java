package joao.splitride.app.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Vehicle;


public class VehicleListAdapter extends ArrayAdapter<Vehicle> {

    private List<Vehicle> items;
    private int layoutResourceId;
    private Context context;


    public VehicleListAdapter(Context context, int layoutResourceId, List<Vehicle> items) {
        super(context, layoutResourceId, items);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        VehicleHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new VehicleHolder();
        holder.vehicle = items.get(position);
        holder.edit_vehicle = (ImageButton) row.findViewById(R.id.edit);
        holder.remove_vehicle = (ImageButton) row.findViewById(R.id.remove);
        holder.edit_vehicle.setTag(holder.vehicle);
        holder.remove_vehicle.setTag(holder.vehicle);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(VehicleHolder holder) {
        holder.name.setText(holder.vehicle.getVehicleName());
    }

    public static class VehicleHolder {
        Vehicle vehicle;
        TextView name;
        ImageButton edit_vehicle;
        ImageButton remove_vehicle;
    }
}

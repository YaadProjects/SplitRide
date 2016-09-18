package joao.splitride.app.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.entities.Vehicle;
import joao.splitride.app.settings.AddEditVehicle;

/**
 * Created by Joao on 15-08-2016.
 */
public class VehicleListAdapter<T> extends ArraySwipeAdapter implements View.OnClickListener {
    private List<Vehicle> items;
    private int layoutResourceId;
    private Context context;

    public VehicleListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public VehicleListAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public VehicleListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public VehicleListAdapter(Context context, int resource, int textViewResourceId, List objects) {
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
        VehicleHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new VehicleHolder();
        holder.vehicle = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.vehicle);
        holder.remove.setTag(holder.vehicle);

        holder.name = (TextView) row.findViewById(R.id.line_name);

        holder.edit.setOnClickListener(this);
        holder.remove.setOnClickListener(this);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(VehicleHolder holder) {
        holder.name.setText(holder.vehicle.getVehicleName());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                final Vehicle vehicle = (Vehicle) v.getTag();

                final Intent intent = new Intent(context, AddEditVehicle.class);
                intent.putExtra("id", vehicle.getObjectId());
                intent.putExtra("name", vehicle.getVehicleName());
                intent.putExtra("consumption", vehicle.getVehicleConsumption());
                intent.putExtra("owner", vehicle.getUserID());

                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

                ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
                query.whereEqualTo("CalendarID", sharedPreferences.getString("calendarID", ""));

                query.findInBackground(new FindCallback<UsersByCalendars>() {
                    @Override
                    public void done(List<UsersByCalendars> usersByCalendarsList, ParseException error) {
                        if (error == null) {

                            ArrayList<String> usernames = new ArrayList<String>();

                            for (UsersByCalendars uc : usersByCalendarsList) {
                                ParseQuery<ParseUser> query_users = ParseUser.getQuery();
                                query_users.whereEqualTo("objectId", uc.getUserID());

                                try {
                                    ParseUser parseUser = query_users.getFirst();
                                    usernames.add(parseUser.getUsername());

                                    if (parseUser.getObjectId().equalsIgnoreCase(vehicle.getUserID()))
                                        intent.putExtra("owner", parseUser.getUsername());

                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            intent.putExtra("usernames", usernames);

                            ((Activity) context).startActivityForResult(intent, 1);

                        } else {
                            Log.d("Erro", error.getMessage());
                        }
                    }
                });
                break;

            case R.id.delete:
                final Vehicle vehicle2 = (Vehicle) v.getTag();

                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Delete vehicle");
                dialog.setMessage("Are you sure you want to delete this vehicle?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        ParseQuery<Vehicle> query = ParseQuery.getQuery("Vehicles");
                        query.whereEqualTo("objectId", vehicle2.getObjectId());

                        try {
                            Vehicle vehicle_delete = query.getFirst();

                            vehicle_delete.deleteInBackground();

                            items.remove(vehicle2);
                            notifyDataSetChanged();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

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

    public static class VehicleHolder {
        Vehicle vehicle;
        TextView name;
        ImageView edit, remove;
    }
}

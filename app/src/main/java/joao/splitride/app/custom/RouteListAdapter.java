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
import joao.splitride.app.entities.Route;


public class RouteListAdapter extends ArrayAdapter<Route> {

	private List<Route> items;
	private int layoutResourceId;
	private Context context;
	
	
	public RouteListAdapter(Context context, int layoutResourceId, List<Route> items){
		super(context, layoutResourceId, items);
		
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		VehicleHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new VehicleHolder();
		holder.route = items.get(position);
		holder.edit_route = (ImageButton)row.findViewById(R.id.edit);
		holder.remove_route = (ImageButton)row.findViewById(R.id.remove);
		holder.edit_route.setTag(holder.route);
		holder.remove_route.setTag(holder.route);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(VehicleHolder holder) {
		holder.name.setText(holder.route.getName());
	}
	
	public static class VehicleHolder{
		Route route;
		TextView name;
		ImageButton edit_route;
		ImageButton remove_route;
	}	
}

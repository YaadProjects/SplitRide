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
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.Route;


public class CalendarListAdapter extends ArrayAdapter<Calendars> {

	private List<Calendars> items;
	private int layoutResourceId;
	private Context context;


	public CalendarListAdapter(Context context, int layoutResourceId, List<Calendars> items){
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
		holder.calendar = items.get(position);
		holder.edit_calendar = (ImageButton)row.findViewById(R.id.edit);
		holder.remove_calendar = (ImageButton)row.findViewById(R.id.remove);
		holder.edit_calendar.setTag(holder.calendar);
		holder.remove_calendar.setTag(holder.calendar);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(VehicleHolder holder) {
		holder.name.setText(holder.calendar.getName());
	}
	
	public static class VehicleHolder{
		Calendars calendar;
		TextView name;
		ImageButton edit_calendar;
		ImageButton remove_calendar;
	}	
}

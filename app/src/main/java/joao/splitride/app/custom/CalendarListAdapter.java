package joao.splitride.app.custom;

import android.annotation.SuppressLint;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Calendars;
import joao.splitride.app.entities.UsersByCalendars;
import joao.splitride.app.settings.AddEditCalendar;


public class CalendarListAdapter extends ArraySwipeAdapter implements View.OnClickListener {

	private List<Calendars> items;
	private int layoutResourceId;
	private Context context;

	public CalendarListAdapter(Context context, int resource) {
		super(context, resource);
	}

	public CalendarListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public CalendarListAdapter(Context context, int resource, Object[] objects) {
		super(context, resource, objects);
	}

	public CalendarListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public CalendarListAdapter(Context context, int resource, int textViewResourceId, List objects) {
		super(context, resource, textViewResourceId, objects);

		this.layoutResourceId = resource;
		this.context = context;
		this.items = objects;
	}

	
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		VehicleHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new VehicleHolder();
		holder.calendar = items.get(position);
		holder.edit_calendar = (ImageView) row.findViewById(R.id.edit);
		holder.remove_calendar = (ImageView) row.findViewById(R.id.delete);
		holder.edit_calendar.setTag(holder.calendar);
		holder.remove_calendar.setTag(holder.calendar);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		holder.edit_calendar.setOnClickListener(this);
		holder.remove_calendar.setOnClickListener(this);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(VehicleHolder holder) {
		holder.name.setText(holder.calendar.getName());
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.edit:
				final Calendars calendars = (Calendars) v.getTag();

				final Intent intent = new Intent(context, AddEditCalendar.class);
				intent.putExtra("id", calendars.getObjectId());
				intent.putExtra("name", calendars.getName());


				ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
				query.whereEqualTo("CalendarID", calendars.getObjectId());

				query.findInBackground(new FindCallback<UsersByCalendars>() {
					@Override
					public void done(List<UsersByCalendars> objects, ParseException e) {

						if (e == null) {

							SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
							String userID = sharedPreferences.getString("userID", "");

							for (UsersByCalendars uc : objects) {

								if (uc.getUserID().equalsIgnoreCase(userID))
									intent.putExtra("default", uc.getDefault());
							}

							((Activity) context).startActivityForResult(intent, 1);

						} else {
							Log.d("Error", e.getMessage());
						}
					}
				});

				break;

			case R.id.delete:
				final Calendars calendar = (Calendars) v.getTag();

				AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
				dialog.setTitle("Delete route");
				dialog.setMessage("Are you sure you want to delete this calendar?");
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ParseQuery<Calendars> query = ParseQuery.getQuery("Calendars");
						query.whereEqualTo("objectId", calendar.getObjectId());

						items.remove(calendar);
						notifyDataSetChanged();

						query.getFirstInBackground(new GetCallback<Calendars>() {
							@Override
							public void done(Calendars object, ParseException e) {
								if (e == null) {

									ParseQuery<UsersByCalendars> query2 = ParseQuery.getQuery("UsersByCalendar");
									query2.whereEqualTo("CalendarID", object.getObjectId());

									query2.findInBackground(new FindCallback<UsersByCalendars>() {
										@Override
										public void done(List<UsersByCalendars> objects, ParseException e) {
											if (e == null) {

												for (UsersByCalendars uc : objects) {
													uc.deleteInBackground();
												}
											}
										}
									});

									object.deleteInBackground();

								} else {

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
				break;


		}
	}

	public static class VehicleHolder{
		Calendars calendar;
		TextView name;
		ImageView edit_calendar, remove_calendar;
	}	
}

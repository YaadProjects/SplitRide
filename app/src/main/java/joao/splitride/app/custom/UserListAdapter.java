package joao.splitride.app.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.UsersByCalendars;


public class UserListAdapter extends ArraySwipeAdapter implements View.OnClickListener {

	private List<ParseUser> items;
	private int layoutResourceId;
	private Context context;

	public UserListAdapter(Context context, int resource) {
		super(context, resource);
	}

	public UserListAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public UserListAdapter(Context context, int resource, Object[] objects) {
		super(context, resource, objects);
	}

	public UserListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public UserListAdapter(Context context, int resource, List objects) {
		super(context, resource, objects);
	}

	public UserListAdapter(Context context, int resource, int textViewResourceId, List objects) {
		super(context, resource, textViewResourceId, objects);

		this.layoutResourceId = resource;
		this.context = context;
		this.items = objects;
	}
	
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		UserHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new UserHolder();
		holder.user = items.get(position);
		holder.remove_user = (ImageView) row.findViewById(R.id.delete);
		holder.remove_user.setTag(holder.user);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		holder.remove_user.setOnClickListener(this);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}

	private void setupItem(UserHolder holder) {
		holder.name.setText(""+holder.user.getUsername());
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.delete:
				final ParseUser user = (ParseUser) v.getTag();


				AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
				dialog.setTitle("Delete segment");
				dialog.setMessage("Are you sure you want to delete this user from this calendar?");
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ParseQuery<UsersByCalendars> query = ParseQuery.getQuery("UsersByCalendar");
						query.whereEqualTo("UserID", user.getObjectId());


						items.remove(user);
						notifyDataSetChanged();

						query.findInBackground(new FindCallback<UsersByCalendars>() {
							@Override
							public void done(List<UsersByCalendars> objects, ParseException e) {

								if (e == null) {

									SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

									for (UsersByCalendars object : objects) {
										if (object.getCalendarID().equalsIgnoreCase(sharedPreferences.getString("calendarID", "")))
											object.deleteInBackground();

									}


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

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	public static class UserHolder {
		ParseUser user;
		TextView name;
		ImageView remove_user;
	}	
}

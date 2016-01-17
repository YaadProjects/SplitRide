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
import joao.splitride.app.entities.Segment;


public class SegmentListAdapter extends ArrayAdapter<Segment> {

	private List<Segment> items;
	private int layoutResourceId;
	private Context context;
	
	
	public SegmentListAdapter(Context context, int layoutResourceId, List<Segment> items){
		super(context, layoutResourceId, items);
		
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent){
		
		View row = convertView;
		SegmentHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new SegmentHolder();
		holder.segment = items.get(position);
		holder.edit_segment = (ImageButton)row.findViewById(R.id.edit);
		holder.remove_segment = (ImageButton)row.findViewById(R.id.remove);
		holder.edit_segment.setTag(holder.segment);
		holder.remove_segment.setTag(holder.segment);
		
		holder.name = (TextView)row.findViewById(R.id.line_name);

		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(SegmentHolder holder) {
		holder.name.setText(""+holder.segment.getName());
	}
	
	public static class SegmentHolder{
		Segment segment;
		TextView name;
		ImageButton edit_segment;
		ImageButton remove_segment;
	}	
}

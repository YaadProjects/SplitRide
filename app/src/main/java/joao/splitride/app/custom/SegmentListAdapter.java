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
import joao.splitride.app.entities.Segment;
import joao.splitride.app.settings.AddEditSegment;


public class SegmentListAdapter<T> extends ArraySwipeAdapter implements View.OnClickListener {
    private List<Segment> items;
    private int layoutResourceId;
    private Context context;

    public SegmentListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SegmentListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public SegmentListAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public SegmentListAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SegmentListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public SegmentListAdapter(Context context, int resource, int textViewResourceId, List objects) {
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
        holder.segment = items.get(position);
        holder.edit = (ImageView) row.findViewById(R.id.edit);
        holder.remove = (ImageView) row.findViewById(R.id.delete);
        holder.edit.setTag(holder.segment);
        holder.remove.setTag(holder.segment);

        holder.name = (TextView)row.findViewById(R.id.line_name);

        holder.edit.setOnClickListener(this);
        holder.remove.setOnClickListener(this);

        row.setTag(holder);

        setupItem(holder);

        return row;
    }

    private void setupItem(RouteHolder holder) {
        holder.name.setText(holder.segment.getName());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                final Segment segment = (Segment) v.getTag();

                final Intent intent = new Intent(context, AddEditSegment.class);
                intent.putExtra("id", segment.getObjectId());
                intent.putExtra("name", segment.getName());
                intent.putExtra("distance", segment.getDistance());
                intent.putExtra("cost", segment.getCost());

                ((Activity) context).startActivityForResult(intent, 1);
                break;

            case R.id.delete:
                final Segment segment2 = (Segment) v.getTag();

                AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Delete segment");
                dialog.setMessage("Are you sure you want to delete this segment?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        ParseQuery<Segment> query = ParseQuery.getQuery("Routes");
                        query.whereEqualTo("objectId", segment2.getObjectId());

                        query.findInBackground(new FindCallback<Segment>() {
                            @Override
                            public void done(List<Segment> objects, ParseException e) {
                                if (e == null) {
                                    // object will be your game score
                                    Segment object = objects.get(0);
                                    object.deleteInBackground();

                                    items.remove(segment2);
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
        Segment segment;
        TextView name;
        ImageView edit, remove;
    }
}

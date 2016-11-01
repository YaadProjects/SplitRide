package joao.splitride.app.custom;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Movement;

public class MovementRecyclerAdapter extends RecyclerView
        .Adapter<MovementRecyclerAdapter
        .DataObjectHolder> {
    private List<Movement> mDataset;
    private Context context;

    public MovementRecyclerAdapter(List<Movement> myDataset, Context myContext) {
        mDataset = myDataset;
        context = myContext;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {

        ParseQuery<ParseUser> query_from = ParseUser.getQuery();
        query_from.whereEqualTo("objectId", mDataset.get(position).getFromUserID());

        ParseQuery<ParseUser> query_to = ParseUser.getQuery();
        query_to.whereEqualTo("objectId", mDataset.get(position).getToUserID());

        try {
            ParseUser userFrom = query_from.getFirst();
            ParseUser userTo = query_to.getFirst();

            holder.label2.setText("From: " + userFrom.getUsername() + " To: " + userTo.getUsername());
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        holder.label1.setText("Valor: " + mDataset.get(position).getValue() + "");

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Movement movement = mDataset.get(position);

                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("Delete Trip");
                dialog.setMessage("Are you sure you want to delete this trip?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        movement.deleteInBackground();
                        mDataset.remove(movement);
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
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView label1;
        TextView label2;
        ImageView edit, delete;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label1 = (TextView) itemView.findViewById(R.id.textView);
            label2 = (TextView) itemView.findViewById(R.id.textView2);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            delete = (ImageView) itemView.findViewById(R.id.delete);

        }
    }

}

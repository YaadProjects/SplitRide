package joao.splitride.app.custom;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Movement;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder>
        implements View.OnClickListener {
    private List<Movement> mDataset;
    private Context context;

    public MyRecyclerViewAdapter(List<Movement> myDataset, Context myContext) {
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
        holder.label2.setText("From: " + mDataset.get(position).getFromUserID() + " To: " + mDataset.get(position).getToUserID());
        holder.label1.setText("Valor: " + mDataset.get(position).getValue() + "");
        holder.edit.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit:
                Log.d("cenas", "CARREGUEI NO BOTÃO DE EDIT!!!!");
                break;

/*            case R.id.delete:
                Log.d("cenas",  );
                Log.d("cenas", "CARREGUEI NO BOTÃO DE DELETE!!!!");
                break;*/
        }

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

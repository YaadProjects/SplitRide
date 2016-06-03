package joao.splitride.app.custom;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Movement;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private List<Movement> mDataset;

    public MyRecyclerViewAdapter(List<Movement> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label2.setText("From: " + mDataset.get(position).getFromUserID() + " To: " + mDataset.get(position).getToUserID());
        holder.label1.setText(mDataset.get(position).getValue() + "");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label1;
        TextView label2;
        ImageButton options, edit, delete;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label1 = (TextView) itemView.findViewById(R.id.textView);
            label2 = (TextView) itemView.findViewById(R.id.textView2);
            options = (ImageButton) itemView.findViewById(R.id.cardView_options);
            edit = (ImageButton) itemView.findViewById(R.id.cardView_edit);
            delete = (ImageButton) itemView.findViewById(R.id.cardView_delete);
            options.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.cardView_options:
                    if (edit.getVisibility() == View.INVISIBLE) {
                        edit.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                    } else {
                        edit.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                    }

                    break;

                case R.id.cardView_edit:
                    Log.d("cenas", "CARREGUEI NO BOTÃO DE EDIT!!!!");
                    break;

                case R.id.cardView_delete:
                    Log.d("cenas", "CARREGUEI NO BOTÃO DE DELETE!!!!");
                    break;
            }

        }
    }

}

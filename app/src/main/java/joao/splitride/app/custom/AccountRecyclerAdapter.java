package joao.splitride.app.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import joao.splitride.R;
import joao.splitride.app.entities.Account;

public class AccountRecyclerAdapter extends RecyclerView
        .Adapter<AccountRecyclerAdapter
        .DataObjectHolder> {
    private List<Account> mDataset;
    private Context context;

    public AccountRecyclerAdapter(List<Account> myDataset, Context myContext) {
        mDataset = myDataset;
        context = myContext;
    }

    @Override
    public AccountRecyclerAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        return new AccountRecyclerAdapter.DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountRecyclerAdapter.DataObjectHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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

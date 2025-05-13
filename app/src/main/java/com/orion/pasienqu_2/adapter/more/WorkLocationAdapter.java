package com.orion.pasienqu_2.adapter.more;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.work_location.WorkLocationInputActivity;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WorkLocationAdapter extends RecyclerView.Adapter {
    Context context;
    List<WorkLocationModel> Datas;
    private List<WorkLocationModel> dataFiltered;
    int view;

    public WorkLocationAdapter(Context context, List<WorkLocationModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.view = view;
        this.dataFiltered = new ArrayList<WorkLocationModel>();
        this.dataFiltered.addAll(Datas);
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextView tvName, tvLocation;
        public CardView crdView;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            crdView = (CardView) itemView.findViewById(R.id.crdView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new WorkLocationAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WorkLocationModel data = dataFiltered.get(position);
        final ItemHolder itemHolder = (ItemHolder) holder;
        String uuid = data.getUuid();

        itemHolder.tvName.setText(data.getName());
        itemHolder.tvLocation.setText(data.getLocation());


        itemHolder.crdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkLocationInputActivity.class);
                intent.putExtra("id", data.getId());
                intent.putExtra("uuid", data.getUuid());
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }



    public void addModel(WorkLocationModel Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltered.add(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<WorkLocationModel> Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltered.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltered.size();
        this.dataFiltered.removeAll(dataFiltered);
        notifyItemRangeRemoved(0, LastPosition);
    }


}

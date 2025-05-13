package com.orion.pasienqu_2.activities.satu_sehat.location;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

public class LocationSatuSehatAdapter extends RecyclerView.Adapter {
    Context context;
    List<WorkLocationModel> Datas;
    private List<WorkLocationModel> dataFiltered;
    int view;

    public LocationSatuSehatAdapter(Context context, List<WorkLocationModel> datas, int view) {
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
        public ImageView imgSatuSehat;
        public CardView crdView;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            imgSatuSehat = itemView.findViewById(R.id.imgSatuSehat);
            crdView = itemView.findViewById(R.id.crdView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LocationSatuSehatAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WorkLocationModel data = dataFiltered.get(position);
        final ItemHolder itemHolder = (ItemHolder) holder;

        itemHolder.tvName.setText(data.getName());
        itemHolder.tvLocation.setText(data.getLocation());
        if (TextUtils.isEmpty(data.getOrganization_ihs()) || TextUtils.isEmpty(data.getLocation_ihs()) || TextUtils.isEmpty(data.getClient_id()) || TextUtils.isEmpty(data.getClient_secret())) {
            itemHolder.imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_gray);
        }else{
            itemHolder.imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
        }

        itemHolder.crdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LocationSatuSehatInputActivity.class);
                intent.putExtra("uuid", data.getUuid());
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                ((AppCompatActivity) context).startActivityForResult(intent, 1);
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

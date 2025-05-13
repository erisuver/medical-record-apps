package com.orion.pasienqu_2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class IcdAdapter extends RecyclerView.Adapter {
    Context context;
    List<RecordDiagnosaModel> Datas;
    int view;

    public IcdAdapter(Context context, List<RecordDiagnosaModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.view = view;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView tvCode, tvName;

        public ItemHolder(View itemView) {
            super(itemView);
            tvCode = (TextView) itemView.findViewById(R.id.tvCode);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new IcdAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecordDiagnosaModel data = Datas.get(position);
        final IcdAdapter.ItemHolder itemHolder = (IcdAdapter.ItemHolder) holder;
        String uuid = data.getUuid();

        itemHolder.tvCode.setText("");
        itemHolder.tvName.setText(data.getIcd_name());

    }


    public void addModel(RecordDiagnosaModel Datas) {
        int pos = this.Datas.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
    }

    public void addModels(List<RecordDiagnosaModel> Datas) {
        int pos = this.Datas.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    public void getListIcd(List<RecordDiagnosaModel> datas){
        Datas = datas;
    }
}

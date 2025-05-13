package com.orion.pasienqu_2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LovIcdAdapter extends RecyclerView.Adapter {
    Context context;
    List<RecordDiagnosaModel> Datas;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private String uid;
    private IcdAdapter icdAdapter;

    public LovIcdAdapter(Context context, List<RecordDiagnosaModel> datas, int view, Dialog dialog, TextInputEditText txtTemp, String uid, IcdAdapter icdAdapter) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = txtTemp;
        this.uid = uid;
        Datas = datas;
        this.view = view;
        this.icdAdapter = icdAdapter;

    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {

        public TextView tvName;
        public Button btnDelete;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovIcdAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecordDiagnosaModel data = Datas.get(position);
        final LovIcdAdapter.ItemHolder itemHolder = (LovIcdAdapter.ItemHolder) holder;
        itemHolder.tvName.setText(data.getIcd_name());
        this.uid = data.getIcd_name();

        itemHolder.btnDelete.setOnClickListener(view1 -> {
            LovIcdAdapter.this.removeModel(position);
            icdAdapter.removeModel(position);
        });

        icdAdapter.removeAllModel();
        String icdCode = "", icdName = "";
        for (int i = 0; i < Datas.size(); i++) {
            icdCode = Datas.get(i).getIcd_code();
            icdName = Datas.get(i).getIcd_name();
            RecordDiagnosaModel model = new RecordDiagnosaModel();
            model.setIcd_code(icdCode);
            model.setIcd_name(icdName);
            icdAdapter.addModel(model);
            icdAdapter.notifyDataSetChanged();
        }

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

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        notifyItemRangeRemoved(0, LastPosition);
    }

//    public void removeModel(int idx) {
//        if (Datas.size() > 0){
//            this.Datas.remove(idx);
//            notifyItemRemoved(idx);
//        }
//    }

    public void removeModel(int idx) {
        if (Datas.size() > 0 && Datas.size() > idx){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
            notifyDataSetChanged();
        }
    }
}

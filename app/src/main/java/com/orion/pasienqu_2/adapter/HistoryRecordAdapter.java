package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.RecordModel;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryRecordAdapter extends RecyclerView.Adapter {
    Context context;
    List<RecordModel> Datas;
    private List<RecordModel> dataFiltererd;
    private int view;
    private int lastpos;

    public HistoryRecordAdapter(Context context, List<RecordModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.dataFiltererd = new ArrayList<RecordModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvDate;
        public ExpandableLayout expandableHistory;
        public TextView txtAnamnesa, txtPhysicalExam, txtDiagnosa, txtTheraphy;
        public CardView crdView;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName =  itemView.findViewById(R.id.tvName);
            expandableHistory = itemView.findViewById(R.id.expandableHistory);
            tvDate = itemView.findViewById(R.id.tvExTitle);
            txtAnamnesa = itemView.findViewById(R.id.txtAnamnesa);
            txtPhysicalExam = itemView.findViewById(R.id.txtPhysicalExam);
            txtDiagnosa = itemView.findViewById(R.id.txtDiagnosa);
            txtTheraphy = itemView.findViewById(R.id.txtTheraphy);
            crdView = itemView.findViewById(R.id.crdView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new HistoryRecordAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecordModel data = dataFiltererd.get(position);
        final HistoryRecordAdapter.ItemHolder itemHolder = (HistoryRecordAdapter.ItemHolder) holder;
        final long id = data.getId();
        final String uuid = data.getUuid();
        itemHolder.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        itemHolder.tvDate.setText(Global.getDateFormated(data.getRecord_date(), "dd-MM-yyyy"));
        itemHolder.txtAnamnesa.setText(data.getAnamnesa());
        itemHolder.txtDiagnosa.setText(data.getDiagnosa());
        itemHolder.txtTheraphy.setText(data.getTherapy());

        String PhysicalExam = "";
        if (data.getWeight() != 0){
            PhysicalExam = PhysicalExam + context.getString(R.string.weight)+" : " +String.format("%.2f", data.getWeight())+" kg \n";
        }

        if (data.getTemperature() != 0){
            PhysicalExam = PhysicalExam + context.getString(R.string.temperature)+" : " +String.format("%.2f", data.getTemperature())+" °C \n";
        }

        if (data.getBlood_pressure_systolic() != 0){
            PhysicalExam = PhysicalExam + "T : " +data.getBlood_pressure_systolic()+"/"+data.getBlood_pressure_diastolic()+ " mm/Hg \n";
        }
        PhysicalExam = PhysicalExam + data.getPhysical_exam();
        itemHolder.txtPhysicalExam.setText(PhysicalExam);

//        itemHolder.expandableHistory.collapse();

        itemHolder.expandableHistory.setOnClickListener(view1 -> {
            itemHolder.expandableHistory.toggleLayout();
            if(itemHolder.expandableHistory.isExpanded()){
                itemHolder.tvDate.setTextColor(Color.parseColor("#FF000000"));
            }else{
                itemHolder.tvDate.setTextColor(Color.parseColor("#FF005EB8"));
            }

        });

        itemHolder.crdView.setOnClickListener(view1 -> {
            Intent s = new Intent(context, RecordInputActivity.class);
            s.putExtra("uuid", data.getUuid());
            s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            ((Activity) context).startActivityForResult(s, 100);
        });
//        if (position == getItemCount()-1) {
//            itemHolder.expandableHistory.setPadding(0, 0, 0, 200);
//        }

    }

    public void addModel(RecordModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<RecordModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void removeAllModel() {
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }

}
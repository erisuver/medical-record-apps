package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class RecordAdapter  extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<RecordModel> Datas;
    private List<RecordModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    int REQUEST_CODE_LOAD = 333;

    //untuk keperluan filter
    TextView tvTotalRecord, tvTotalBilling, tvInform;

    public RecordAdapter(Context context, List<RecordModel> Datas, int view, TextView tvTotalRecord, TextView tvTotalBilling, TextView tvInform) {
        this.context = context;
        this.Datas = Datas;
        this.dataFiltererd = new ArrayList<RecordModel>();
        this.dataFiltererd.addAll(Datas);
        this.Loading = new ProgressDialog(context);
        this.view = view;

        //untuk keperluan filter
        this.tvTotalRecord = tvTotalRecord;
        this.tvTotalBilling = tvTotalBilling;
        this.tvInform = tvInform;

    }

    public void addModels(List<RecordModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(RecordModel data, int pos) {
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModel(RecordModel data) {
        this.Datas.add(data);
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
        this.dataFiltererd.add(data);
        notifyItemRangeInserted(dataFiltererd.size()-1, Datas.size()-1);

        this.Datas.get(9).setPatient_id_kode(data.getPatient_id_kode());

    }

    public void changeModel(RecordModel data, int idx) {
        this.Datas.add(data);
        this.Datas.get(idx).setPatient_id_kode(data.getPatient_name());
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
        this.dataFiltererd.add(data);
        notifyItemRangeInserted(dataFiltererd.size()-1, Datas.size()-1);
    }

    public void removeModel(int idx) {
//        if (Datas.size() > 0){
//            this.Datas.remove(idx);
//            notifyItemRemoved(idx);
//        }
//        if (dataFiltererd.size() > 0){
//            this.dataFiltererd.remove(idx);
//            notifyItemRemoved(idx);
//        }
        if (Datas.size() > 0 && Datas.size() > idx){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
        }
        if (dataFiltererd.size() > 0 && dataFiltererd.size() > idx){
            this.dataFiltererd.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new RecordAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new RecordAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvDate, tvName, tvpatientType, tvBillingTotal;
        public ItemHolder(View itemView) {
            super(itemView);
            tvDate   = itemView.findViewById(R.id.tvDate);
            tvName  = itemView.findViewById(R.id.tvName);
            tvpatientType  = itemView.findViewById(R.id.tvpatientType);
            tvBillingTotal  = itemView.findViewById(R.id.tvBillingtotal);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof RecordAdapter.ItemHolder){
            final RecordModel mCurrentItem = dataFiltererd.get(position);
            final RecordAdapter.ItemHolder itemHolder = (RecordAdapter.ItemHolder) holder;

//            PatientTable patientTable = new PatientTable(context);
//            String nameId = patientTable.getNameIdByRecord(mCurrentItem.getPatient_id());
//            mCurrentItem.setName(nameId);
//            itemHolder.tvName.setText(mCurrentItem.getName());

            itemHolder.tvName.setText(mCurrentItem.getPatient_name());
            itemHolder.tvDate.setText(Global.getDateFormated(mCurrentItem.getRecord_date()));
            itemHolder.tvpatientType.setText(mCurrentItem.getpatientTypeStr(context));
            itemHolder.tvBillingTotal.setText("Rp. "+Global.FloatToStrFmt(mCurrentItem.getTotal_billing()));

            itemHolder.tvBillingTotal.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            itemHolder.tvBillingTotal.setSelected(true);

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, RecordInputActivity.class);
                        s.putExtra("uuid", mCurrentItem.getUuid());
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, REQUEST_CODE_LOAD);
                        JApplication.getInstance().lastIdxList=position;
                    }
                }
            });




        }else if (holder instanceof RecordAdapter.LoadingViewHolder){
            RecordAdapter.LoadingViewHolder loadingViewHolder = (RecordAdapter.LoadingViewHolder)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataFiltererd.get(position) == null ? VIEW_TYVE_LOADING : VIEW_TYVE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataFiltererd.size();
    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
//                List<RecordModel> listRecord = JApplication.getInstance().recordTable.getRecords();
                if (charString.isEmpty()) {
                    dataFiltererd.removeAll(dataFiltererd);
                    dataFiltererd.addAll(Datas);
//                    dataFiltererd.addAll(Datas);
                } else {
                    List<RecordModel> filteredList = new ArrayList<RecordModel>();

                    for (RecordModel row : Datas) {
//                    for (RecordModel row : listRecord) {
                        if (row.getPatient_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    dataFiltererd = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFiltererd;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFiltererd = (List<RecordModel>) filterResults.values;
                notifyDataSetChanged();


                double totalBilling = 0;
                for (int i = 0; i < getItemCount(); i++) {
                    totalBilling += dataFiltererd.get(i).getTotal_billing();
                }
                tvTotalBilling.setText(context.getString(R.string.billing_total) +" : Rp. "+Global.FloatToStrFmt(totalBilling));
                tvTotalRecord.setText(context.getString(R.string.medical_records) + " : "+dataFiltererd.size());

                if (getItemCount() == 0) {
                    tvInform.setVisibility(View.VISIBLE);
                    tvInform.setText(context.getString(R.string.no_data_filter));
                }
                else {
                    tvInform.setVisibility(View.GONE);
                }
            }
        };
    }
}

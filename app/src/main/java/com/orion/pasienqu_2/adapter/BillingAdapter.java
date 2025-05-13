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
import com.orion.pasienqu_2.activities.billing.BillingInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class BillingAdapter extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<BillingModel> Datas;
    private List<BillingModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    private  TextView txtBillingtotal;
    private boolean isMedicalRecordEdit = false;
    public boolean isClickable = true;
    public boolean isDetail = false;


    public BillingAdapter(Context context, List<BillingModel> Datas, int view, TextView txtBillingtotal, boolean isMedicalRecordEdit) {
        this.context = context;
        this.Datas = Datas;
        this.dataFiltererd = new ArrayList<BillingModel>();
        this.dataFiltererd.addAll(Datas);
        this.Loading = new ProgressDialog(context);
        this.view = view;
        this.txtBillingtotal = txtBillingtotal;
        this.isMedicalRecordEdit = isMedicalRecordEdit;
    }

    public void addModels(List<BillingModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(BillingModel data, int pos) {
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModel(BillingModel data) {
        this.Datas.add(data);
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
        this.dataFiltererd.add(data);
        notifyItemRangeInserted(dataFiltererd.size()-1, Datas.size()-1);
    }

    public void removeModel(int idx) {
        if (Datas.size() > 0){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
        }
        if (dataFiltererd.size() > 0){
            this.dataFiltererd.remove(idx);
            notifyItemRemoved(idx);
        }
//        if (Datas.size() > 0 && Datas.size() > idx){
//            this.Datas.remove(idx);
//            notifyItemRemoved(idx);
//        }
//        if (dataFiltererd.size() > 0 && dataFiltererd.size() > idx){
//            this.dataFiltererd.remove(idx);
//            notifyItemRemoved(idx);
//        }
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
            return new BillingAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new BillingAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvDate, tvName, tvAmount;
        public ItemHolder(View itemView) {
            super(itemView);
            tvDate   = itemView.findViewById(R.id.tvDate);
            tvName  = itemView.findViewById(R.id.tvName);
            tvAmount  = itemView.findViewById(R.id.tvAmount);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BillingAdapter.ItemHolder){
            final BillingModel mCurrentItem = dataFiltererd.get(position);
            final BillingAdapter.ItemHolder itemHolder = (BillingAdapter.ItemHolder) holder;

            itemHolder.tvDate.setText(Global.getDateFormated(mCurrentItem.getBilling_date()));
            itemHolder.tvName.setText(mCurrentItem.getName());
            itemHolder.tvAmount.setText(Global.FloatToStrFmt(mCurrentItem.getTotal_amount(), true));

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isClickable){
                        return;
                    }
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, BillingInputActivity.class);
                        s.putExtra("uuid", mCurrentItem.getUuid());
                        s.putExtra("record_id", mCurrentItem.getMedical_record_id());
                        s.putExtra("medical_record_edit", isMedicalRecordEdit);
                        s.putExtra("mode_detail", isDetail);
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, 2);
                    }
                }
            });


        }else if (holder instanceof BillingAdapter.LoadingViewHolder){
            BillingAdapter.LoadingViewHolder loadingViewHolder = (BillingAdapter.LoadingViewHolder)holder;
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
                if (charString.isEmpty()) {
                    dataFiltererd.removeAll(dataFiltererd);
                    dataFiltererd.addAll(Datas);
                } else {
                    List<BillingModel> filteredList = new ArrayList<BillingModel>();

                    for (BillingModel row : Datas) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                dataFiltererd = (List<BillingModel>) filterResults.values;
                notifyDataSetChanged();
                //filtering total
                double totalBilling = 0;
                for (int i = 0; i < getItemCount(); i++) {
                    totalBilling += dataFiltererd.get(i).getTotal_amount();
                }
                txtBillingtotal.setText("Total: "+Global.FloatToStrFmt(totalBilling));
            }
        };
    }


}
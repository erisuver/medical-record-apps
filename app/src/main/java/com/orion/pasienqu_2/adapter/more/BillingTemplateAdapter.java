package com.orion.pasienqu_2.adapter.more;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.billing_template.BillingTemplateInputActivity;
import com.orion.pasienqu_2.models.BillingTemplateModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BillingTemplateAdapter extends RecyclerView.Adapter  {
    Context context;
    List<BillingTemplateModel> Datas;
    private List<BillingTemplateModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;

    public BillingTemplateAdapter(Context context, List<BillingTemplateModel> datas, int view) {
        this.context = context;
        Datas = datas;
        this.dataFiltererd = new ArrayList<BillingTemplateModel>();
        this.dataFiltererd.addAll(Datas);
        Loading = new ProgressDialog(context);
        this.view = view;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new BillingTemplateAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new BillingTemplateAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        public ItemHolder(View itemView) {
            super(itemView);
            txtName   = itemView.findViewById(R.id.txtName);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BillingTemplateAdapter.ItemHolder){
            final BillingTemplateModel mCurrentItem = dataFiltererd.get(position);
            final BillingTemplateAdapter.ItemHolder itemHolder = (BillingTemplateAdapter.ItemHolder) holder;
            itemHolder.txtName.setText(mCurrentItem.getName());

            if (mCurrentItem.getId() != 0) {
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent s = new Intent(context, BillingTemplateInputActivity.class);
                        s.putExtra("uuid", mCurrentItem.getUuid());
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, 1);
                    }
                });
            }
        }else if (holder instanceof BillingTemplateAdapter.LoadingViewHolder){
            BillingTemplateAdapter.LoadingViewHolder loadingViewHolder = (BillingTemplateAdapter.LoadingViewHolder)holder;
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

    public void addModels(List<BillingTemplateModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(BillingTemplateModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
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
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }


}

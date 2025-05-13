package com.orion.pasienqu_2.adapter.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.subaccount.SubaccountInputActivity;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.SubaccountTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.SubaccountModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SubaccountAdapter  extends RecyclerView.Adapter  {
    Context context;
    List<SubaccountModel> Datas;
    private List<SubaccountModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    private Runnable runnableArchived;

    public SubaccountAdapter(Context context, List<SubaccountModel> datas, int view, Runnable runnableArchived) {
        this.context = context;
        Datas = datas;
        this.dataFiltererd = new ArrayList<SubaccountModel>();
        this.dataFiltererd.addAll(Datas);
        Loading = new ProgressDialog(context);
        this.view = view;
        this.runnableArchived = runnableArchived;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new SubaccountAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new SubaccountAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtEmail;
        private ImageButton btnAccount;
        public ItemHolder(View itemView) {
            super(itemView);
            txtName   = itemView.findViewById(R.id.tvName);
            txtEmail   =  itemView.findViewById(R.id.tvEmail);
            btnAccount = itemView.findViewById(R.id.btnAccount);
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
        if (holder instanceof SubaccountAdapter.ItemHolder){
            final SubaccountModel mCurrentItem = dataFiltererd.get(position);
            final SubaccountAdapter.ItemHolder itemHolder = (SubaccountAdapter.ItemHolder) holder;
            final String uuid = mCurrentItem.getUuid();

            GlobalTable globalTable = ((JApplication) context.getApplicationContext()).globalTable;
            boolean isArchive = globalTable.isArchived("pasienqu_subaccount", mCurrentItem.getUuid());
            //init
            itemHolder.txtName.setText(mCurrentItem.getName());
            itemHolder.txtEmail.setText(mCurrentItem.getLogin());
            if(isArchive){
                itemHolder.btnAccount.setColorFilter(Color.parseColor("#ff0000"));
            }else{
                itemHolder.btnAccount.setColorFilter(Color.parseColor("#4DD153"));
            }

            //event
            itemHolder.btnAccount.setOnClickListener(view1 -> {
                if(isArchive){
                    Runnable activate = new Runnable() {
                        @Override
                        public void run() {
                            globalTable.unarchive("pasienqu_subaccount", uuid, "res.users");
                            itemHolder.btnAccount.setColorFilter(Color.parseColor("#ff0000"));
                            runnableArchived.run();
                        }
                    };
                    ShowDialog.confirmDialog(((Activity)context), context.getString(R.string.activate), context.getString(R.string.unarchive_sub_acc), activate);

                }else{
                    Runnable nonactivate = new Runnable() {
                        @Override
                        public void run() {
                            globalTable.archive("pasienqu_subaccount", uuid, "res.users");
                            itemHolder.btnAccount.setColorFilter(Color.parseColor("#4DD153"));
                            runnableArchived.run();
                        }
                    };
                    ShowDialog.confirmDialog(((Activity)context), context.getString(R.string.nonactivate), context.getString(R.string.archive_sub_acc), nonactivate);

                }


            });

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent s = new Intent(context, SubaccountInputActivity.class);
                    s.putExtra("uuid", mCurrentItem.getUuid());
                    ((Activity) context).startActivityForResult(s, 1);
                }
            });
        }else if (holder instanceof SubaccountAdapter.LoadingViewHolder){
            SubaccountAdapter.LoadingViewHolder loadingViewHolder = (SubaccountAdapter.LoadingViewHolder)holder;
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

    public void addModels(List<SubaccountModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(SubaccountModel data, int pos) {
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
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

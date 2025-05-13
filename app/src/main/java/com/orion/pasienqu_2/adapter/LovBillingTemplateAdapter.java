package com.orion.pasienqu_2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingTemplateModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LovBillingTemplateAdapter extends RecyclerView.Adapter {
    Context context;
    List<BillingTemplateModel> Datas;
    private List<BillingTemplateModel> dataFiltererd;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private String uid;
    private BillingInputAdapter billingInputAdapter;

    public LovBillingTemplateAdapter(Context context, List<BillingTemplateModel> datas, int view, Dialog dialog, BillingInputAdapter billingInputAdapter, String uid) {
        this.context = context;
        this.dialog = dialog ;
        this.billingInputAdapter = billingInputAdapter;
        this.uid = uid;
        Datas = datas;
        this.dataFiltererd = new ArrayList<BillingTemplateModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }


    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {

        public TextView txtLabel;

        public ItemHolder(View itemView) {
            super(itemView);
            txtLabel = (TextView) itemView.findViewById(R.id.txtLabel);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovBillingTemplateAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BillingTemplateModel data = dataFiltererd.get(position);
        final LovBillingTemplateAdapter.ItemHolder itemHolder = (LovBillingTemplateAdapter.ItemHolder) holder;
        final long id = data.getId();
        final String name = data.getName();
        final String uuid = data.getUuid();
        itemHolder.txtLabel.setText(name);

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingInputAdapter.removeAllModel();
                billingInputAdapter.isReqFocus = false;
                String items = data.getItems();
                String item1 = items.replaceAll("'", "").replace("[", "").replace("]", "");
                String[] itemStringlist = item1.split(",");
                for (int i = 0; i < itemStringlist.length; i++){
                    String item2 = itemStringlist[i];
                    BillingItemModel model = new BillingItemModel();
                    model.setName(item2);
                    billingInputAdapter.addModel(model);
                    billingInputAdapter.notifyDataSetChanged();
                }
                LovBillingTemplateAdapter.this.uid = uuid;
                dialog.dismiss();
            }
        });
    }

    public void addModel(BillingTemplateModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<BillingTemplateModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltererd.size();
        this.dataFiltererd.removeAll(dataFiltererd);
        notifyItemRangeRemoved(0, LastPosition);
    }
}

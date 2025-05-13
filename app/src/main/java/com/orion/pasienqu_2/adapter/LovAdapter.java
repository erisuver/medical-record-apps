package com.orion.pasienqu_2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.List;

public class LovAdapter extends RecyclerView.Adapter {
    Context context;
    List<LovModel> Datas;
    private List<LovModel> dataFiltererd;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private String value;
    private String sortKey;

    public LovAdapter(Context context, List<LovModel> datas, int view, Dialog dialog, String value, String sortKey) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = txtTemp;
        this.value = value;
        Datas = datas;
        this.dataFiltererd = new ArrayList<LovModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;
        this.sortKey = sortKey;
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {

        public TextView txtLabel;
        public ImageView imgChecked;

        public ItemHolder(View itemView) {
            super(itemView);
            txtLabel = (TextView) itemView.findViewById(R.id.txtLabel);
            imgChecked = (ImageView) itemView.findViewById(R.id.imgChecked);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LovModel data = dataFiltererd.get(position);
        final LovAdapter.ItemHolder itemHolder = (LovAdapter.ItemHolder) holder;
        final String dataValue = data.getValue();
        final String label = data.getLabel();
        itemHolder.txtLabel.setText(label);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LovAdapter.this.value = value;
//                JApplication.getInstance().SelectedLov = value;
                SharedPrefsUtils.setStringPreference(context,sortKey,dataValue);
                dialog.dismiss();
            }
        });

        //munculin penanda sort yg sedang aktif (checked)
//        SharedPreferences settingPref = context.getSharedPreferences("setting_pref", Context.MODE_PRIVATE);
        String valueSort = SharedPrefsUtils.getStringPreference(context, sortKey);
        if (valueSort.equals(dataValue)) {
            itemHolder.imgChecked.setVisibility(View.VISIBLE);
        }else{
            itemHolder.imgChecked.setVisibility(View.INVISIBLE);
        }


    }

    public void addModel(LovModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<LovModel> Datas) {
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

    public void setPutExtra(long id, String uuid, String name, int category, String template){
//        Intent intent = context.getIntent();
//        intent.putExtra("id", id);
//        intent.putExtra("uuid", uuid);
//        intent.putExtra("name", name);
//        setResult(RESULT_OK, intent);
//        finish();
    }
}

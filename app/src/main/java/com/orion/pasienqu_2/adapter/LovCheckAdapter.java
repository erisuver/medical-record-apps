package com.orion.pasienqu_2.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.models.LovCheckModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LovCheckAdapter extends RecyclerView.Adapter {
    Context context;
    List<LovCheckModel> Datas;
    private List<LovCheckModel> dataFiltererd;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private String uid;

    public LovCheckAdapter(Context context, List<LovCheckModel> datas, int view) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = txtTemp;
        this.uid = uid;
        Datas = datas;
        this.dataFiltererd = new ArrayList<LovCheckModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {

        public TextView txtLabel;
        public CheckBox chbCheck;

        public ItemHolder(View itemView) {
            super(itemView);
            txtLabel = (TextView) itemView.findViewById(R.id.txtLabel);
            chbCheck = (CheckBox) itemView.findViewById(R.id.chbCheck);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovCheckAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LovCheckModel data = dataFiltererd.get(position);
        final LovCheckAdapter.ItemHolder itemHolder = (LovCheckAdapter.ItemHolder) holder;
        final String label = data.getLabel();
        final boolean state = data.isState();

        itemHolder.txtLabel.setText(label);
        itemHolder.chbCheck.setChecked(state);

        itemHolder.itemView.setOnClickListener(v -> {
            if (!itemHolder.chbCheck.isChecked()) {
                data.setState(true);
                itemHolder.chbCheck.setChecked(true);
            }else {
                data.setState(false);
                itemHolder.chbCheck.setChecked(false);

            }
            if (position == getItemCount()-1 ) {
                if (itemHolder.chbCheck.isChecked()) {
                    customReminder(itemHolder.txtLabel, itemHolder.chbCheck, data, v);
                }else{
                    data.setState(false);
                    itemHolder.chbCheck.setChecked(false);

                    data.setValue("");
                    data.setLabel(context.getString(R.string.ReminderCustom));
                    itemHolder.txtLabel.setText(data.getLabel());
                }
            }
        });

        itemHolder.chbCheck.setOnClickListener(view1 -> {
            data.setState(!state);

            if (position == getItemCount()-1 && itemHolder.chbCheck.isChecked()){
                customReminder(itemHolder.txtLabel, itemHolder.chbCheck, data, view1);
            }
        });
    }

    public void addModel(LovCheckModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<LovCheckModel> Datas) {
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

    private void customReminder (TextView txtLabel, CheckBox chbCheck, LovCheckModel data, View v){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String customValue = txtLabel.getText().toString();
                String newValue = String.valueOf(Global.getMillisDateTime(customValue));

                if (customValue.equals("")){
                    data.setState(false);
                    chbCheck.setChecked(false);
                }else {
                    data.setState(true);
                    chbCheck.setChecked(true);
                }

                data.setValue(newValue);
                data.setLabel(context.getString(R.string.ReminderCustom)+" "+customValue);
                txtLabel.setText(data.getLabel());
            }
        };
        Global.dtpTimeClickTextview(((Activity) context), txtLabel , v, runnable);
        if (!chbCheck.isChecked()) {
            data.setState(true);
            chbCheck.setChecked(true);
        }else {
            data.setState(false);
            chbCheck.setChecked(false);
        }
    }
}

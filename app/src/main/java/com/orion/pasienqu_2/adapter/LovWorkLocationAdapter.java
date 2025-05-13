package com.orion.pasienqu_2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.WorkLocationModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.List;

public class LovWorkLocationAdapter extends RecyclerView.Adapter {
    Context context;
    List<WorkLocationModel> Datas;
    private List<WorkLocationModel> dataFiltererd;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private TextView txtTempTextView;
    private String uid;

    public LovWorkLocationAdapter(Context context, List<WorkLocationModel> datas, int view, Dialog dialog, TextInputEditText txtTemp, String uid) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = txtTemp;
        this.txtTempTextView = null;
        this.uid = uid;
        Datas = datas;
        this.dataFiltererd = new ArrayList<WorkLocationModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }

    public LovWorkLocationAdapter(Context context, List<WorkLocationModel> datas, int view, Dialog dialog, TextView txtTemp, String uid) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = null;
        this.txtTempTextView = txtTemp;
        this.uid = uid;
        Datas = datas;
        this.dataFiltererd = new ArrayList<WorkLocationModel>();
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
        return new LovWorkLocationAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WorkLocationModel data = dataFiltererd.get(position);
        final LovWorkLocationAdapter.ItemHolder itemHolder = (LovWorkLocationAdapter.ItemHolder) holder;
        final long id = data.getId();
        final String name = data.getName();
        final String uuid = data.getUuid();
        itemHolder.txtLabel.setText(data.getName());

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtTemp == null) {
                    txtTempTextView.setText(name);
                }else{
                    txtTemp.setText(name);
                }
                LovWorkLocationAdapter.this.uid = uuid;
                JApplication.getInstance().SelectedLov = uuid;
                dialog.dismiss();
            }
        });
    }

    public void addModel(WorkLocationModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<WorkLocationModel> Datas) {
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

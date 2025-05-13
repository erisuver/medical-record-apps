package com.orion.pasienqu_2.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LovTemplateAdapter extends RecyclerView.Adapter {
    Context context;
    List<NoteTemplateModel> Datas;
    private List<NoteTemplateModel> dataFiltererd;
    private int view;
    private Dialog dialog;
    private TextInputEditText txtTemp;
    private String uid;

    public LovTemplateAdapter(Context context, List<NoteTemplateModel> datas, int view, Dialog dialog, TextInputEditText txtTemp, String uid) {
        this.context = context;
        this.dialog = dialog ;
        this.txtTemp = txtTemp;
        this.uid = uid;
        Datas = datas;
        this.dataFiltererd = new ArrayList<NoteTemplateModel>();
        this.dataFiltererd.addAll(Datas);
        this.view = view;

    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {

        public TextView tvName;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovTemplateAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NoteTemplateModel data = dataFiltererd.get(position);
        final LovTemplateAdapter.ItemHolder itemHolder = (LovTemplateAdapter.ItemHolder) holder;
        final long id = data.getId();
        final String name = data.getName();
        final String uuid = data.getUuid();
        final String category = data.getCategory();
        final String template = data.getTemplate();

        itemHolder.tvName.setText(data.getName());

//        if(position %2 == 1)
        if (category.equals("") && id != 0)
        {
            // Set a background color for ListView regular row/item
            itemHolder.itemView.setBackgroundColor(Color.WHITE);
            itemHolder.tvName.setTypeface(itemHolder.tvName.getTypeface(), Typeface.NORMAL);
            itemHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else if (!category.equals("") && id == 0)
        {
            // Set the background color for alternate row/item
            itemHolder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0"));
            itemHolder.tvName.setTypeface(itemHolder.tvName.getTypeface(), Typeface.BOLD);
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id == 0){
                    return;
                }else {
                    txtTemp.setText(template);
                    txtTemp.requestFocus();
                    txtTemp.setSelection(template.length());
                    LovTemplateAdapter.this.uid = uuid;
                    dialog.dismiss();
                }
            }
        });



    }

    public void addModel(NoteTemplateModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModels(List<NoteTemplateModel> Datas) {
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

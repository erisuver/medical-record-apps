package com.orion.pasienqu_2.adapter.more;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.login.SignUpActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateInputActivity;
import com.orion.pasienqu_2.models.MoreModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class NoteTemplateAdapter extends RecyclerView.Adapter {
    Context context;
    List<NoteTemplateModel> Datas;
    private List<NoteTemplateModel> dataFiltererd;
    private int view;

    public NoteTemplateAdapter(Context context, List<NoteTemplateModel> datas, int view) {
        this.context = context;
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
        return new NoteTemplateAdapter.ItemHolder(row);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NoteTemplateModel data = dataFiltererd.get(position);
        final NoteTemplateAdapter.ItemHolder itemHolder = (NoteTemplateAdapter.ItemHolder) holder;
        final int id = data.getId();
        final String uuid = data.getUuid();

        itemHolder.tvName.setText(data.getName());

        itemHolder.tvName.setTypeface(itemHolder.tvName.getTypeface(), Typeface.NORMAL);

        if (uuid.equals("")){
            itemHolder.tvName.setBackgroundColor(Color.parseColor("#E0E0E0"));
            itemHolder.tvName.setTypeface(itemHolder.tvName.getTypeface(), Typeface.BOLD);
        }else{
            itemHolder.tvName.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if (id != 0) {
                itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, NoteTemplateInputActivity.class);
                        intent.putExtra("uuid", uuid);
                        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                });
            }
        }

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



}

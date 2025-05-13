package com.orion.pasienqu_2.activities.patient;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.models.LovPatientSatuSehatModel;

import java.util.ArrayList;
import java.util.List;

public class LovPatientSatuSehatAdapter extends RecyclerView.Adapter {
    Context context;
    LovPatientSatuSehatActivity lovPatientSatuSehatActivity;
    List<LovPatientSatuSehatModel> Datas;
    private List<LovPatientSatuSehatModel> dataFiltered;
    int view;
    // Variabel untuk menyimpan posisi item yang dipilih
    public int selectedPosition = RecyclerView.NO_POSITION;

    public LovPatientSatuSehatAdapter(LovPatientSatuSehatActivity lovPatientSatuSehatActivity, List<LovPatientSatuSehatModel> datas, int view) {
        this.context = lovPatientSatuSehatActivity.getApplicationContext();
        this.lovPatientSatuSehatActivity = lovPatientSatuSehatActivity;
        this.Datas = datas;
        this.view = view;
        this.dataFiltered = new ArrayList<LovPatientSatuSehatModel>();
        this.dataFiltered.addAll(Datas);
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextView tvName, tvBirthDate, tvCity;
        public CheckBox chbPilih;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBirthDate = itemView.findViewById(R.id.tvDateBirth);
            tvCity = itemView.findViewById(R.id.tvCity);
            chbPilih = itemView.findViewById(R.id.chbPilih);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new LovPatientSatuSehatAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LovPatientSatuSehatModel mCurrentItem = dataFiltered.get(position);
        final ItemHolder itemHolder = (ItemHolder) holder;

        itemHolder.tvName.setText(mCurrentItem.getName());
        itemHolder.tvBirthDate.setText(mCurrentItem.getBirth_date());
        itemHolder.tvCity.setText(mCurrentItem.getCity());

        /*//initial
        itemHolder.chbPilih.setEnabled(false);

        itemHolder.chbPilih.setChecked(mCurrentItem.isSelected());
        itemHolder.chbPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();
                mCurrentItem.setSelected(isChecked);
            }
        });*/

        // Tambahkan efek UI untuk item yang dipilih
        if (selectedPosition == position) {
            itemHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_300));
        } else {
            itemHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Tambahkan click listener pada item
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update posisi item yang dipilih
                selectedPosition = position;

                // Update tampilan item
                notifyDataSetChanged();
            }
        });

    }

    public void addModel(LovPatientSatuSehatModel Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltered.add(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<LovPatientSatuSehatModel> Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltered.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void removeAllModel(){
        int LastPosition = Datas.size();
        this.Datas.removeAll(Datas);
        LastPosition = dataFiltered.size();
        this.dataFiltered.removeAll(dataFiltered);
        notifyItemRangeRemoved(0, LastPosition);
    }


}

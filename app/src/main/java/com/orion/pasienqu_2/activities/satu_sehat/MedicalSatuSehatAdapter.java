package com.orion.pasienqu_2.activities.satu_sehat;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.activities.satu_sehat.location.LocationSatuSehatInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;

import java.util.ArrayList;
import java.util.List;

public class MedicalSatuSehatAdapter extends RecyclerView.Adapter {
    Context context;
    MedicalSatuSehatActivity medicalSatuSehatActivity;
    List<MedicalSatuSehatModel> Datas;
    private List<MedicalSatuSehatModel> dataFiltered;
    int view;
    int REQUEST_CODE_PATIENT = 1;

    public MedicalSatuSehatAdapter(MedicalSatuSehatActivity medicalSatuSehatActivity, List<MedicalSatuSehatModel> datas, int view) {
        this.context = medicalSatuSehatActivity.getApplicationContext();
        this.medicalSatuSehatActivity = medicalSatuSehatActivity;
        this.Datas = datas;
        this.view = view;
        this.dataFiltered = new ArrayList<MedicalSatuSehatModel>();
        this.dataFiltered.addAll(Datas);
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder  {
        public TextView tvDate, tvPatient, tvLocation, tvNIK;
        public CardView crdView;
        public ImageView imgSatuSehat, imgPatient;
        public CheckBox chbPilih;

        public ItemHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPatient = itemView.findViewById(R.id.tvPatient);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvNIK = itemView.findViewById(R.id.tvNIK);
            crdView = itemView.findViewById(R.id.crdView);
            imgSatuSehat = itemView.findViewById(R.id.imgSatuSehat);
            imgPatient = itemView.findViewById(R.id.imgPatient);
            chbPilih = itemView.findViewById(R.id.chbPilih);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(view, parent, false);
        return new MedicalSatuSehatAdapter.ItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MedicalSatuSehatModel mCurrentItem = dataFiltered.get(position);
        final ItemHolder itemHolder = (ItemHolder) holder;

        itemHolder.tvDate.setText(Global.getDateFormated(mCurrentItem.getRecord_date()));
        itemHolder.tvPatient.setText(mCurrentItem.getPatient_codename());
        itemHolder.tvLocation.setText(mCurrentItem.getLocation());
        itemHolder.tvNIK.setText(mCurrentItem.getIdentification_no());
        if (TextUtils.isEmpty(mCurrentItem.getIdentification_no()) ) {
            itemHolder.tvNIK.setVisibility(View.GONE);
        }else{
            itemHolder.tvNIK.setVisibility(View.VISIBLE);
        }

        //initial
        itemHolder.chbPilih.setEnabled(false);
        itemHolder.imgPatient.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_ss)));
        itemHolder.imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);

        //jika pasien blm lengkap
        if (TextUtils.isEmpty(mCurrentItem.getPatient_ihs()) ) {
            itemHolder.imgPatient.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
        }
        //jika data blm lengkap
        if (!TextUtils.isEmpty(mCurrentItem.getPatient_ihs()) && mCurrentItem.getisPosteded().equals(JConst.FALSE_STRING) ) {
            itemHolder.chbPilih.setEnabled(true);
        }
        //jika blm kirim
        if (mCurrentItem.getisPosteded().equals(JConst.FALSE_STRING)) {
            itemHolder.imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_gray);
        }

        itemHolder.chbPilih.setChecked(mCurrentItem.isSelected());
        itemHolder.chbPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();
                mCurrentItem.setSelected(isChecked);
            }
        });

        itemHolder.imgPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.ReadOnlyMode()){
                    ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                    return;
                }else {
                    Intent s = new Intent(context, PatientInputActivity.class);
                    s.putExtra("uuid", mCurrentItem.getPatient_uuid());
                    s.putExtra("isCallFromSatuSehat", true);
                    s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    medicalSatuSehatActivity.startActivityForResult(s, REQUEST_CODE_PATIENT);
                }
            }
        });

    }

    public void addModel(MedicalSatuSehatModel Datas) {
        int pos = this.dataFiltered.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltered.add(Datas);
        notifyItemRangeInserted(pos, dataFiltered.size());
    }

    public void addModels(List<MedicalSatuSehatModel> Datas) {
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

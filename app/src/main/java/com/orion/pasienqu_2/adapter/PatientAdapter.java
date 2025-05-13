package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.PatientModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PatientAdapter extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<PatientModel> Datas;
    public List<PatientModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    private int REQUEST_CODE = 222;
    TextView tvInform;

    public PatientAdapter(Context context, List<PatientModel> Datas, int view, TextView tvInform) {
        this.context = context;
        this.Datas = Datas;
        this.dataFiltererd = new  ArrayList<PatientModel>();
        this.dataFiltererd.addAll(Datas);
        this.Loading = new ProgressDialog(context);
        this.view = view;
        this.tvInform = tvInform;
    }

    public void addModels(List<PatientModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(PatientModel data, int pos) {
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }

    public void addModel(PatientModel patientModel) {
        this.Datas.add(patientModel);
        notifyItemRangeInserted(Datas.size()-1,Datas.size()-1);
        this.dataFiltererd.add(patientModel);
        notifyItemRangeInserted(dataFiltererd.size()-1, Datas.size()-1);
    }

    public void removeModel(int idx) {
//        if (Datas.size() > 0){
//            this.Datas.remove(idx);
//            notifyItemRemoved(idx);
//        }
//        if (dataFiltererd.size() > 0){
//            this.dataFiltererd.remove(idx);
//            notifyItemRemoved(idx);
//        }
        if (Datas.size() > 0 && Datas.size() > idx){
            this.Datas.remove(idx);
            notifyItemRemoved(idx);
        }
        if (dataFiltererd.size() > 0 && dataFiltererd.size() > idx){
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYVE_ITEM){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(view, parent, false);
            return new ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(row);
        }
        return null;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtGenderAge, txtPatientType, txtAddrees;
        public ItemHolder(View itemView) {
            super(itemView);
            txtName   = itemView.findViewById(R.id.txtName);
            txtGenderAge  = itemView.findViewById(R.id.txtGenderAge);
            txtPatientType = itemView.findViewById(R.id.txtPatientType);
            txtAddrees = itemView.findViewById(R.id.txtAddress);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final PatientModel mCurrentItem = dataFiltererd.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtName.setText(mCurrentItem.getPatient_id()+" - "+mCurrentItem.getFirst_name()+" "+mCurrentItem.getSurname());

            itemHolder.txtGenderAge.setText('('+
                    mCurrentItem.getGenderInitialStr(context)+'/'+
                    getAge(mCurrentItem.getDate_of_birth())+
                    ')');

            if(mCurrentItem.getPatient_type_id() > 1) { //selain umum
                itemHolder.txtPatientType.setText(mCurrentItem.getpatientTypeStr(context)); //+" - "+ mCurrentItem.getDescription());
            }else{
                itemHolder.txtPatientType.setText(mCurrentItem.getpatientTypeStr(context));
            }
            itemHolder.txtAddrees.setText(mCurrentItem.getAddress_street_1());


            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, PatientInputActivity.class);
                        s.putExtra("uuid", mCurrentItem.getUuid());
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((Activity) context).startActivityForResult(s, REQUEST_CODE);
                    }
                }
            });
        }else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
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


    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pg_loading);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    dataFiltererd.removeAll(dataFiltererd);
                    dataFiltererd.addAll(Datas);
                } else {
                    List<PatientModel> filteredList = new ArrayList<PatientModel>();

                    for (PatientModel row : Datas) {

                        if (row.getPatientNameId().toLowerCase().contains(charString.toLowerCase()) || row.getAddress_street_1().toLowerCase().contains(charString.toLowerCase()))  {
                            filteredList.add(row);
                        }
                    }

                    dataFiltererd = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFiltererd;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFiltererd = (List<PatientModel>) filterResults.values;
                notifyDataSetChanged();

                if (getItemCount() == 0) {
                    tvInform.setVisibility(View.VISIBLE);
                    tvInform.setText(context.getString(R.string.no_data_filter));
                }
                else {
                    tvInform.setVisibility(View.GONE);
                }
            }
        };
    }

    private String getAge(long birthDate){
        long LDateNow = Global.serverNowLong();

        Date dateNow, dateBirth;
        dateBirth = new Date(Integer.parseInt(Global.getTahun(birthDate)), Integer.parseInt(Global.getBulan(birthDate)),
                Integer.parseInt(Global.getHari(birthDate)));
        dateNow = new Date(Integer.parseInt(Global.getTahun(LDateNow)), Integer.parseInt(Global.getBulan(LDateNow)),
                Integer.parseInt(Global.getHari(LDateNow)));

        return String.valueOf(Global.getUmur(dateNow, dateBirth, true));
    }

}
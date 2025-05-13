package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.calendar.CalendarInputActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.PatientModel;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<AppointmentModel> Datas;
    private List<AppointmentModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    public boolean isHome = false;
    private int REQUEST_LOAD_CALENDER = 555, REQUEST_LOAD_HOME = 111;

    public AppointmentAdapter(Context context, List<AppointmentModel> Datas, int view) {
        this.context = context;
        this.Datas = Datas;
        this.dataFiltererd = new  ArrayList<AppointmentModel>();
        this.dataFiltererd.addAll(Datas);
        this.Loading = new ProgressDialog(context);
        this.view = view;
    }

    public void addModels(List<AppointmentModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }
    public void addModel(AppointmentModel data, int pos) {
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

        private TextView txtDate, txtPatient;
        public ItemHolder(View itemView) {
            super(itemView);
            txtDate   = itemView.findViewById(R.id.txtDate);
            txtPatient  = itemView.findViewById(R.id.txtPatient);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            final AppointmentModel mCurrentItem = dataFiltererd.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.txtDate.setText(Global.getDateFormated(mCurrentItem.getAppointment_date()) +"  -  "+Global.getTimeFormated(mCurrentItem.getAppointment_date()));
            itemHolder.txtPatient.setText(mCurrentItem.getPatient_text());
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, CalendarInputActivity.class);
                        s.putExtra("uuid", mCurrentItem.getUuid());
                        s.putExtra("appointment_date", Global.getDateFormated(mCurrentItem.getAppointment_date()));
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        if (isHome) {
                            ((Activity) context).startActivityForResult(s, REQUEST_LOAD_HOME);
                        } else {
                            ((Activity) context).startActivityForResult(s, REQUEST_LOAD_CALENDER);
                        }

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
                    List<AppointmentModel> filteredList = new ArrayList<AppointmentModel>();
                    for (AppointmentModel row : Datas) {
                        if (row.getPatient_text().toLowerCase().contains(charString.toLowerCase())) {
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
                dataFiltererd = (List<AppointmentModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
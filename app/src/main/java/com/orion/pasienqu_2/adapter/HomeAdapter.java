package com.orion.pasienqu_2.adapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.billing.BillingActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.activities.patient.PatientListFragment;
import com.orion.pasienqu_2.activities.record.RecordFragment;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.HomeModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends RecyclerView.Adapter implements Filterable {
    Context context;
    List<HomeModel> Datas;
    private List<HomeModel> dataFiltererd;
    private final int VIEW_TYVE_ITEM = 0, VIEW_TYVE_LOADING = 1;
    private ProgressDialog Loading;
    private int view;
    public int REQUEST_CODE = 111;

    public HomeAdapter(Context context, List<HomeModel> Datas, int view) {
        this.context = context;
        this.Datas = Datas;
        this.dataFiltererd = new  ArrayList<HomeModel>();
        this.dataFiltererd.addAll(Datas);
        this.Loading = new ProgressDialog(context);
        this.view = view;
    }

    public void addModels(List<HomeModel> Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.addAll(Datas);
        notifyItemRangeInserted(pos, Datas.size());
        this.dataFiltererd.addAll(Datas);
        notifyItemRangeInserted(pos, dataFiltererd.size());
    }


    public void addModel(HomeModel Datas) {
        int pos = this.dataFiltererd.size();
        this.Datas.add(Datas);
        notifyItemRangeInserted(pos, 1);
        this.dataFiltererd.add(Datas);
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
            return new HomeAdapter.ItemHolder(row);
        }else if(viewType == VIEW_TYVE_LOADING){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.item_loading, parent, false);
            return new HomeAdapter.LoadingViewHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HomeAdapter.ItemHolder){
            final HomeModel mCurrentItem = dataFiltererd.get(position);
            final HomeAdapter.ItemHolder itemHolder = (HomeAdapter.ItemHolder) holder;
            final BottomNavigationView navigation = ((AppCompatActivity) context).findViewById(R.id.nav_view);

            if (mCurrentItem.getType().equals(JConst.TITLE_HOME_PATIENT)){
                itemHolder.txtTitle.setText(context.getString(R.string.title_home_patient_text));
                itemHolder.txtTitleTotal.setText(context.getString(R.string.title_home_tipe_total_total));
                itemHolder.btnAction.setImageResource(R.drawable.ic_baseline_person_add_alt_1_24);
                itemHolder.btnAction.setOnClickListener(view1 -> {
                    //offline
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, PatientInputActivity.class);
                        s.putExtra("uuid", "");
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((AppCompatActivity) context).startActivityForResult(s, REQUEST_CODE);
                    }
                });
                itemHolder.itemView.setOnClickListener(view1 -> {
                    //cara1
                    Fragment targetFragment = new PatientListFragment();
                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fl_container, targetFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    navigation.setSelectedItemId(R.id.navigation_patients);
                });
            }else if (mCurrentItem.getType().equals(JConst.TITLE_HOME_MEDICAL_RECORD)){
                itemHolder.txtTitle.setText(context.getString(R.string.title_home_medical_record_text));
                itemHolder.txtTitleTotal.setText(context.getString(R.string.title_home_tipe_total_total));
                itemHolder.btnAction.setImageResource(R.drawable.ic_baseline_book_24);
                itemHolder.btnAction.setOnClickListener(view1 -> {
                    //offline
                    if(Global.ReadOnlyMode()){
                        ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                        return;
                    }else {
                        Intent s = new Intent(context, RecordInputActivity.class);
                        s.putExtra("uuid", "");
                        s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        ((AppCompatActivity) context).startActivityForResult(s, REQUEST_CODE);
                    }
                });
                itemHolder.itemView.setOnClickListener(view1 -> {
                    //cara2
                    navigation.setSelectedItemId(R.id.navigation_records);
                });
            }else if (mCurrentItem.getType().equals(JConst.TITLE_HOME_BILLING)){
                itemHolder.txtTitle.setText(context.getString(R.string.title_home_billing_text));
                itemHolder.txtTitleTotal.setText(context.getString(R.string.title_home_tipe_total_today));
                itemHolder.btnAction.setImageResource(R.drawable.ic_baseline_attach_money_24);
                itemHolder.btnAction.setOnClickListener(view1 -> {
                  //offline
                  if(Global.ReadOnlyMode()){
                    ShowDialog.warningDialog(((AppCompatActivity) context), context.getString(R.string.title_app), context.getString(R.string.grace_period_eror));
                    return;
                  }else {
                      Intent s = new Intent(context, BillingActivity.class);
                      s.putExtra("uuid", "");
                      s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                      ((AppCompatActivity) context).startActivityForResult(s, REQUEST_CODE);
                  }
                });
                itemHolder.itemView.setOnClickListener(view1 -> {
                    Intent s = new Intent(context, BillingActivity.class);
                    s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(s);
                });
            }
            if (mCurrentItem.getType().equals(JConst.TITLE_HOME_BILLING)) {
                itemHolder.txtTotal.setText(":   " + Global.FloatToStrFmt(mCurrentItem.getTotal(), true));
                itemHolder.txtThisMonth.setText(":   " + Global.FloatToStrFmt(mCurrentItem.getThis_month(), true));
            }else{
                itemHolder.txtTotal.setText(":   " + Global.FloatToStrFmt(mCurrentItem.getTotal()));
                itemHolder.txtThisMonth.setText(":   " + Global.FloatToStrFmt(mCurrentItem.getThis_month()));
            }

//
//            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent s = new Intent(context, DailyTripLogInputActivity.class);
//                    s.putExtra("uuid", mCurrentItem.getUuid());
//                    context.startActivity(s);
//                    DailyTripLogAdapter.this.notifyItemChanged(position);
//                }
//            });
//
//            itemHolder.chbSelect.setOnClickListener(view1 -> {
//                mCurrentItem.setCheck(itemHolder.chbSelect.isChecked());
//            });
        }else if (holder instanceof HomeAdapter.LoadingViewHolder){
            HomeAdapter.LoadingViewHolder loadingViewHolder = (HomeAdapter.LoadingViewHolder)holder;
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

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtTitleTotal, txtTotal, txtThisMonth;
        private FloatingActionButton btnAction;
        public ItemHolder(View itemView) {
            super(itemView);
            txtTitle   = itemView.findViewById(R.id.txtTitle);
            txtTitleTotal   = itemView.findViewById(R.id.txtTitleTotal);
            txtTotal   = itemView.findViewById(R.id.txtTotal);
            txtThisMonth  = itemView.findViewById(R.id.txtThisMonth);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
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
                    List<HomeModel> filteredList = new ArrayList<HomeModel>();
                    for (HomeModel row : Datas) {
                        String tripType = "";
                        filteredList.add(row);
                    }

                    dataFiltererd = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFiltererd;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFiltererd = (List<HomeModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
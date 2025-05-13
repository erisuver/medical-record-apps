package com.orion.pasienqu_2.activities.record;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.patient.FilterPatientActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.adapter.PatientAdapter;
import com.orion.pasienqu_2.adapter.RecordAdapter;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ILoadMore;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.FilterModel;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RecordFragment extends Fragment {
    private RecyclerView rcvLoad;
    private View v;
    private SwipeRefreshLayout swipe;
    public RecordAdapter mAdapter;
    public List<RecordModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private AppCompatActivity thisAppCompat;
    private FloatingActionButton btnAdd, btnAutoAdd;
    private SearchView txtSearch;
    private androidx.appcompat.widget.Toolbar toolbar;
    private String diagnosa;
    private int idxSelectedType, idWorkLocation;
    private long dateFrom, dateTo;
    private boolean archived;
    private String valueSort = "";
    private ProgressBar pgLoading;
    private boolean isFilter = false;
    private TextView txtInform, tvTotalBiling, tvTotalRecord;
    private int patientTypeId;
    ILoadMore loadMore;
    private int totalItemCount, lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;
    private boolean IsLoading;
    int visibleThreshold = 20;
    private String SearchQuery = "";
    private View loadingView;
    private ChipGroup chipGroup;
    public List<FilterModel> SelectedFilter = new ArrayList<>();
    private int REQ_CODE_LOAD = 333;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_list_fragment, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        ResetData();
        return view;
    }

    private void CreateView(){
        rcvLoad = (RecyclerView) v.findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        btnAutoAdd = (FloatingActionButton) v.findViewById(R.id.btnAutoAdd);
        txtSearch = (SearchView) v.findViewById(R.id.txtSearch);
        pgLoading = (ProgressBar) v.findViewById(R.id.pg_loading);
        txtInform = (TextView) v.findViewById(R.id.txtInform);
        tvTotalRecord = (TextView) v.findViewById(R.id.tvTotalRecord);
        tvTotalBiling = (TextView) v.findViewById(R.id.tvTotalBilling);
        loadingView = (View) v.findViewById(R.id.loadingView);
        chipGroup = (ChipGroup) v.findViewById(R.id.chipGroup);

        this.mAdapter = new RecordAdapter(getActivity(), ListItems, R.layout.record_list_item, tvTotalRecord, tvTotalBiling, txtInform);
        thisActivity = getActivity();

        toolbar = (androidx.appcompat.widget.Toolbar) v.findViewById(R.id.ToolbarAct);
        thisAppCompat = (AppCompatActivity) getActivity();
        thisAppCompat.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        linearLayoutManager = (LinearLayoutManager)rcvLoad.getLayoutManager();
        rcvLoad.setAdapter(mAdapter);
        loadRefresh();
    }

    private void EventClass(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                synchronize();
//                LoadData();
                if (!IsLoading){
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }
                swipe.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(view -> {
            if(Global.ReadOnlyMode()){
                ShowDialog.warningDialog(thisActivity, getString(R.string.title_app), getString(R.string.grace_period_eror));
                return;
            }else {
                Intent s = new Intent(thisActivity, RecordInputActivity.class);
                s.putExtra("uuid", "");
                s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(s, REQ_CODE_LOAD);
            }
        });


        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //                mAdapter.getFilter().filter(newText);
                if (newText.equals("")) {
                    SearchQuery = "";
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }else if (newText.length() >= 2){
                    SearchQuery = newText;
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }

                return false;
            }
        });

        setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
//                mAdapter.addModel(null);
//                mAdapter.notifyItemChanged(ListItems.size()-1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mAdapter.removeModel(ListItems.size()-1);
                        LoadData();
                    }
                },100);
            }
        });

        rcvLoad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RefreshRecyclerView();
            }
        });

        btnAutoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String datenow = String.valueOf(Global.getMillisDate(Global.serverNow()));
                String sqlInsert = "INSERT INTO pasienqu_record (uuid, record_date, work_location_id, patient_id, active, patient_type_id) " +
                        " SELECT " +
                        "    uuid, " +
                        "    " + datenow + " AS record_date, " +
                        "    (SELECT MAX(id) FROM pasienqu_work_location) AS work_location_id, " +
                        "    id AS patient_id, 'true' as active, 1 as patient_type_id " +
                        " FROM " +
                        "    (SELECT " +
                        "       hex(randomblob(4)) || '-' || " +
                        "       hex(randomblob(2)) || '-' || " +
                        "       '4' || substr(hex(randomblob(2)), 2) || '-' || " +
                        "       substr('89AB', 1 + (abs(random()) % 4), 1) || " +
                        "       substr(hex(randomblob(2)), 2) || '-' || " +
                        "       hex(randomblob(6)) AS uuid, " +
                        "       id " +
                        "     FROM pasienqu_patient " +
                        "     WHERE patient_ihs IS NOT NULL) AS subquery";

                JApplication.getInstance().db.execSQL(sqlInsert);

                loadRefresh();
            }
        });
    }

    public void LoadData() {
//        mAdapter.removeAllModel();

        RecordTable recordTable = new RecordTable(thisActivity);
        recordTable.setFilter(idxSelectedType, dateFrom, dateTo, diagnosa, idWorkLocation, archived, patientTypeId);
        recordTable.setSorting(valueSort);
        SharedPrefsUtils.setStringPreference(thisActivity,JConst.sort_key_medical,valueSort);
        recordTable.setOffset(mAdapter.getItemCount());
        recordTable.setSearchQuery(SearchQuery);

        if (mAdapter.getItemCount() == 0) {
            ListItems = recordTable.getRecordsLimit(visibleThreshold);
        }else{
            ListItems = recordTable.getRecordsLimit(100);
        }

        if (totalItemCount != 0 || ListItems.size() > 0) {
            mAdapter.addModels(ListItems);
            txtInform.setVisibility(View.GONE);
        }else{
            txtInform.setVisibility(View.VISIBLE);
            if (!isFilter) {
                txtInform.setText(String.format(thisActivity.getString(R.string.no_data), thisActivity.getString(R.string.medical_record_title)));
            }else{
                txtInform.setText(R.string.no_data_filter);
            }

        }
//        mAdapter.changeModel(ListItems.get(JApplication.getInstance().lastIdxList), JApplication.getInstance().lastIdxList);

        mAdapter.notifyDataSetChanged();
        IsLoading = false;
        setLoaded(false);
        swipe.setRefreshing(false);

        tvTotalRecord.setText(thisActivity.getString(R.string.medical_records) + " : " + Global.FloatToStrFmt(recordTable.getCountRecordFiltered()));
        tvTotalBiling.setText(thisActivity.getString(R.string.billing_total) + " : Rp. " + Global.FloatToStrFmt(recordTable.getCountBillingFiltered()));


//        loadingView.setVisibility(View.GONE);
    }

    private void ResetData(){   
        idxSelectedType = 0; //thismont
        dateFrom = Global.serverNowLong();
        dateTo = Global.serverNowLong();
        diagnosa = "";
        idWorkLocation = 0;
        archived = false;
        isFilter = false;
        valueSort = JConst.value_desc;
        SharedPrefsUtils.setStringPreference(thisActivity, valueSort, JConst.sort_key_medical);
        patientTypeId = 0;
//        LoadData();
//        loadingView.setVisibility(View.VISIBLE);
        SelectedFilter.clear();
        SelectedFilter.add(new FilterModel(getString(R.string.this_month), String.valueOf(JConst.idx_filter_calendar_this_month), JConst.tipe_filter_date));
        createChips();
        loadRefresh();
    }

    private void setSort() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                valueSort = SharedPrefsUtils.getStringPreference(thisActivity, JConst.sort_key_medical);
                loadRefresh();
            }
        };
        List<LovModel> listItem = ListValue.list_sorting_record(thisActivity);
        ShowDialog.showLov(thisActivity, listItem, valueSort, runnable, JConst.sort_key_medical);
    }

    private void synchronize() {
        ProgressDialog loading = new ProgressDialog(thisActivity);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LoadData();
                loading.dismiss();
            }
        };
        SyncUp.sync_all(thisActivity, getContext(), () -> {});
        SyncDown.syncDown(thisActivity, getContext(), "pasienqu.medical.record", pgLoading, runnable, "");

    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    public void setLoaded(boolean loading) {
        IsLoading = loading;
    }

    public void RefreshRecyclerView(){
        totalItemCount  = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!IsLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
            if (loadMore != null){
                loadMore.onLoadMore();
                setLoaded(true);
            }
        }
    }

    public void loadRefresh(){
        mAdapter.removeAllModel();
        RefreshRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recap_list, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                Intent i = new Intent(getActivity(), FilterRecordActivity.class);
                i.putExtra("idxSelectedType", idxSelectedType);
                i.putExtra("date_from", dateFrom);
                i.putExtra("date_to", dateTo);
                i.putExtra("diagnosa", diagnosa);
                i.putExtra("work_location", idWorkLocation);
                i.putExtra("archived", archived);
                i.putExtra("patient_type_id", patientTypeId);
                i.putExtra("SelectedFilter", (Serializable) SelectedFilter);
                startActivityForResult(i, 1);

                txtSearch.setQuery("", false); // clear the text
                return true;
            case R.id.menu_reset:
                ResetData();
                txtSearch.setQuery("", false); // clear the text
                return true;
            case R.id.menu_sort:
                setSort();
                txtSearch.setQuery("", false); // clear the text
                return true;
            case R.id.menu_synchronize:
                synchronize();
                txtSearch.setQuery("", false); // clear the text
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        idxSelectedType = extra.getInt("idxSelectedType");
                        dateFrom = extra.getLong("date_from");
                        dateTo = extra.getLong("date_to");
                        diagnosa = extra.getString("diagnosa");
                        idWorkLocation = extra.getInt("work_location");
                        archived = extra.getBoolean("archived");
                        patientTypeId = extra.getInt("patient_type_id");
                        isFilter = true;
                        SelectedFilter = (List<FilterModel>) data.getSerializableExtra("SelectedFilter");
                        createChips();
                    }
                }
                break;
        }
        loadRefresh();
    }

    //hilangkan pilihan menu sync
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(3).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void createChips() {
        chipGroup.removeAllViews();
        if (SelectedFilter != null) {
            for (FilterModel item : SelectedFilter) {
                Chip chip = new Chip(thisActivity);
                chip.setText(item.getName());
//                chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
                chip.setCloseIconEnabled(true);
                chip.setChecked(true);
                chip.canScrollHorizontally(View.LAYOUT_DIRECTION_LOCALE);
                chip.setChipBackgroundColor(getResources().getColorStateList(R.color.pasienqu_blue));
                chip.setTextColor(getResources().getColorStateList(R.color.white));
                chip.setOnCloseIconClickListener(v -> {
                    SelectedFilter.remove(item);
                    chipGroup.removeView(chip);
                    if(item.getTipe_filter().equals(JConst.tipe_filter_date)){
                        idxSelectedType = 3;
                    }else if (item.getTipe_filter().equals(JConst.tipe_filter_diagnosa)){
                        diagnosa = "";
                    }else if (item.getTipe_filter().equals(JConst.tipe_filter_work_location)){
                        idWorkLocation = 0;
                    }else if (item.getTipe_filter().equals(JConst.tipe_filter_patient_type)){
                        patientTypeId = 0;
                    }
                    loadRefresh();
                });
                if (!item.getName().equals(getString(R.string.all_dates))) {
                    chipGroup.addView(chip);
                }
            }
        }
        setVisibilityFilter();
    }

    private void setVisibilityFilter(){
        if (SelectedFilter.isEmpty()){
            chipGroup.setVisibility(View.GONE);
        }else{
            chipGroup.setVisibility(View.VISIBLE);
        }
    }

}

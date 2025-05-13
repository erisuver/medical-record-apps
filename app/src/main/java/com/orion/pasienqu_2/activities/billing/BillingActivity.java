package com.orion.pasienqu_2.activities.billing;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.billing_template.FilterBillingActivity;
import com.orion.pasienqu_2.adapter.BillingAdapter;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ILoadMore;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BillingActivity extends CustomAppCompatActivity {
    public BillingAdapter mAdapter;
    public List<BillingModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private SearchView txtSearch;
    private TextView txtBillingtotal;
    private androidx.appcompat.widget.Toolbar toolbar;
    private Menu menu;
    private boolean archived;
    private String notes;
    private int dateIdx;
    private long dateFrom, dateTo;
    private AppCompatActivity thisAppCompat;
    private ProgressBar pgLoading;
    ILoadMore loadMore;
    private int totalItemCount, lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;
    private boolean IsLoading;
    int visibleThreshold = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        CreateView();
        InitClass();
        EventClass();
        ResetData();
    }

    private void CreateView(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        txtSearch = (SearchView) findViewById(R.id.txtSearch);
        txtBillingtotal = (TextView) findViewById(R.id.txtTotalBilling);
        thisActivity = BillingActivity.this;
        this.mAdapter = new BillingAdapter(thisActivity, ListItems, R.layout.billing_list_item, txtBillingtotal,false);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.ToolbarAct);
        thisAppCompat = (AppCompatActivity) thisActivity;
        thisAppCompat.setSupportActionBar(toolbar);

        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
    }

    private void InitClass(){
        BillingTable billingTable = JApplication.getInstance().billingTable;
        rcvLoad.setLayoutManager(new GridLayoutManager(thisActivity, 1, GridLayoutManager.VERTICAL, false));
        linearLayoutManager = (LinearLayoutManager)rcvLoad.getLayoutManager();
        rcvLoad.setAdapter(mAdapter);

    }

    private void EventClass(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                synchronize();
//                loadData();
                if (!IsLoading){
                    mAdapter.removeAllModel();
                    RefreshRecyclerView();
                }
                swipe.setRefreshing(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.ReadOnlyMode()){
                    ShowDialog.warningDialog(thisActivity, getString(R.string.title_app), getString(R.string.grace_period_eror));
                }else {
                    Intent intent = new Intent(thisActivity, BillingInputActivity.class);
                    intent.putExtra("uuid", "");
                    intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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

                mAdapter.removeAllModel();
                RefreshRecyclerView();
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
//                        mAdapter.removeModel(ListItems.size());
                        loadData();
                    }
                },1000);
            }
        });

        rcvLoad.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RefreshRecyclerView();
            }
        });
    }

    private void loadData(){
//        Bundle extra = this.getIntent().getExtras();
//        int patientId = extra.getInt("patient_id");
//        String uuid = extra.getString("uuid");
//        boolean isMedicalRecord = extra.getBoolean("medical_record");
//
//        if (isMedicalRecord){
//            mAdapter.removeAllModel();
//            BillingTable billingTable = new BillingTable(thisActivity);
//            mAdapter.addModels(billingTable.getRecords());
//        }

        mAdapter.removeAllModel();
        BillingTable billingTable = new BillingTable(thisActivity);
        billingTable.setFilter(dateIdx, dateFrom, dateTo, notes, archived);
        billingTable.setOffset(mAdapter.getItemCount());

        ListItems = billingTable.getRecordsLimit();
        mAdapter.addModels(ListItems);

        mAdapter.notifyDataSetChanged();
        setLoaded(false);
        swipe.setRefreshing(false);

        String total = Global.FloatToStrFmt(billingTable.getSumByFilter(), true);
        txtBillingtotal.setText("Total: "+total);
    }

    private void ResetData(){
        archived = false;
        notes = "";
        dateIdx = 0; //set default to this month
        dateFrom = Global.serverNowLong();
        dateTo = Global.serverNowLong();
        loadData();
//        loadRefresh();
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
                loadData();
                loading.dismiss();
            }
        };
        SyncUp.sync_all(thisActivity, getApplicationContext(), runnable);
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.billing", pgLoading, runnable, "");
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.billing.item", pgLoading, runnable, "");
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

    private void loadRefresh(){
        mAdapter.removeAllModel();
        RefreshRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_filter:
                Intent i = new Intent(BillingActivity.this, BillingFilterActivity.class);
                i.putExtra("archived", archived);
                i.putExtra("notes", notes);
                i.putExtra("date_idx", dateIdx);
                i.putExtra("date_from", dateFrom);
                i.putExtra("date_to", dateTo);
                i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 1);
                return true;
            case R.id.menu_reset:
                ResetData();
                return true;
            case R.id.menu_synchronize:
                synchronize();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_template_item, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    archived = extra.getBoolean("archived");
                    notes = extra.getString("notes");
                    dateIdx = extra.getInt("date_idx");
                    dateFrom = extra.getLong("date_from");
                    dateTo = extra.getLong("date_to");
                }
                break;
        }
//        loadData();
        RefreshRecyclerView();
    }
    //

    //hilangkan pilihan menu sync
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(2).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }
}


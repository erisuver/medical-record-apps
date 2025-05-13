package com.orion.pasienqu_2.activities.more.billing_template;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.more.BillingTemplateAdapter;
import com.orion.pasienqu_2.data_table.more.BillingTemplateTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingTemplateModel;
import com.orion.pasienqu_2.models.LovModel;

import java.util.ArrayList;
import java.util.List;

public class BillingTemplateActivity extends CustomAppCompatActivity {
    public BillingTemplateAdapter mAdapter;
    public List<BillingTemplateModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private Menu menu;
    private boolean archived = false;
    private ProgressBar pgLoading;
    private View loading;
    private boolean isFilter = false;
    private String valueSort = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_template);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.billing_templates));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        loading = (View) findViewById(R.id.loadingView);

        thisActivity = BillingTemplateActivity.this;
        this.mAdapter = new BillingTemplateAdapter(thisActivity, ListItems, R.layout.billing_template_list_item);
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(thisActivity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        loadData();
    }

    private void EventClass(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                synchronize();
                loadData();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity, BillingTemplateInputActivity.class);
                intent.putExtra("uuid", "");
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadData(){
        mAdapter.removeAllModel();
        BillingTemplateTable billingTemplateTable = new BillingTemplateTable(thisActivity);
        BillingTemplateModel billingTemplateModel = new BillingTemplateModel();

        billingTemplateTable.setFilter(archived);
        billingTemplateTable.setSorting(valueSort);
        SharedPrefsUtils.setStringPreference(thisActivity,JConst.sort_key_billing_template,valueSort);
        //cek data kosong
        if (billingTemplateTable.getRecords().size() > 0) {
            mAdapter.addModels(billingTemplateTable.getRecords());
        }else{
            if (isFilter) {
                billingTemplateModel.setName(getString(R.string.no_data_filter));
            }else{
                billingTemplateModel.setName(String.format(getString(R.string.no_data), getString(R.string.billing_templates)));
            }
            mAdapter.addModel(billingTemplateModel);
        }

        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }

    private void ResetData(){
        archived = false;
        isFilter = false;
        valueSort = JConst.value_asc;
        SharedPrefsUtils.setStringPreference(thisActivity, valueSort, JConst.sort_key_billing_template);
        loadData();
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

        SyncUp.sync_all(thisActivity, getApplicationContext(), () -> {});
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.billing.template", pgLoading, runnable, "");
    }

    private void setSort() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                valueSort = SharedPrefsUtils.getStringPreference(thisActivity, JConst.sort_key_billing_template);
                loadData();
            }
        };
        List<LovModel> listItem = ListValue.list_sorting_name_az(thisActivity);
        ShowDialog.showLov(thisActivity, listItem, valueSort, runnable, JConst.sort_key_billing_template);
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
                Intent i = new Intent(BillingTemplateActivity.this, FilterBillingActivity.class);
                i.putExtra("archived", archived);
                startActivityForResult(i, 2);
                return true;
            case R.id.menu_reset:
                ResetData();
                return true;
            case R.id.menu_synchronize:
                synchronize();
                return true;
            case R.id.menu_sort:
                setSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_template_item_with_sort, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    archived = extra.getBoolean("archived");
                    isFilter = true;
                }
                break;
        }
        loadData();
    }

    //hilangkan pilihan menu sync
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(2).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }


}
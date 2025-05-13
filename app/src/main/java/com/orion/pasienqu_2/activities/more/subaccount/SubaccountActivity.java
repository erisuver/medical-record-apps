package com.orion.pasienqu_2.activities.more.subaccount;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

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
import com.orion.pasienqu_2.adapter.more.SubaccountAdapter;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.SubaccountTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.SubaccountModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SubaccountActivity extends CustomAppCompatActivity {
    public SubaccountAdapter mAdapter;
    public List<SubaccountModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private SubaccountTable subaccountTable;
    private boolean archived;
    private ProgressBar pgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subaccount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.sub_accounts));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        thisActivity = SubaccountActivity.this;
        subaccountTable = new SubaccountTable(thisActivity);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);

        Runnable runnableArchive = new Runnable() {
            @Override
            public void run() {
                setActiveUnarchived();
            }
        };

        this.mAdapter = new SubaccountAdapter(thisActivity, ListItems, R.layout.subaccount_list_item, runnableArchive);
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
                Intent intent = new Intent(thisActivity, SubaccountInputActivity.class);
                intent.putExtra("uuid", "");
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadData(){
        mAdapter.removeAllModel();
        subaccountTable.setFilter(archived);
        mAdapter.addModels(subaccountTable.getRecords());

        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
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
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        String filterCompanyId = "?filter=[[\"company_id\",\"=\",\""+loginCompanyModel.getId()+"\"]]";
        SyncUp.sync_all(thisActivity, getApplicationContext(), () -> {});
        SyncDown.syncDown(thisActivity, getApplicationContext(), "res.users", pgLoading, runnable, filterCompanyId);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setActiveUnarchived(){
//        GlobalTable globalTable;
//        globalTable = ((JApplication) getApplicationContext()).globalTable;
//
//        String uuid = "";
//        for (int i = 0; i < ListItems.size(); i++){
//            uuid = ListItems.get(i).getUuid();
//        }
//
//        boolean isArchive = globalTable.isArchived("pasienqu_subaccount", uuid);
//        if (isArchive) {
//            globalTable.unarchive("pasienqu_subaccount", uuid, "res.users");
//            this.archived = false;
//        }else{
//            globalTable.archive("pasienqu_subaccount", uuid, "res.users");
//            this.archived = true;
//        }

        loadData();
    }


}
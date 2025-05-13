package com.orion.pasienqu_2.activities.record;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.billing.BillingActivity;
import com.orion.pasienqu_2.activities.billing.BillingInputActivity;
import com.orion.pasienqu_2.adapter.BillingAdapter;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingModel;

import java.util.ArrayList;
import java.util.List;

public class
RecordBillingActivity extends CustomAppCompatActivity {
    public BillingAdapter mAdapter;
    public List<BillingModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private TextView txtBillingtotal;
    private ProgressBar pgLoading;
    private int recordId, patientId;
    private String total;
    private boolean archived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_billing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_billing));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        txtBillingtotal = (TextView) findViewById(R.id.txtTotalBilling);
        thisActivity = RecordBillingActivity.this;
        this.mAdapter = new BillingAdapter(thisActivity, ListItems, R.layout.billing_list_item, txtBillingtotal, true);

        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
    }

    private void InitClass(){
        Bundle extra = this.getIntent().getExtras();
        recordId = extra.getInt("record_id");
        String uuid = extra.getString("uuid");
        patientId = extra.getInt("patient_id");
        boolean isDetail = extra.getBoolean("mode_detail");

        if (isDetail){
            btnAdd.setVisibility(View.GONE);
//            mAdapter.isClickable = false;
            mAdapter.isDetail = true;
        }

        BillingTable billingTable = JApplication.getInstance().billingTable;
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
                Intent intent = new Intent(thisActivity, BillingInputActivity.class);
                intent.putExtra("uuid", "");
                intent.putExtra("patient_id", patientId);
                intent.putExtra("record_id", recordId);
                intent.putExtra("medical_record", true);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void loadData(){
//        boolean isMedicalRecord = extra.getBoolean("medical_record");
//
//        if (isMedicalRecord){
//            mAdapter.removeAllModel();
//            BillingTable billingTable = new BillingTable(thisActivity);
//            mAdapter.addModels(billingTable.getRecords());
//        }

        mAdapter.removeAllModel();
        BillingTable billingTable = new BillingTable(thisActivity);
//        billingTable.setFilter(dateIdx, dateFrom, dateTo, notes, archived);
        billingTable.setArchived(archived);
        mAdapter.addModels(billingTable.getRecordsByMedical(recordId));

        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);

        total = Global.FloatToStrFmt(billingTable.getSumByMedicalRecord(recordId));
        txtBillingtotal.setText("Total: Rp. "+total);
    }


    private void synchronize() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //
            }
        };
        SyncUp.sync_all(thisActivity, getApplicationContext(), runnable);
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.billing.item", pgLoading, runnable, "");
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.billing", pgLoading, runnable, "");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
    }

    @Override
    public void onBackPressed() {
        Intent i = thisActivity.getIntent();
        i.putExtra("total_billing", total);
        setResult(RESULT_OK, i);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }
}
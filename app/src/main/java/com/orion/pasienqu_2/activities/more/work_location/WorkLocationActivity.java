package com.orion.pasienqu_2.activities.more.work_location;

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
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.note_template.FilterNoteTemplateActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateInputActivity;
import com.orion.pasienqu_2.adapter.more.WorkLocationAdapter;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncOdoo;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WorkLocationActivity extends CustomAppCompatActivity {
    public WorkLocationAdapter mAdapter;
    public List<WorkLocationModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private Menu menu;
    private boolean archived;
    private ProgressBar pgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.work_location));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);

        this.mAdapter = new WorkLocationAdapter(WorkLocationActivity.this, ListItems, R.layout.work_location_list_item);
        thisActivity = WorkLocationActivity.this;
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(WorkLocationActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        loadData();
    }

    private void ResetData(){
        archived = false;
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
                Intent intent = new Intent(WorkLocationActivity.this, WorkLocationInputActivity.class);
                intent.putExtra("uuid", "");
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void loadData(){
        mAdapter.removeAllModel();
        WorkLocationTable workLocationTable = new WorkLocationTable(thisActivity);

        //sementara
        workLocationTable.setFilter(archived);
        if (workLocationTable.getRecords().size() == 0){
            WorkLocationModel data = new WorkLocationModel(0,"",getString(R.string.work_location_no_data), "", "");
            mAdapter.addModel(data);
        }else{
            mAdapter.addModels(workLocationTable.getRecords());
        }

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
        SyncUp.sync_all(thisActivity, getApplicationContext(), () -> {});
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.work.location", pgLoading, runnable, "");

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
                Intent i = new Intent(WorkLocationActivity.this, FilterWorkLocationActivity.class);
                i.putExtra("archived", archived);
                startActivityForResult(i, 2);
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
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    archived = extra.getBoolean("archived");
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

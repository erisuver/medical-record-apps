package com.orion.pasienqu_2.activities.lov;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.work_location.WorkLocationActivity;
import com.orion.pasienqu_2.activities.more.work_location.WorkLocationInputActivity;
import com.orion.pasienqu_2.adapter.LovCheckAdapter;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.models.LovCheckModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LovCheckActivity extends CustomAppCompatActivity {
    public LovCheckAdapter mAdapter;
    public List<LovCheckModel> ListItems, ListDefault = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lov_check);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.choose_reminder));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
//        Bundle extra = this.getIntent().getExtras();
//        ListItems = (ArrayList<LovCheckModel>) getIntent().getSerializableExtra("list");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ListItems = (ArrayList<LovCheckModel>) args.getSerializable("ARRAYLIST");
        
        this.mAdapter = new LovCheckAdapter(LovCheckActivity.this, ListItems, R.layout.lov_check_list_item);
        thisActivity = LovCheckActivity.this;
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(LovCheckActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        loadData();
    }

    private void EventClass(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

    }

    private void loadData(){
        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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
    public void onBackPressed() {
        Intent intent = LovCheckActivity.this.getIntent();

        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)ListItems);
        intent.putExtra("BUNDLE",args);
        setResult(RESULT_OK, intent);
        finish();
    }
}

package com.orion.pasienqu_2.activities.more.note_template;

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
import com.orion.pasienqu_2.activities.calendar.FilterCalendarActivity;
import com.orion.pasienqu_2.adapter.more.NoteTemplateAdapter;

import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NoteTemplateActivity extends CustomAppCompatActivity {
    public NoteTemplateAdapter mAdapter;
    public List<NoteTemplateModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton btnAdd;
    private Menu menu;
    private boolean archived;
    private ProgressBar pgLoading;
    private View loadingLayout, mainLayout;
    private String valueSort = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_template);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.note_templates));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        loadingLayout = (View) findViewById(R.id.loadingView);
        mainLayout = (View) findViewById(R.id.main_layout);

        this.mAdapter = new NoteTemplateAdapter(NoteTemplateActivity.this, ListItems, R.layout.note_list_item);
        thisActivity = NoteTemplateActivity.this;
    }

    private void InitClass() {
        rcvLoad.setLayoutManager(new GridLayoutManager(NoteTemplateActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        loadData();
    }

    private void ResetData() {
        archived = false;
        valueSort = JConst.value_asc; //default sort asc
        SharedPrefsUtils.setStringPreference(thisActivity, valueSort, JConst.sort_key_billing_template);
        loadData();
    }

    private void EventClass() {
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
                Intent intent = new Intent(NoteTemplateActivity.this, NoteTemplateInputActivity.class);
                intent.putExtra("uuid", "");
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    private void addData(String label, String category) {

        NoteTemplateModel data = new NoteTemplateModel(0, "", label, category, "", 0);
        mAdapter.addModel(data);
        NoteTemplateTable noteTemplateTable = new NoteTemplateTable(thisActivity);
        noteTemplateTable.setFilter(archived);
        noteTemplateTable.setSorting(valueSort);
        SharedPrefsUtils.setStringPreference(thisActivity,JConst.sort_key_note_template,valueSort);

        mAdapter.addModels(noteTemplateTable.getRecordByCategory(category));

    }

    public void loadData() {
        mAdapter.removeAllModel();
        List<String> listValue = ListValue.list_value_template_category(thisActivity);
        List<String> listLabel = ListValue.list_template_category(thisActivity);
        for (int i = 0; i < listLabel.size(); i++) {
            addData(listLabel.get(i), listValue.get(i));
        }

        mAdapter.notifyDataSetChanged();
        rcvLoad.setAdapter(mAdapter);
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
        SyncDown.syncDown(thisActivity, getApplicationContext(), "pasienqu.note.template", pgLoading, runnable, "");

    }


    private void setSort() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                valueSort = SharedPrefsUtils.getStringPreference(thisActivity, JConst.sort_key_note_template);
                loadData();
            }
        };
        List<LovModel> listItem = ListValue.list_sorting_name_az(thisActivity);
        ShowDialog.showLov(thisActivity, listItem, valueSort, runnable, JConst.sort_key_note_template);
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
                Intent i = new Intent(NoteTemplateActivity.this, FilterNoteTemplateActivity.class);
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

        switch (requestCode) {
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

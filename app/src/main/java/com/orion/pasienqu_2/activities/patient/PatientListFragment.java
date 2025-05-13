package com.orion.pasienqu_2.activities.patient;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.change_password.ChangePasswordActivity;
import com.orion.pasienqu_2.activities.syncronize_data.SyncronizeDataActivity;
import com.orion.pasienqu_2.adapter.PatientAdapter;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ILoadMore;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.LovModel;
import com.orion.pasienqu_2.models.PatientModel;

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

public class PatientListFragment extends Fragment {
    private RecyclerView rcvLoad;
    private View v;
    private SwipeRefreshLayout swipe;
    public PatientAdapter mAdapter;
    public List<PatientModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private AppCompatActivity thisAppCompat;
    private FloatingActionButton btnAdd;
    private SearchView txtSearch;
    private androidx.appcompat.widget.Toolbar toolbar;
    //filter
    private String ageFrom = "", ageTo = "";
    private int genderId = 0;
    private boolean archived = false;
    private String valueSort = "";
    private ProgressBar pgLoading;
    private TextView txtInform;
    private boolean isFilter = false;
    private int patientTypeId;
    ILoadMore loadMore;
    private int totalItemCount, lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;
    private boolean IsLoading;
    int visibleLimit = 20;
    private String SearchQuery = "";
    private int REQ_CODE_ADD = 222;

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_list_fragment, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        ResetData();
        return view;
    }

    private void CreateView() {
        rcvLoad = (RecyclerView) v.findViewById(R.id.rcvLoad);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        txtSearch = (SearchView) v.findViewById(R.id.txtSearch);
        pgLoading = (ProgressBar) v.findViewById(R.id.pg_loading);
        txtInform = (TextView) v.findViewById(R.id.txtInform);

        this.mAdapter = new PatientAdapter(getActivity(), ListItems, R.layout.patient_list_item, txtInform);
        thisActivity = getActivity();

        toolbar = (androidx.appcompat.widget.Toolbar) v.findViewById(R.id.ToolbarAct);
        thisAppCompat = (AppCompatActivity) getActivity();
        thisAppCompat.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
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
                Intent i = new Intent(getActivity(), FilterPatientActivity.class);
                i.putExtra("age_from", ageFrom);
                i.putExtra("age_to", ageTo);
                i.putExtra("gender_id", genderId);
                i.putExtra("archived", archived);
                i.putExtra("patient_type_id", patientTypeId);
                startActivityForResult(i, 2);

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


    private void ResetData(){
        ageFrom = "";
        ageTo = "";
        genderId = 0;
        archived = false;
        valueSort = SharedPrefsUtils.getStringPreference(thisActivity, JConst.sort_key_patient); //default by newest
//        SharedPrefsUtils.setStringPreference(thisActivity, JConst.sort_key_patient, valueSort);
        isFilter = false;
        patientTypeId = 0;
//        LoadData();
        loadRefresh();
    }

    private void setSort(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                valueSort = SharedPrefsUtils.getStringPreference(thisActivity, JConst.sort_key_patient);
//                LoadData();
                //save last sort to shared preference
                SharedPrefsUtils.setStringPreference(thisActivity, JConst.sort_key_patient, valueSort);

                loadRefresh();
            }
        };
        List<LovModel> listItem = ListValue.list_sorting_patient(thisActivity);
        ShowDialog.showLov(thisActivity, listItem, valueSort, runnable, JConst.sort_key_patient);

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
        SyncDown.syncDown(thisActivity, getContext(), "pasienqu.patient", pgLoading, runnable, "");
    }

    private void InitClass() {
        rcvLoad.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        linearLayoutManager = (LinearLayoutManager)rcvLoad.getLayoutManager();
        rcvLoad.setAdapter(mAdapter);
//        LoadData();
        loadRefresh();
    }

    public void LoadData() {
//        mAdapter.removeAllModel();
        PatientTable patientTable = new PatientTable(thisActivity);
        patientTable.setFilter(ageFrom, ageTo, genderId, archived, patientTypeId);
        patientTable.setSorting(valueSort);
        patientTable.setOffset(mAdapter.getItemCount());
        patientTable.setSearchQuery(SearchQuery);

        ListItems = patientTable.getRecordsLimit(visibleLimit);

        if (ListItems.size() > 0 || totalItemCount != 0) {
            mAdapter.addModels(ListItems);
            txtInform.setVisibility(View.GONE);
        }else{
            txtInform.setVisibility(View.VISIBLE);
            if (!isFilter) {
                txtInform.setText(String.format(thisActivity.getString(R.string.no_data), thisActivity.getString(R.string.patient)));
            }else{
                txtInform.setText(R.string.no_data_filter);
            }

        }

        mAdapter.notifyDataSetChanged();
        setLoaded(false);
        swipe.setRefreshing(false);
    }

    private void EventClass() {
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
                Intent s = new Intent(thisActivity, PatientInputActivity.class);
                s.putExtra("uuid", "");
                s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(s, REQ_CODE_ADD);
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
        if (!IsLoading && totalItemCount <= (lastVisibleItem + visibleLimit)){
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    ageFrom = extra.getString("age_from");
                    ageTo = extra.getString("age_to");
                    genderId = extra.getInt("gender_id");
                    archived = extra.getBoolean("archived");
                    patientTypeId = extra.getInt("patient_type_id");
                    isFilter = true;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        loadRefresh();
    }

    //hilangkan pilihan menu sync
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(3).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}

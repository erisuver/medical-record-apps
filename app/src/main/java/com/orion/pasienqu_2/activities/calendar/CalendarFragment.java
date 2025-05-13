package com.orion.pasienqu_2.activities.calendar;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.patient.FilterPatientActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.adapter.AppointmentAdapter;
import com.orion.pasienqu_2.data_table.AppointmentTable;
import com.orion.pasienqu_2.globals.DrawableUtils;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncDown;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.LovCheckModel;
import com.orion.pasienqu_2.models.PatientModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {
    private RecyclerView rcvLoad;
//    private CalendarView calendarView;
    private com.applandeo.materialcalendarview.CalendarView calendarView;
    private View v;

    long date = 0;
    private SwipeRefreshLayout swipe;
    public AppointmentAdapter mAdapter;
    private Activity thisActivity;
    private AppCompatActivity thisAppCompat;
    private FloatingActionButton btnAdd;
    private FloatingActionButton btnNow;
    private SearchView txtSearch;
    private androidx.appcompat.widget.Toolbar toolbar;
    public List<AppointmentModel> ListItems = new ArrayList<>();
    //filter
    private int idxSelectedType, idWorkLocation;
    private long dateFrom, dateTo;
    private String notes;
    private boolean archived;
    private ProgressBar pgLoading;

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        v = view;
        CreateView();
        InitClass();
        EventClass();
        ResetData();
        return view;
    }

    private void CreateView() {
        rcvLoad = (RecyclerView) v.findViewById(R.id.rcvLoad);
//        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        calendarView = (CalendarView) v.findViewById(R.id.calendarView);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        btnAdd = (FloatingActionButton) v.findViewById(R.id.btnAdd);
        btnNow = (FloatingActionButton) v.findViewById(R.id.btnNow);
        txtSearch = (SearchView) v.findViewById(R.id.txtSearch);
        this.mAdapter = new AppointmentAdapter(getActivity(), ListItems, R.layout.appointment_list_item);
        thisActivity = getActivity();

        toolbar = (androidx.appcompat.widget.Toolbar) v.findViewById(R.id.ToolbarAct);
        thisAppCompat = (AppCompatActivity) getActivity();
        thisAppCompat.setSupportActionBar(toolbar);
        pgLoading = (ProgressBar) v.findViewById(R.id.pg_loading);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendar_list, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                txtSearch.setQuery("", false); // clear the text
                Intent i = new Intent(getActivity(), FilterCalendarActivity.class);
                i.putExtra("idxSelectedType", idxSelectedType);
                i.putExtra("date_from", dateFrom);
                i.putExtra("date_to", dateTo);
                i.putExtra("notes", notes);
                i.putExtra("work_location", idWorkLocation);
                i.putExtra("archived", archived);
                i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 2);
                return true;
            case R.id.menu_reset:
                txtSearch.setQuery("", false); // clear the text
                ResetData();
                return true;
            case R.id.menu_synchronize:
                synchronize();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        SyncDown.syncDown(thisActivity, getContext(), "pasienqu.appointment", pgLoading, runnable, "");
    }


    private void InitClass() {
        date = Global.serverNowWithoutTimeLong();
        rcvLoad.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        btnNow.setVisibility(View.GONE);

        List<Calendar> hightligt = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        hightligt.add(calendar);
        calendarView.setHighlightedDays(hightligt);

    }

    private void ResetData(){
        idxSelectedType = 3;
        idWorkLocation = 0;
        dateFrom = Global.serverNowLong();
        dateTo = Global.serverNowLong();
        notes = "";
        archived = false;
        LoadData();
        btnNow.callOnClick();
    }

    public void LoadData() {
        AppointmentTable appointmentTable = new AppointmentTable(thisActivity);
        appointmentTable.setFilter(idxSelectedType, idWorkLocation, dateFrom, dateTo, notes, archived);
        List<EventDay> events = appointmentTable.getListEvent(thisActivity);
        calendarView.setEvents(events);

        LoadDataByDate();
        swipe.setRefreshing(false);
    }

    public void LoadDataByDate() {
        mAdapter.removeAllModel();
        AppointmentTable appointmentTable = new AppointmentTable(getContext());
        appointmentTable.setFilter(idxSelectedType, idWorkLocation, dateFrom, dateTo, notes, archived);
        mAdapter.addModels(appointmentTable.getRecords(date));
        mAdapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }

    private void EventClass() {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                synchronize();
                LoadData();
            }
        });

        btnAdd.setOnClickListener(view -> {

            if(Global.ReadOnlyMode()){
                ShowDialog.warningDialog(thisActivity, getString(R.string.title_app), getString(R.string.grace_period_eror));
                return;
            }else {
                Intent s = new Intent(thisActivity, CalendarInputActivity.class);
                s.putExtra("uuid", "");
                s.putExtra("appointment_date", Global.getDateFormated(date) + " " + Global.serverNowTimeFormated());
                s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(s, 1);
            }
        });


        txtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        calendarView.setOnDayClickListener(eventDay ->{
            List<Calendar> hightligt = new ArrayList<>();
            hightligt.add(eventDay.getCalendar());
            calendarView.setHighlightedDays(hightligt);

            btnNow.setImageDrawable(null);
            date = eventDay.getCalendar().getTimeInMillis();
            String dateString;
            String dateNowString = Global.serverNow();
            long dateNow = Global.serverNowLong();
            dateString = Global.getDateFormated(date);
            btnNow.setImageBitmap(Global.textAsBitmap(String.valueOf(Global.getHari(dateNow)) , 30, Color.WHITE));
            if (Global.isBeforeToday(date)){
                btnAdd.setVisibility(View.GONE);
                btnNow.setVisibility(View.VISIBLE);
            }else{
                btnAdd.setVisibility(View.VISIBLE);
                btnNow.setVisibility(View.VISIBLE);
            }

            if (dateString.equals(dateNowString)){
                btnNow.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
            }else{
                btnNow.setVisibility(View.VISIBLE);
            }


            LoadDataByDate();
        });

        btnNow.setOnClickListener(view -> {
            long dateNow = Global.serverNowWithoutTimeLong();
            List<Calendar> hightligt = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateNow);

            date = calendar.getTimeInMillis();
            hightligt.add(calendar);
            calendarView.setHighlightedDays(hightligt);
            try {
                calendarView.setDate(calendar);
            } catch (OutOfDateRangeException e) {

            }
            btnNow.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
            LoadDataByDate();
        });

//        calendarView.setOnDateChangeListener((calendarView1, i, i1, i2) -> {
//
//            btnNow.setImageDrawable(null);
//            String dateString = String.valueOf(i2)+"/"+String.valueOf(i1+1)+"/"+String.valueOf(i);
//            String dateNow = Global.serverNow();
//            date = Global.getMillisDate(dateString);
//            dateString = Global.getDateFormated(date);
//            btnNow.setImageBitmap(Global.textAsBitmap(String.valueOf(i2) , 30, Color.WHITE));
//            if (dateString.equals(dateNow)){
//                btnNow.setVisibility(View.GONE);
//            }else{
//                btnNow.setVisibility(View.VISIBLE);
//            }
//            LoadDataByDate();
//        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extra = data.getExtras();
                    idxSelectedType = extra.getInt("idxSelectedType");
                    idWorkLocation = extra.getInt("work_location");
                    dateFrom = extra.getLong("date_from");
                    dateTo = extra.getLong("date_to");
                    notes = extra.getString("notes");
                    archived = extra.getBoolean("archived");
                }
                break;
        }
        LoadData();
    }

    //hilangkan pilihan menu sync
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(2).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}

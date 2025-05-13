package com.orion.pasienqu_2.activities.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class FilterCalendarActivity extends CustomAppCompatActivity {
    private TextInputEditText txtNotes, txtDateFrom, txtDateTo;
    private AutoCompleteTextView spinDates;
    private AutoCompleteTextView spinWorkLocation;
    private List<String>listStringWorkLocation;
    private List<String>listIdWorkLocation;
    private CheckBox chbArchived;
    private ConstraintLayout clDates;

    private Button btnFilter, btnReset;
    private List<String> listStringFilterDateCalendar;
    private int idWorkLocation;
    private int idxSelectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_calendar);
        setTitle(getString(R.string.filter_data));
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }


    private void CreateView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtNotes = (TextInputEditText) findViewById(R.id.txtNotes);
        txtDateFrom = (TextInputEditText) findViewById(R.id.txtDateFrom);
        txtDateTo = (TextInputEditText) findViewById(R.id.txtDateTo);
        spinDates = (AutoCompleteTextView) findViewById(R.id.spinDates);
        spinWorkLocation = (AutoCompleteTextView) findViewById(R.id.spinWorkLocation);
        chbArchived = (CheckBox) findViewById(R.id.chbArchived);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnReset = (Button) findViewById(R.id.btnReset);
        clDates = (ConstraintLayout) findViewById(R.id.clDates);
    }

    private void InitClass() {
        Global.setEnabledClickAutoCompleteText(spinWorkLocation, false);
        Global.setEnabledClickAutoCompleteText(spinDates, false);
        Global.setEnabledClickText(txtDateTo, false);
        Global.setEnabledClickText(txtDateFrom, false);

        listStringWorkLocation = ListValue.list_work_location(getApplicationContext());
        listIdWorkLocation = ListValue.list_id_work_location(getApplicationContext());

        String[] mStringArrayWorkLocation = new String[listStringWorkLocation.size()];
        mStringArrayWorkLocation = listStringWorkLocation.toArray(mStringArrayWorkLocation);
        CustomAutoCompleteAdapter workLocationAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArrayWorkLocation);
        spinWorkLocation.setAdapter(workLocationAdapter);
        idWorkLocation = 0;

        listStringFilterDateCalendar = ListValue.list_filter_calendar_date(getApplicationContext());

        String[] mStringArray = new String[listStringFilterDateCalendar.size()];
        mStringArray = listStringFilterDateCalendar.toArray(mStringArray);

        CustomAutoCompleteAdapter dateAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                mStringArray);
//        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDates.setAdapter(dateAdapter);
        idxSelectedType = 0;

    }

    private void LoadData(){
        Bundle extra = this.getIntent().getExtras();

//        uuid = extra.getString("uuid");
        idxSelectedType = extra.getInt("idxSelectedType");
        spinDates.setText(listStringFilterDateCalendar.get(idxSelectedType));
        txtDateFrom.setText(Global.getDateTimeFormated(extra.getLong("date_from")));
        txtDateTo.setText(Global.getDateTimeFormated(extra.getLong("date_to")));
        txtNotes.setText(extra.getString("notes"));
        idWorkLocation = extra.getInt("work_location");
        for (int i = 0; i < listIdWorkLocation.size(); i++){
            if (listIdWorkLocation.get(i).equals(String.valueOf(idWorkLocation))){
                spinWorkLocation.setText(listStringWorkLocation.get(i));
            }
        }

        if (idxSelectedType == 4){
            clDates.setVisibility(View.VISIBLE);
        }else{
            clDates.setVisibility(View.GONE);
        }
        chbArchived.setChecked(extra.getBoolean("archived"));
    }

    private void EventClass() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
                startFilter();
            }
        });


        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFilter();
            }
        });
        spinWorkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinWorkLocation.showDropDown();
            }
        });

        spinWorkLocation.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                idWorkLocation = Integer.parseInt(listIdWorkLocation.get(position));
            }
        });



        spinDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinDates.showDropDown();
            }
        });

        spinDates.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                if (position == 4){
                    clDates.setVisibility(View.VISIBLE);
                }else{
                    clDates.setVisibility(View.GONE);
                }
                idxSelectedType = position;
            }
        });

        txtDateTo.setOnClickListener(view -> {
            Global.dtpTimeClick(FilterCalendarActivity.this, txtDateTo, view);
        });


        txtDateFrom.setOnClickListener(view -> {
            Global.dtpTimeClick(FilterCalendarActivity.this, txtDateFrom, view);
        });
    }

    private void resetFilter() {
        txtNotes.setText("");
        spinDates.setText(getString(R.string.filter_calendar_all_dates));
        idxSelectedType = 3;
        idWorkLocation = 0;
        txtDateFrom.setText(Global.serverNowWithTime());
        txtDateTo.setText(Global.serverNowWithTime());
        chbArchived.setChecked(false);

        clDates.setVisibility(View.GONE);
    }


    public void startFilter() {
        Intent intent = FilterCalendarActivity.this.getIntent();
        intent.putExtra("date_type", idxSelectedType);
        intent.putExtra("date_from", Global.getMillisDateTime(txtDateFrom.getText().toString()));
        intent.putExtra("date_to", Global.getMillisDateTime(txtDateTo.getText().toString()));
        intent.putExtra("notes", txtNotes.getText().toString().trim());
        intent.putExtra("work_location", idWorkLocation);
        intent.putExtra("archived", chbArchived.isChecked());
        intent.putExtra("idxSelectedType", idxSelectedType);

        setResult(RESULT_OK, intent);
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
}
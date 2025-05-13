package com.orion.pasienqu_2.activities.record;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.models.FilterModel;
import com.orion.pasienqu_2.models.LovCheckModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterRecordActivity extends CustomAppCompatActivity {
    private TextInputEditText txtRecordFrom, txtRecordto, txtDiagnosa;
    private Spinner spinDate;
    private CheckBox chbArchived;
    private Button btnFilter, btnReset, btnResetDate;
    private View customDate;
    private AutoCompleteTextView spinWorkLocation, spinpatientType;
    private List<String>listStringWorkLocation;
    private List<String>listIdWorkLocation;
    private int idWorkLocation, patientTypeId;
    private List<String> listStringGroupPatient;
    private List<String> listIdGroupPatient;
    public List<FilterModel> SelectedFilter = new ArrayList<>();
    public List<FilterModel> SelectedFilterTemp = new ArrayList<>();
    private FilterModel filterModel;
    private ArrayList<FilterModel> listTemp = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_record);
        setTitle(getString(R.string.filter_data));
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void CreateView(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtRecordFrom = (TextInputEditText) findViewById(R.id.txtRecordFrom);
        txtRecordto = (TextInputEditText) findViewById(R.id.txtRecordTo);
        txtDiagnosa = (TextInputEditText) findViewById(R.id.txtDiagnosa);
        spinWorkLocation = (AutoCompleteTextView) findViewById(R.id.spinLocation);
        spinDate = (Spinner) findViewById(R.id.spinRecordDate);
        spinpatientType = (AutoCompleteTextView) findViewById(R.id.spinpatientType);

        chbArchived = (CheckBox) findViewById(R.id.chbArchived);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnResetDate = (Button) findViewById(R.id.btnResetDate);
        customDate = (View) findViewById(R.id.layoutCustomDate);

        filterModel = new FilterModel();
    }

    private void InitClass(){
        //isi record date
        ArrayAdapter adapterDate = new ArrayAdapter<>(this, R.layout.spinner_item_style,
                ListValue.list_record_date(FilterRecordActivity.this));
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDate.setAdapter(adapterDate);
        spinDate.setSelection(0);

        //isi location
        listStringWorkLocation = ListValue.list_work_location(getApplicationContext());
        listIdWorkLocation = ListValue.list_id_work_location(getApplicationContext());

        String[] mStringArrayWorkLocation = new String[listStringWorkLocation.size()];
        mStringArrayWorkLocation = listStringWorkLocation.toArray(mStringArrayWorkLocation);
        CustomAutoCompleteAdapter workLocationAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArrayWorkLocation);
        spinWorkLocation.setAdapter(workLocationAdapter);
        idWorkLocation = 0;

        //isi Patient Type
        listStringGroupPatient = ListValue.list_patient_type(this);
        listIdGroupPatient = ListValue.list_id_patient_type(this);

        String[] mStringArraypatientType = new String[listStringGroupPatient.size()];
        mStringArraypatientType = listStringGroupPatient.toArray(mStringArraypatientType);
        CustomAutoCompleteAdapter patienTypeAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArraypatientType);
        spinpatientType.setAdapter(patienTypeAdapter);
        patientTypeId = 0;

        Global.setEnabledClickText(txtRecordto, false);
        Global.setEnabledClickText(txtRecordFrom, false);


    }

    private void EventClass(){
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFilter();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilter();
                startFilter();
            }
        });

        spinDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4){
                    customDate.setVisibility(View.VISIBLE);
//                    txtRecordFrom.setText(Global.serverNow());
//                    txtRecordto.setText(Global.serverNow());
//                    addToModel(getString(R.string.custom), String.valueOf(i));
                }else {
                    customDate.setVisibility(View.GONE);
//                    addToModel(spinDate.getSelectedItem().toString(), String.valueOf(i));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
//                addToModel(spinWorkLocation.getText().toString(), String.valueOf(idWorkLocation));
            }
        });

        txtRecordFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerClick(FilterRecordActivity.this, txtRecordFrom, view);
            }
        });

        txtRecordto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerClick(FilterRecordActivity.this, txtRecordto, view);
            }
        });

        btnResetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRecordFrom.setText(Global.serverNow());
                txtRecordto.setText(Global.serverNow());
            }
        });

        spinpatientType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                patientTypeId = Integer.parseInt(listIdGroupPatient.get(position));
//                addToModel(spinpatientType.getText().toString(), String.valueOf(patientTypeId));
            }
        });

        txtDiagnosa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (!str.isEmpty()){
//                    addToModel(getString(R.string.diagnosa), str);
                }
            }
        });
    }


    private void LoadData(){
        Bundle extra = this.getIntent().getExtras();

        spinDate.setSelection(extra.getInt("idxSelectedType"));
        txtRecordFrom.setText(Global.getDateFormated(extra.getLong("date_from")));
        txtRecordto.setText(Global.getDateFormated(extra.getLong("date_to")));
        txtDiagnosa.setText(extra.getString("diagnosa"));
        idWorkLocation = extra.getInt("work_location");
        
        for (int i = 0; i < listIdWorkLocation.size(); i++){
            if (listIdWorkLocation.get(i).equals(String.valueOf(idWorkLocation))){
                spinWorkLocation.setText(listStringWorkLocation.get(i));
            }
        }

        chbArchived.setChecked(extra.getBoolean("archived"));

        patientTypeId = extra.getInt("patient_type_id");
        for (int i = 0; i < listStringGroupPatient.size(); i++){
            if (listIdGroupPatient.get(i).equals(String.valueOf(patientTypeId))){
                spinpatientType.setText(listStringGroupPatient.get(i), false);
            }
        }

        SelectedFilter = (List<FilterModel>) extra.getSerializable("SelectedFilter");
    }


    private void resetFilter() {
        spinDate.setSelection(0);
        txtRecordFrom.setText(Global.serverNow());
        txtRecordto.setText(Global.serverNow());
        txtDiagnosa.setText("");
        idWorkLocation = 0;
        chbArchived.setChecked(false);
        customDate.setVisibility(View.GONE);
        spinpatientType.setText("");
        spinWorkLocation.setText("");
        patientTypeId = 0;
        SelectedFilter.clear();
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

    private void addToModel(String name, String value, String tipe){
        listTemp.add(new FilterModel(name, value, tipe));
    }

    private void cekFilterSelected(){
        SelectedFilter.clear();
        if (!spinDate.getSelectedItem().toString().isEmpty()){
            addToModel(spinDate.getSelectedItem().toString(), String.valueOf(spinDate.getSelectedItemPosition()), JConst.tipe_filter_date);
        }
        if(!spinWorkLocation.getText().toString().isEmpty()){
            addToModel(spinWorkLocation.getText().toString(), String.valueOf(idWorkLocation), JConst.tipe_filter_work_location);
        }
        if(!spinpatientType.getText().toString().isEmpty()){
            addToModel(spinpatientType.getText().toString(), String.valueOf(patientTypeId), JConst.tipe_filter_patient_type);
        }
        if(!txtDiagnosa.getText().toString().isEmpty()){
            addToModel(getString(R.string.diagnosa)+" : "+txtDiagnosa.getText().toString(), txtDiagnosa.getText().toString(), JConst.tipe_filter_diagnosa);
        }
        SelectedFilter = listTemp;
    }


    private void startFilter() {
        Intent intent = FilterRecordActivity.this.getIntent();
        intent.putExtra("idxSelectedType", spinDate.getSelectedItemPosition());
        intent.putExtra("date_from", Global.getMillisDate(txtRecordFrom.getText().toString()));
        intent.putExtra("date_to", Global.getMillisDate(txtRecordto.getText().toString()));
        intent.putExtra("diagnosa", txtDiagnosa.getText().toString().trim());
        intent.putExtra("work_location", idWorkLocation);
        intent.putExtra("archived", chbArchived.isChecked());
        intent.putExtra("patient_type_id", patientTypeId);

        cekFilterSelected();
        intent.putExtra("SelectedFilter", (Serializable) SelectedFilter);

        setResult(RESULT_OK, intent);
        finish();
    }
}
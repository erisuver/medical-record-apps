package com.orion.pasienqu_2.activities.patient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.calendar.FilterCalendarActivity;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.models.GenderModel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class FilterPatientActivity extends CustomAppCompatActivity {
    private TextInputEditText txtAgeFrom, txtAgeTo;
    private TextInputLayout errAgeFrom, errAgeTo;
    private AutoCompleteTextView spinGender, spinpatientType;
    private CheckBox chbArchived;
    private Button btnFilter, btnReset;
    private List<String> listStringGender;
    private ArrayList<GenderModel> listGender;
    private GenderTable genderTable;
    private int genderId, patientTypeId;
    private List<String> listStringGroupPatient;
    private List<String> listIdGroupPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_patient);
        setTitle(getString(R.string.filter_data));
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }


    private void CreateView(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtAgeFrom = (TextInputEditText) findViewById(R.id.txtAgeFrom);
        txtAgeTo = (TextInputEditText) findViewById(R.id.txtAgeTo);
        spinGender = (AutoCompleteTextView) findViewById(R.id.spinGender);
        spinpatientType = (AutoCompleteTextView) findViewById(R.id.spinpatientType);
        chbArchived = (CheckBox) findViewById(R.id.chbArchived);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnReset = (Button) findViewById(R.id.btnReset);
        errAgeFrom = (TextInputLayout) findViewById(R.id.layoutAgeFrom);
        errAgeTo = (TextInputLayout) findViewById(R.id.layoutAgeTo);
    }

    private void InitClass(){
        //isi gender
        genderTable = ((JApplication) getApplicationContext()).genderTable;
        listGender = genderTable.getRecords();
        listStringGender = new ArrayList<>();
        for (int i = 0; i < listGender.size(); i++){
            listStringGender.add(listGender.get(i).getName(FilterPatientActivity.this));
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listStringGender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(genderAdapter);
        genderId = 0;

        //isi Patient Type
        listStringGroupPatient = ListValue.list_patient_type(this);
        listIdGroupPatient = ListValue.list_id_patient_type(this);

        String[] mStringArraypatientType = new String[listStringGroupPatient.size()];
        mStringArraypatientType = listStringGroupPatient.toArray(mStringArraypatientType);
        CustomAutoCompleteAdapter patienTypeAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArraypatientType);
        spinpatientType.setAdapter(patienTypeAdapter);
        patientTypeId = 0;
    }

    private void EventClass(){
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
                if (isValid()){
                    startFilter();
                }
            }
        });
        spinGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinGender.showDropDown();
            }
        });

        spinGender.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);

                for (int i = 0; i < listStringGender.toArray().length; i++) {
                    if (listStringGender.get(i).equals(selection)) {
                        genderId = listGender.get(i).getId();
                        break;
                    }
                }
            }
        });

        spinpatientType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                patientTypeId = Integer.parseInt(listIdGroupPatient.get(position));
            }
        });
    }

    private void resetFilter(){
        txtAgeFrom.setText("");
        txtAgeTo.setText("");
        spinGender.setText("");
        genderId = 0;
        chbArchived.setChecked(false);
        spinpatientType.setText("");
        patientTypeId = 0;
    }

    private void LoadData(){
        Bundle extra = this.getIntent().getExtras();
        txtAgeFrom.setText(extra.getString("age_from"));
        txtAgeTo.setText(extra.getString("age_to"));
        chbArchived.setChecked(extra.getBoolean("archived"));

        genderId = extra.getInt("gender_id");
        for (int i = 0; i < listGender.size(); i++){
            if (listGender.get(i).getId() == genderId) {
                spinGender.setText(listStringGender.get(i), false);
            }
        }


        patientTypeId = extra.getInt("patient_type_id");
        for (int i = 0; i < listStringGroupPatient.size(); i++){
            if (listIdGroupPatient.get(i).equals(String.valueOf(patientTypeId))){
                spinpatientType.setText(listStringGroupPatient.get(i), false);
            }
        }
    }

    private boolean isValid(){
        int ageFrom = 0, ageTo = 0;
        if (!TextUtils.isEmpty(txtAgeFrom.getText())) {
            ageFrom = Integer.parseInt(txtAgeFrom.getText().toString());
        }
        if (!TextUtils.isEmpty(txtAgeTo.getText())) {
            ageTo = Integer.parseInt(txtAgeTo.getText().toString());
        }

        if (TextUtils.isEmpty(txtAgeFrom.getText()) && !TextUtils.isEmpty(txtAgeTo.getText())) {
            errAgeFrom.setError(getString(R.string.patient_filter_age_error));
            return false;
        }else{
            errAgeFrom.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtAgeTo.getText()) && !TextUtils.isEmpty(txtAgeFrom.getText())) {
            errAgeTo.setError(getString(R.string.patient_filter_age_error2));
            return false;
        }else{
            errAgeTo.setErrorEnabled(false);
        }

        if (ageFrom > ageTo){
            errAgeFrom.setError(getString(R.string.patient_filter_age_error3));
            errAgeTo.setError(getString(R.string.patient_filter_age_error3));
            return false;
        }else {
            errAgeTo.setErrorEnabled(false);
            errAgeFrom.setErrorEnabled(false);
        }

        return true;
    }



    public void startFilter() {
        Intent intent = FilterPatientActivity.this.getIntent();
        intent.putExtra("age_from", txtAgeFrom.getText().toString());
        intent.putExtra("age_to", txtAgeTo.getText().toString());
        intent.putExtra("gender_id", genderId);
        intent.putExtra("archived", chbArchived.isChecked());
        intent.putExtra("patient_type_id", patientTypeId);
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


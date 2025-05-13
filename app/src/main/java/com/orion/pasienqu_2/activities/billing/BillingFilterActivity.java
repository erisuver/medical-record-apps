package com.orion.pasienqu_2.activities.billing;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.billing_template.FilterBillingActivity;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

public class BillingFilterActivity extends CustomAppCompatActivity {
    private TextInputEditText txtBillingFrom, txtBillingTo, txtNotes;
    private Spinner spinBillingDate;
    private CheckBox chbArchived;
    private Button btnFilter, btnReset, btnResetDate;
    private View customDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_billing);
        setTitle(getString(R.string.filter_data));
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void CreateView(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtBillingFrom = (TextInputEditText) findViewById(R.id.txtBillingFrom);
        txtBillingTo = (TextInputEditText) findViewById(R.id.txtBillingTo);
        txtNotes = (TextInputEditText) findViewById(R.id.txtNotes);
        spinBillingDate = (Spinner) findViewById(R.id.spinBillingDate);

        chbArchived = (CheckBox) findViewById(R.id.chbArchived);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnResetDate = (Button) findViewById(R.id.btnResetDate);
        customDate = (View) findViewById(R.id.layoutCustomDate);
    }

    private void InitClass(){
        //isi Billing date
        ArrayAdapter adapterDate = new ArrayAdapter<>(this, R.layout.spinner_item_style,
                ListValue.list_record_date(BillingFilterActivity.this));
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBillingDate.setAdapter(adapterDate);
        spinBillingDate.setSelection(4);

        Global.setEnabledClickText(txtBillingFrom, false);
        Global.setEnabledClickText(txtBillingTo, false);

        chbArchived.setChecked(false);
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

        spinBillingDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 4){
                    customDate.setVisibility(View.VISIBLE);
//                    txtBillingFrom.setText(Global.serverNow());
//                    txtBillingTo.setText(Global.serverNow());
                }else {
                    customDate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtBillingFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerClick(BillingFilterActivity.this, txtBillingFrom, view);
            }
        });

        txtBillingTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerClick(BillingFilterActivity.this, txtBillingTo, view);
            }
        });

        btnResetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtBillingFrom.setText(Global.serverNow());
                txtBillingTo.setText(Global.serverNow());
            }
        });

    }

    private void LoadData(){
        Bundle extra = this.getIntent().getExtras();
        chbArchived.setChecked(extra.getBoolean("archived"));
        txtNotes.setText(extra.getString("notes"));
        spinBillingDate.setSelection(extra.getInt("date_idx"));
        txtBillingFrom.setText(Global.getDateFormated(extra.getLong("date_from")));
        txtBillingTo.setText(Global.getDateFormated(extra.getLong("date_to")));

    }


    private void resetFilter() {
        chbArchived.setChecked(false);
        txtNotes.setText("");
        spinBillingDate.setSelection(3);
        txtBillingFrom.setText(Global.serverNow());
        txtBillingTo.setText(Global.serverNow());
        customDate.setVisibility(View.GONE);
    }


    public void startFilter() {
        Intent intent = BillingFilterActivity.this.getIntent();
        intent.putExtra("archived", chbArchived.isChecked());
        intent.putExtra("notes", txtNotes.getText().toString());
        intent.putExtra("date_from", Global.getMillisDate(txtBillingFrom.getText().toString()));
        intent.putExtra("date_to", Global.getMillisDate(txtBillingTo.getText().toString()));
        intent.putExtra("date_idx", spinBillingDate.getSelectedItemPosition());

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
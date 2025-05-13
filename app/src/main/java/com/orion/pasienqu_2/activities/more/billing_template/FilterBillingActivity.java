package com.orion.pasienqu_2.activities.more.billing_template;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.note_template.FilterNoteTemplateActivity;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

public class FilterBillingActivity extends CustomAppCompatActivity {
    private CheckBox chbArchived;
    private Button btnFilter, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_template);
        setTitle(getString(R.string.filter_data));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }
    private void CreateView() {
        chbArchived = (CheckBox) findViewById(R.id.chbArchived);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnReset = (Button) findViewById(R.id.btnReset);
    }

    private void InitClass() {
        chbArchived.setChecked(false);
    }

    private void LoadData(){
        Bundle extra = this.getIntent().getExtras();
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

    }

    private void resetFilter() {
        chbArchived.setChecked(false);
    }



    public void startFilter() {
        Intent intent = FilterBillingActivity.this.getIntent();
        intent.putExtra("archived", chbArchived.isChecked());

        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

        }
        return true;
    }
}

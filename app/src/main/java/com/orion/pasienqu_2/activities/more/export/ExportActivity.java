package com.orion.pasienqu_2.activities.more.export;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;

public class ExportActivity extends CustomAppCompatActivity {
    private Button btnExport, btnCancel;
    private CheckBox chbAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.export));
        CreateView();
        InitClass();
        EventClass();

    }

    private void CreateView(){
        chbAgree = (CheckBox) findViewById(R.id.chbAgree);
        btnExport = (Button) findViewById(R.id.btnExport);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    private void InitClass(){
        chbAgree.setChecked(false);
    }

    private void EventClass(){
        chbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!chbAgree.isChecked()){
                    btnExport.setEnabled(false);
                    btnExport.setBackgroundColor(Color.parseColor("#E0E0E0"));
                }else{
                    btnExport.setEnabled(true);
                    btnExport.setBackgroundColor(Color.parseColor("#FF005EB8"));
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                export();
            }
        });

    }

    private void export(){
        if (!Global.CheckConnectionInternet(ExportActivity.this)) {
            Toast.makeText(ExportActivity.this, getString(R.string.must_be_online), Toast.LENGTH_SHORT).show();
            return;
        }else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            };
            //erik tutup odoo 040325
//            GlobalOdoo.exportExternal(ExportActivity.this, getApplicationContext(), runnable);
            startActivity(new Intent(ExportActivity.this, ExportSuccessActivity.class)
                    .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
package com.orion.pasienqu_2.activities.more.delete_archive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;

public class DeleteArchive extends AppCompatActivity {
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_archive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_delete_archive));
        CreateView();
        InitClass();
        EventClass();

    }

    private void CreateView(){
        btnDelete = (Button) findViewById(R.id.btnDelete);
    }

    private void InitClass(){

    }

    private void EventClass(){
        btnDelete.setOnClickListener(view -> {
            Runnable runDelete = new Runnable() {
                @Override
                public void run() {
                    GlobalTable globalTable = new GlobalTable(DeleteArchive.this);
                    globalTable.deleteAllArchive();

                    ProgressDialog loading = new ProgressDialog(DeleteArchive.this);
                    loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    loading.setCancelable(false);
                    loading.setMessage("Deleting...");
                    loading.show();

                    new CountDownTimer(1000, 500) {
                        public void onTick(long millisUntilFinished) {
                            // You don't need anything here
                        }

                        public void onFinish() {
                            loading.dismiss();
                            Snackbar.make(view, "Delete Success", Snackbar.LENGTH_SHORT).show();
                        }
                    }.start();

                }
            };

            ShowDialog.confirmDialog(this, getString(R.string.title_delete_archive), getString(R.string.inform_delete_all_archive), runDelete);
        });
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
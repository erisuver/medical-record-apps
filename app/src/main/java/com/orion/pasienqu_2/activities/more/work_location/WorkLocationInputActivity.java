package com.orion.pasienqu_2.activities.more.work_location;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateInputActivity;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.UUID;

public class WorkLocationInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtLocation, txtRemarks;
    private TextInputLayout errName, errLocation;
    private Button btnSave, btnCancel;
    private WorkLocationTable workLocationTable;
    private WorkLocationModel workLocationModel;
    private int idLocation;
    private String mode = "";
    private String uuid;
    private Menu menu;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_location_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        txtLocation = (TextInputEditText) findViewById(R.id.txtLocation);
        txtRemarks = (TextInputEditText) findViewById(R.id.txtRemarks);
        errName = (TextInputLayout) findViewById(R.id.layoutName);
        errLocation = (TextInputLayout) findViewById(R.id.layoutLocation);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        workLocationTable = ((JApplication) getApplicationContext()).workLocationTable;
        workLocationModel = new WorkLocationModel();
        idLocation = 0;
        uuid = "";
    }

    private void InitClass(){
        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        if (uuid.equals("")){
            mode   = "add";
        }else{
            mode   = "edit";
            loadData();
        }

        this.setTitleInput();
    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if(saveForm()) {
//                    syncUpData();
                    onBackPressed();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadData(){
        WorkLocationModel dataLocation = workLocationTable.getDataByUuid(uuid);
        idLocation = dataLocation.getId();
        txtName.setText(dataLocation.getName());
        txtLocation.setText(dataLocation.getLocation());
        txtRemarks.setText(dataLocation.getRemarks());
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
        }else{
            errName.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtLocation.getText())) {
            errLocation.setError(getString(R.string.error_location));
        }else{
            errLocation.setErrorEnabled(false);
        }
        return true;
    }

    private boolean saveForm() {
        if (this.isValid()) {
            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }
            String name = txtName.getText().toString().trim();
            String location = txtLocation.getText().toString().trim();
            String remarks = txtRemarks.getText().toString().trim();

            switch(this.mode.toString()){
                case "add":{
                    WorkLocationModel newData = new WorkLocationModel(0, uuid, name, location, remarks);
                    if(!workLocationTable.insert(newData, true)){
                        return false;
                    }
                    break;
                }
                case "edit":{
                    WorkLocationModel dataLocation = workLocationTable.getDataByUuid(uuid);

                    dataLocation.setName(name);
                    dataLocation.setLocation(location);
                    dataLocation.setRemarks(remarks);
                    if(!workLocationTable.update(dataLocation)){
                        return false;
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.work_location)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.work_location)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_work_location", uuid, "pasienqu.work.location");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(WorkLocationInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.work_location)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_work_location", uuid, "pasienqu.work.location");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(WorkLocationInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.work_location)), runUnarchive);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_archive_unarchive, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        if(this.mode.equals("edit")) {
            GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
            boolean isArchive = globalTable.isArchived("pasienqu_work_location", uuid);
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        return true;
    }

    private void syncUpData(){
        if(Global.CheckConnectionInternet(WorkLocationInputActivity.this)){
            SyncUp.sync_all(WorkLocationInputActivity.this, getApplicationContext(), ()->{});
        }
    }



}
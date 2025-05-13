package com.orion.pasienqu_2.activities.satu_sehat.location;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.satu_sehat.MedicalSatuSehatActivity;
import com.orion.pasienqu_2.activities.satu_sehat.PractitionerActivity;
import com.orion.pasienqu_2.data_table.PractitionerTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.MedicalSatuSehatModel;
import com.orion.pasienqu_2.models.PractitionerModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.util.ArrayList;
import java.util.List;

public class LocationSatuSehatActivity extends CustomAppCompatActivity {
    public LocationSatuSehatAdapter mAdapter;
    public List<WorkLocationModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    
    //nakes
    private TextInputEditText txtIHSNumber, txtNIK;
    private TextInputLayout layoutIHSNumber, layoutNIK;
    private Button btnSave;
    private ImageView imgCariNakes, imgCekNakes;
    private PractitionerModel practitionerModel;
    private PractitionerTable practitionerTable;
    private boolean isForceOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpecialLanguageSelected();
        setContentView(R.layout.activity_location_satu_sehat);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.setting));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        rcvLoad = findViewById(R.id.rcvLoad);

        btnSave = findViewById(R.id.btnSave);
        txtIHSNumber = findViewById(R.id.txtIHSNumber);
        layoutIHSNumber = findViewById(R.id.layoutIHSNumber);
        txtNIK = findViewById(R.id.txtNIK);
        layoutNIK = findViewById(R.id.layoutNIK);
        imgCariNakes = findViewById(R.id.imgCariNakes);
        imgCekNakes = findViewById(R.id.imgCekNakes);
        practitionerTable = new PractitionerTable(this);

        this.mAdapter = new LocationSatuSehatAdapter(LocationSatuSehatActivity.this, ListItems, R.layout.location_satu_sehat_list_item);
        thisActivity = LocationSatuSehatActivity.this;
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(LocationSatuSehatActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        Bundle extra = this.getIntent().getExtras();
        isForceOpen = false;
        if (extra != null) {
            isForceOpen = extra.getBoolean("isForceOpen");
        }
        ResetData();
    }

    private void ResetData(){
        txtIHSNumber.setText("");
        txtNIK.setText("");
        loadData();
    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    GlobalSatuSehat.checkTokenSatuSehat(thisActivity, () -> savePractitioner());
                }
            }
        });
        imgCariNakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidNIK()){
                    cekPractitionerByNIK();
                }
            }
        });
        imgCekNakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    cekPractitionerByID();
                }
            }
        });

        txtIHSNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Method ini dipanggil sebelum teks berubah
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Method ini dipanggil saat teks berubah
                if (practitionerModel != null) {
                    if (!TextUtils.isEmpty(practitionerModel.getNomor_ihs()) && charSequence.toString().equals(practitionerModel.getNomor_ihs())) {
                        // Jika nilai txtIHSNumber sama dengan "default"
                        imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
                    } else {
                        // Jika nilai txtIHSNumber tidak sama dengan "default"
                        imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_search_gray);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Method ini dipanggil setelah teks berubah
            }
        });
    }

    private void loadData(){
        loadDataLokasi();
        loadDataNakes();
    }

    private void loadDataLokasi(){
        mAdapter.removeAllModel();
        WorkLocationTable workLocationTable = new WorkLocationTable(thisActivity);
        mAdapter.addModels(workLocationTable.getRecords());
        mAdapter.notifyDataSetChanged();
    }

    private void loadDataNakes() {
        practitionerModel = practitionerTable.getData();
        if (practitionerModel != null) {
            txtIHSNumber.setText(practitionerModel.getNomor_ihs());
            imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
        }
    }
    
    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtIHSNumber.getText())) {
            layoutIHSNumber.setError(String.format(getString(R.string.field_is_empty), getString(R.string.ihs_number)));
            isValid = false;
        } else {
            layoutIHSNumber.setErrorEnabled(false);
        }

        int totalDataLengkap = 0;
        for (WorkLocationModel data : mAdapter.Datas) {
            if (!TextUtils.isEmpty(data.getOrganization_ihs()) && !TextUtils.isEmpty(data.getOrganization_ihs()) && !TextUtils.isEmpty(data.getOrganization_ihs())) {
                totalDataLengkap += 1;
            }
        }
        if (totalDataLengkap == 0){
            ShowDialog.infoDialog(thisActivity, getString(R.string.information), "Data Lokasi belum ada yang dilengkapi. Mohon lengkapi minimal 1 data.");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidNIK() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtNIK.getText())) {
            layoutNIK.setError(String.format(getString(R.string.field_is_empty), getString(R.string.field)));
            isValid = false;
        } else {
            layoutNIK.setErrorEnabled(false);
        }

        int totalDataLengkap = 0;
        for (WorkLocationModel data : mAdapter.Datas) {
            if (!TextUtils.isEmpty(data.getOrganization_ihs()) && !TextUtils.isEmpty(data.getOrganization_ihs()) && !TextUtils.isEmpty(data.getOrganization_ihs())) {
                totalDataLengkap += 1;
            }
        }
        if (totalDataLengkap == 0){
            ShowDialog.infoDialog(thisActivity, getString(R.string.information), "Data Lokasi belum ada yang dilengkapi. Mohon lengkapi minimal 1 data.");
            isValid = false;
        }
        return isValid;
    }

    private void cekPractitionerByNIK() {
        //validasi
        if (!Global.CheckConnectionInternet(thisActivity)) {
            ShowDialog.infoDialog(thisActivity, getString(R.string.information), getString(R.string.must_be_online));
            return;
        }

        //runnable
        Runnable runTerdaftar = new Runnable() {
            @Override
            public void run() {
                txtIHSNumber.setText(JApplication.practitionerIHS);
                imgCariNakes.setBackgroundResource(R.drawable.ic_satu_sehat_search_colored);
                imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
            }
        };
        Runnable runTidakTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgCariNakes.setBackgroundResource(R.drawable.ic_satu_sehat_search_gray);
                ShowDialog.infoDialog(thisActivity, getString(R.string.information), "IHS Number tidak ditemukan");
            }
        };
        Runnable runCheckPatientByNIK = new Runnable() {
            @Override
            public void run() {
                GlobalSatuSehat.checkPractitionerByNIK(thisActivity, runTerdaftar, runTidakTerdaftar, txtNIK.getText().toString().trim());
            }
        };

        //jalankan
        GlobalSatuSehat.checkTokenSatuSehat(thisActivity, runCheckPatientByNIK);
    }

    private void cekPractitionerByID(){
        //validasi
        if (!Global.CheckConnectionInternet(thisActivity)) {
            ShowDialog.infoDialog(thisActivity, getString(R.string.information), getString(R.string.must_be_online));
            return;
        }

        //runnable
        Runnable runTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
            }
        };
        Runnable runTidakTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgCekNakes.setBackgroundResource(R.drawable.ic_satu_sehat_search_gray);
                ShowDialog.infoDialog(thisActivity, getString(R.string.information), "IHS Number tidak ditemukan");
            }
        };
        Runnable runCheckPatientByNIK = new Runnable() {
            @Override
            public void run() {
                GlobalSatuSehat.checkPractitionerByID(thisActivity, runTerdaftar, runTidakTerdaftar, txtIHSNumber.getText().toString().trim());
            }
        };

        //jalankan
        GlobalSatuSehat.checkTokenSatuSehat(thisActivity, runCheckPatientByNIK);
    }

    private void savePractitioner(){
        Runnable runSuccess = new Runnable() {
            @Override
            public void run() {
                //lakukan save
                practitionerModel = new PractitionerModel(
                        0,
                        JApplication.getInstance().loginInformationModel.getCompanyId(),
                        txtIHSNumber.getText().toString()
                );

                practitionerTable.deleteAll();
                if (practitionerTable.insert(practitionerModel)){
                    ShowDialog.infoDialogWithRunnable(thisActivity, getString(R.string.information), getString(R.string.success_save), () -> onBackPressed());
                }else{
                    ShowDialog.infoDialog(thisActivity, getString(R.string.information), getString(R.string.failed_save));
                }
            }
        };
        Runnable runFailed = new Runnable() {
            @Override
            public void run() {
                ShowDialog.infoDialog(thisActivity, getString(R.string.information),getString(R.string.number_not_registered_or_inactive));
            }
        };

        GlobalSatuSehat.checkPractitionerByID(thisActivity, runSuccess, runFailed, txtIHSNumber.getText().toString());
    }
    

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String practitionerID = JApplication.getInstance().globalTable.getPractitionerID();
        if (isForceOpen && practitionerID.isEmpty()){
            finish();
        }else if (isForceOpen){
            startActivity(new Intent(this, MedicalSatuSehatActivity.class)
                    .setFlags(FLAG_ACTIVITY_CLEAR_TOP));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_help:
                ShowDialog.showDialogHelp(thisActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting_satu_sehat, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }
}

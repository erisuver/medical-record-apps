package com.orion.pasienqu_2.activities.satu_sehat;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.activities.satu_sehat.location.LocationSatuSehatInputActivity;
import com.orion.pasienqu_2.data_table.PractitionerTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.PractitionerModel;

public class PractitionerActivity extends AppCompatActivity {
    private TextInputEditText txtIHSNumber, txtNIK;
    private TextInputLayout layoutIHSNumber, layoutNIK;
    private Button btnSave;
    private ImageView imgSatuSehat;
    private PractitionerModel practitionerModel;
    private PractitionerTable practitionerTable;
    private boolean isForceOpen;
    private AppCompatActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practitioner);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_practitioner));
        CreateView();
        InitClass();
        EventClass();
        LoadData();
    }

    private void LoadData() {
        practitionerModel = practitionerTable.getData();
        if (practitionerModel != null) {
            txtIHSNumber.setText(practitionerModel.getNomor_ihs());
        }
    }

    private void CreateView() {
        btnSave = findViewById(R.id.btnSave);
        txtIHSNumber = findViewById(R.id.txtIHSNumber);
        layoutIHSNumber = findViewById(R.id.layoutIHSNumber);
        txtNIK = findViewById(R.id.txtNIK);
        layoutNIK = findViewById(R.id.layoutNIK);
        imgSatuSehat = findViewById(R.id.imgSatuSehat);
        practitionerTable = new PractitionerTable(this);
        thisActivity = PractitionerActivity.this;
    }

    private void InitClass() {
        txtIHSNumber.setText("");
        txtNIK.setText("");

        Bundle extra = this.getIntent().getExtras();
        isForceOpen = false;
        if (extra != null) {
            isForceOpen = extra.getBoolean("isForceOpen");
        }
    }

    private void EventClass() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    GlobalSatuSehat.checkTokenSatuSehat(PractitionerActivity.this, () -> savePractitioner());
                }
            }
        });
        imgSatuSehat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidNIK()){
                    cekPractitionerByNIK();
                }
            }
        });

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
                    ShowDialog.infoDialogWithRunnable(PractitionerActivity.this, getString(R.string.information), getString(R.string.success_save), () -> onBackPressed());
                }else{
                    ShowDialog.infoDialog(PractitionerActivity.this, getString(R.string.information), getString(R.string.failed_save));
                }
            }
        };
        Runnable runFailed = new Runnable() {
            @Override
            public void run() {
                ShowDialog.infoDialog(PractitionerActivity.this, getString(R.string.information),getString(R.string.number_not_registered_or_inactive));
            }
        };

        GlobalSatuSehat.checkPractitionerByID(PractitionerActivity.this, runSuccess, runFailed, txtIHSNumber.getText().toString());
    }


    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtIHSNumber.getText())) {
            layoutIHSNumber.setError(String.format(getString(R.string.field_is_empty), getString(R.string.ihs_number)));
            isValid = false;
        } else {
            layoutIHSNumber.setErrorEnabled(false);
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
                imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
            }
        };
        Runnable runTidakTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_gray);
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
}
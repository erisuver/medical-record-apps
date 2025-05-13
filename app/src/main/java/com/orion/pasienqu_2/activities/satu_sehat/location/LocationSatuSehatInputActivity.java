package com.orion.pasienqu_2.activities.satu_sehat.location;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.satu_sehat.PractitionerActivity;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.WorkLocationModel;

public class LocationSatuSehatInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtLocation, txtOrganizationID, txtClientKey, txtSecretKey;
    private TextInputLayout errName, errLocation, errOrganizationID, errClientKey, errSecretKey;
    private Button btnSave, btnCancel;
    private ImageButton btnHelp;
    private WorkLocationTable workLocationTable;
    private WorkLocationModel workLocationModel;
    private String uuid;
    private long mLastClickTime = 0;
    private WorkLocationModel dataLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpecialLanguageSelected();
        setContentView(R.layout.activity_location_satu_sehat_input);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        txtName = findViewById(R.id.txtName);
        txtLocation = findViewById(R.id.txtLocation);
        txtOrganizationID = findViewById(R.id.txtOrganizationID);
        txtClientKey = findViewById(R.id.txtClientKey);
        txtSecretKey = findViewById(R.id.txtSecretKey);
        errName = findViewById(R.id.layoutName);
        errLocation = findViewById(R.id.layoutLocation);
        errOrganizationID = findViewById(R.id.layoutOrganizationID);
        errClientKey = findViewById(R.id.layoutClientKey);
        errSecretKey = findViewById(R.id.layoutSecretKey);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnHelp = findViewById(R.id.btnHelp);
        workLocationTable = ((JApplication) getApplicationContext()).workLocationTable;
        workLocationModel = new WorkLocationModel();
        uuid = "";
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        loadData();

        this.setTitleInput();
    }

    private void EventClass() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                if (!Global.CheckConnectionInternet(getApplicationContext())) {
                    ShowDialog.infoDialog(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                    return;
                }

                saveLocation();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://satusehat.kemkes.go.id/platform/login";
                String deskripsiText = getString(R.string.organization_id_info);

                // Tentukan indeks di mana teks "Disini" berada
                int startIndex = deskripsiText.indexOf(getString(R.string.here));
                int endIndex = startIndex + getString(R.string.here).length();

                // Buat ClickableSpan untuk menangani klik pada teks "Disini"
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        // Tangani klik di sini, buka URL
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                };

                // Terapkan ClickableSpan ke SpannableString hanya untuk teks "Disini"
                SpannableString spannableString = new SpannableString(deskripsiText);
                spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Tampilkan tautan di dalam dialog informasi
                Global.infoDialogLink(LocationSatuSehatInputActivity.this, getString(R.string.information), spannableString);
            }
        });
    }

    private void loadData() {
        dataLocation = workLocationTable.getDataByUuid(uuid);
        txtName.setText(dataLocation.getName());
        txtLocation.setText(dataLocation.getLocation());
        txtOrganizationID.setText(dataLocation.getOrganization_ihs());
        txtClientKey.setText(dataLocation.getClient_id());
        txtSecretKey.setText(dataLocation.getClient_secret());
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtOrganizationID.getText())) {
//            errOrganizationID.setError(String.format(getString(R.string.field_is_empty), getString(R.string.organization_id)));
            isValid = false;
//        } else if (dataLocation.getOrganization_ihs() != null && dataLocation.getOrganization_ihs().equals(txtOrganizationID.getText().toString())){
//            errOrganizationID.setError("Organization ID pada lokasi saat ini sudah terdaftar.");
//            isValid = false;
        }else {
            errOrganizationID.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtClientKey.getText())) {
//            errClientKey.setError(String.format(getString(R.string.field_is_empty), getString(R.string.field)));
            isValid = false;
        }else {
            errClientKey.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtSecretKey.getText())) {
//            errSecretKey.setError(String.format(getString(R.string.field_is_empty), getString(R.string.field)));
            isValid = false;
        }else {
            errSecretKey.setErrorEnabled(false);
        }

        return isValid;
    }

    private void saveLocation() {
        /**
         * Langakah save sbb :
         * Cek ke satu sehat apakah client id dan secret valid.
         * Cek ke satu sehat dengan Get Organization by ID IHS dari klinik yang sudah didaftarkan.
         * jika ID valid maka lanjut cek lokasi
         * jika sudah terdaftar maka llangsung  simpan ID Organisasi dan ID lokasi ke database.
         * jika belum maka create location dis satu sehat
         * jika create location sukses maka simpan ID Organisasi dan ID lokasi ke database.
         **/
        Runnable runSuccess = new Runnable() {
            @Override
            public void run() {
                //lakukan update
                dataLocation.setOrganization_ihs(txtOrganizationID.getText().toString());
                dataLocation.setLocation_ihs(JApplication.locationIHS);
                dataLocation.setClient_id(txtClientKey.getText().toString());
                dataLocation.setClient_secret(txtSecretKey.getText().toString());
                if (workLocationTable.update(dataLocation)) {
                    ShowDialog.infoDialogWithRunnable(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.success_save), () -> onBackPressed());
                } else {
                    ShowDialog.infoDialog(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.failed_save));
                }
            }
        };

        Runnable runFailed = new Runnable() {
            @Override
            public void run() {
                ShowDialog.infoDialog(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.not_registered_organization_id));
            }
        };

        Runnable runSaveLocation = new Runnable() {
            @Override
            public void run() {
                GlobalSatuSehat.createLocation(LocationSatuSehatInputActivity.this, runSuccess, runFailed, txtOrganizationID.getText().toString(), dataLocation);
            }
        };

        Runnable runCheckLocation = new Runnable() {
            @Override
            public void run() {
//                    Log.i("TAG", "cukup sampai sini");
//                    runSuccess.run();
                GlobalSatuSehat.checkLocationOrganizationID(LocationSatuSehatInputActivity.this, runSuccess, runSaveLocation, txtOrganizationID.getText().toString(), JConst.DEFAULT_LOCATION_SATUSEHAT+", "+dataLocation.getName());
            }
        };

        Runnable runCheckOrganization = new Runnable() {
            @Override
            public void run() {
                    GlobalSatuSehat.checkOrganizationByID(LocationSatuSehatInputActivity.this, runCheckLocation, runFailed, txtOrganizationID.getText().toString());
                }
        };

        if (this.isValid()) {
            GlobalSatuSehat.checkClient(LocationSatuSehatInputActivity.this, runCheckOrganization, runFailed, txtClientKey.getText().toString(), txtSecretKey.getText().toString());
        }else{
            //isi
            String organizationID = txtOrganizationID.getText().toString().trim();
            String clientKey = txtClientKey.getText().toString().trim();
            String secretKey = txtSecretKey.getText().toString().trim();

            //lakukan update
            dataLocation.setOrganization_ihs(organizationID);
            dataLocation.setLocation_ihs(TextUtils.isEmpty(organizationID) ? "" : dataLocation.getLocation_ihs());
            dataLocation.setClient_id(clientKey);
            dataLocation.setClient_secret(secretKey);
            if (TextUtils.isEmpty(clientKey) || TextUtils.isEmpty(secretKey)){
                dataLocation.setLast_generate_token(0);
                dataLocation.setToken("");
            }
            if (workLocationTable.update(dataLocation)) {
                ShowDialog.infoDialogWithRunnable(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.success_save), () -> onBackPressed());
            } else {
                ShowDialog.infoDialog(LocationSatuSehatInputActivity.this, getString(R.string.information), getString(R.string.failed_save));
            }
        }
    }

    private void setTitleInput() {
        this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.work_location)));
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
package com.orion.pasienqu_2.activities.more.google_drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.BackupDriveTable;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.BackupDriveModel;
import com.orion.pasienqu_2.models.RadioCheckModel;

import java.util.Collections;
import java.util.List;

public class BackupDriveActivity extends CustomAppCompatActivity {
    private TextView txtUser, tvSize;
    private Button btnUpload;
    private SwitchCompat swcFile;
    private RadioGroup rgReminder;
    private RadioButton radioButton;
    private String reminderValue;
    private ImageView imgInfo;

    private final String TAG = "pasienqu-gdrive";
    private final int REQUEST_CODE_SIGN_IN = 0;
    private final String DATABASE_PATH = JApplication.getInstance().lokasi_db;

    public static DriveServiceHelper driveServiceHelper;
    private GoogleSignInClient client;
    private boolean isLogin = false;
    private GoogleSignInOptions signInOptions;

    public static BackupDriveActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_backup_google_drive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Backup");
        CreateView();
        InitClass();
        EventClass();
    }


    private void CreateView() {
        btnUpload = findViewById(R.id.btnUpload);
        txtUser = findViewById(R.id.txtUser);
        swcFile = findViewById(R.id.swcFile);
        rgReminder = findViewById(R.id.rgReminder);
        imgInfo = findViewById(R.id.imgInfo);
        tvSize = findViewById(R.id.tvSize);

        signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();

    }

    private void InitClass() {
        //cek apakah sudah pernah login
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            updateUI(false, account);
            isLogin = false;
        } else {
            client = GoogleSignIn.getClient(this, signInOptions);
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService = new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("My Drive Tutorial").build();
            driveServiceHelper = new DriveServiceHelper(googleDriveService);

            updateUI(true, account);
            isLogin = true;
        }

        SharedPreferences prefs = getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        boolean isMasterDevice = prefs.getBoolean("master_device", true);
        boolean isIncludeFile = prefs.getBoolean("include_file", false);
        reminderValue = prefs.getString("reminder_value", "");

        swcFile.setChecked(isIncludeFile);

        //default seting reminder
        if (!reminderValue.equals("")) {
            ((RadioButton) rgReminder.getChildAt(Integer.parseInt(reminderValue) - 1)).setChecked(true);
        } else {
            //jadikan ke default (3day)
            ((RadioButton) rgReminder.getChildAt(1)).setChecked(true);
        }

        double fileSizeDB = Global.calculateFileSize(JApplication.getInstance().lokasi_db);
        if (swcFile.isChecked()){
            double fileSizeMedia = Global.calculateFolderSize(JConst.mediaLocationPath);
            double fileSize = fileSizeDB + fileSizeMedia;
            tvSize.setText(String.format("[%s MB]", fileSize));
        }else{
            tvSize.setText(String.format("[%s MB]", fileSizeDB));
        }

    }

    private void EventClass() {
        btnUpload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(this)){
                Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                return;
            }

            ProgressDialog progressDialog = Global.createProgresSpinner(BackupDriveActivity.this, getString(R.string.loading));
            progressDialog.show();
            driveServiceHelper.checkLastModified("pasienqu")
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            progressDialog.dismiss();

                            Runnable runUpload = new Runnable() {
                                @Override
                                public void run() {
                                    DriveUtil.upload(BackupDriveActivity.this);
                                }
                            };

                            long lastModifiedTimeBackup = JApplication.lastModifiedBackup;
                            long lastMedicalRecordDate = JApplication.getInstance().globalTable.getLastMedicalRecordDate();
                            StringBuilder strInfoDate = new StringBuilder();
                            if (lastMedicalRecordDate <= lastModifiedTimeBackup) {
                                strInfoDate.append(BackupDriveActivity.this.getString(R.string.warning_backup_version)).append("\n");
                                strInfoDate.append(BackupDriveActivity.this.getString(R.string.google_drive_version)).append(Global.getDateFormated(lastModifiedTimeBackup, "dd/MM/yyyy HH:mm")).append("\n");
                                strInfoDate.append(BackupDriveActivity.this.getString(R.string.device_version)).append(Global.getDateFormated(lastMedicalRecordDate, "dd/MM/yyyy HH:mm")).append("\n");

                                ShowDialog.overwriteDialog(BackupDriveActivity.this, getString(R.string.backup_your_data), strInfoDate.toString(), getString(R.string.inform_confirm_backup), runUpload);
                            }else{
                                ShowDialog.confirmDialog(BackupDriveActivity.this, getString(R.string.backup_your_data), getString(R.string.inform_confirm_backup), runUpload);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });

        });


        swcFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setSwitchFile();
                double fileSizeDB = Global.calculateFileSize(JApplication.getInstance().lokasi_db);
                if (isChecked){
                    double fileSizeMedia = Global.calculateFolderSize(JConst.mediaLocationPath);
                    double fileSize = fileSizeDB + fileSizeMedia;
                    tvSize.setText(String.format("[%s MB]", fileSize));
                }else{
                    tvSize.setText(String.format("[%s MB]", fileSizeDB));
                }
            }
        });

        rgReminder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                radioButton = rgReminder.findViewById(rgReminder.getCheckedRadioButtonId());
                String selected = radioButton.getText().toString();
                List<RadioCheckModel> listItem = ListValue.list_backup_reminder2(BackupDriveActivity.this);

                for (int i = 0; i < listItem.size(); i++) {
                    if (selected.equals(listItem.get(i).getLabel())) {
                        reminderValue = listItem.get(i).getPosition();
                    }
                }

                SharedPreferences.Editor editor = getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
                editor.putString("reminder_value", reminderValue);
                editor.apply();
            }
        });
    }


    private void setSwitchFile() {
        SharedPreferences.Editor editor = getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
        editor.putBoolean("include_file", swcFile.isChecked());
        editor.apply();
    }
    private void updateUI(boolean enable, GoogleSignInAccount account) {
        if (enable) {
            txtUser.setText(account.getEmail());
            btnUpload.setEnabled(true);
            btnUpload.setBackgroundColor(Color.parseColor("#FF005EB8"));
        } else {
            btnUpload.setEnabled(false);
            btnUpload.setBackgroundColor(Color.parseColor("#E0E0E0"));
            txtUser.setText("");
        }
    }

    private void signIn() {
        client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void signOut() {
        client.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(false, null);
                    }
                });
        isLogin = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    GoogleSignInAccount lastAccount = GoogleSignIn.getLastSignedInAccount(this);
                    handleSignInIntent(data);
                    isLogin = true;
                    updateUI(true, lastAccount);
                    String AccountName = lastAccount.getDisplayName();
                    String Accountid = lastAccount.getId();
                    Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.welcome_user), AccountName), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());

                Drive googleDriveService = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("My Drive Tutorial").build();

                driveServiceHelper = new DriveServiceHelper(googleDriveService);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
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

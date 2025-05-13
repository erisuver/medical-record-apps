package com.orion.pasienqu_2.activities.more.google_drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.orion.pasienqu_2.activities.calendar.NotificationUtil;
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
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackupRestoreDrive extends CustomAppCompatActivity {
    private TextView txtUser;
    private Button btnLogin, btnUpload, btnDownload;
    private Switch swcMaster, swcFile;
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

    private RecordTable recordTable;
    private RecordFileTable recordFileTable;
    private BackupDriveTable backupDriveTable;
    private BackupDriveModel backupDriveModel;
    private long last_backup_data = 0;
    private long last_backup_media = 0;
    private long last_restore = 0;
    private boolean isRestoreSuccess = false;

    public static BackupRestoreDrive mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_backup_restore_google_drive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_backup_restore_gdrive));
        CreateView();
        InitClass();
        EventClass();
    }


    private void CreateView() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        txtUser = (TextView) findViewById(R.id.txtUser);
        swcMaster = (Switch) findViewById(R.id.swcMaster);
        swcFile = (Switch) findViewById(R.id.swcFile);
        rgReminder = (RadioGroup) findViewById(R.id.rgReminder);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);

        signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();


        recordTable = new RecordTable(this);
        recordFileTable = new RecordFileTable(this);
        backupDriveTable = new BackupDriveTable(this);
        backupDriveModel = new BackupDriveModel();
    }

    private void InitClass() {
        last_backup_data = backupDriveTable.getLastUpdateData();
        last_backup_media = backupDriveTable.getLastUpdateMedia();

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

        if (isLogin) {
            if (isMasterDevice) {
                swcMaster.setChecked(true);
            } else {
                swcMaster.setChecked(false);
            }
            setSwitch();
        }

        if (isIncludeFile) {
            swcFile.setChecked(true);
        } else {
            swcFile.setChecked(false);
        }

        //default seting reminder
        if (!reminderValue.equals("")) {
            ((RadioButton) rgReminder.getChildAt(Integer.parseInt(reminderValue) - 1)).setChecked(true);
        } else {
            //jadikan ke default (3day)
            ((RadioButton) rgReminder.getChildAt(1)).setChecked(true);
        }

    }

    private void EventClass() {
        btnLogin.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(this)){
                Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                return;
            }

            if (!isLogin) {
                signIn();
            } else {
                signOut();
            }

        });
        btnUpload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(this)){
                Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                return;
            }

            ProgressDialog progressDialog = Global.createProgresSpinner(BackupRestoreDrive.this, getString(R.string.loading));
            progressDialog.show();
            driveServiceHelper.checkLastModified("pasienqu")
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            long lastModifiedTimeBackup = JApplication.lastModifiedBackup;
                            long lastMedicalRecordDate = JApplication.getInstance().globalTable.getLastMedicalRecordDate();
                            StringBuilder strInfoDate = new StringBuilder();

                            if (lastMedicalRecordDate <= lastModifiedTimeBackup) {
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.last_backup_drive)).append(Global.getDateFormated(lastModifiedTimeBackup, "dd/MM/yyyy HH:mm")).append("\n");
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.last_medical_record)).append(Global.getDateFormated(lastMedicalRecordDate, "dd/MM/yyyy HH:mm")).append("\n");
                                strInfoDate.append("\n");
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.inform_warning_backup));
                                strInfoDate.append("\n");
                            }

                            progressDialog.dismiss();
                            Runnable runUpload = new Runnable() {
                                @Override
                                public void run() {
                                    DriveUtil.upload(BackupRestoreDrive.this);
                                }
                            };
                            ShowDialog.confirmDialog(BackupRestoreDrive.this, getString(R.string.upload), strInfoDate.toString()+getString(R.string.inform_confirm_backup), runUpload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });

//            Runnable runUpload = new Runnable() {
//                @Override
//                public void run() {
//                    DriveUtil.upload(BackupRestoreDrive.this);
//                }
//            };
//            ShowDialog.confirmDialog(this, getString(R.string.upload), getString(R.string.inform_confirm_backup), runUpload);

        });
        btnDownload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(this)){
                Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                return;
            }

            ProgressDialog progressDialog = Global.createProgresSpinner(BackupRestoreDrive.this, getString(R.string.loading));
            progressDialog.show();
            driveServiceHelper.checkLastModified("pasienqu")
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            long lastModifiedTimeBackup = JApplication.lastModifiedBackup;
                            long lastMedicalRecordDate = JApplication.getInstance().globalTable.getLastMedicalRecordDate();
                            StringBuilder strInfoDate = new StringBuilder();

                            if (lastMedicalRecordDate >= lastModifiedTimeBackup) {
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.last_backup_drive)).append(Global.getDateFormated(lastModifiedTimeBackup, "dd/MM/yyyy HH:mm")).append("\n");
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.last_medical_record)).append(Global.getDateFormated(lastMedicalRecordDate, "dd/MM/yyyy HH:mm")).append("\n");
                                strInfoDate.append("\n");
                                strInfoDate.append(BackupRestoreDrive.this.getString(R.string.inform_warning_restore));
                                strInfoDate.append("\n");
                            }

                            progressDialog.dismiss();
                            Runnable runDownload = new Runnable() {
                                @Override
                                public void run() {
                                    DriveUtil.download(BackupRestoreDrive.this);
                                }
                            };
                            ShowDialog.confirmDialog(BackupRestoreDrive.this, getString(R.string.restore), strInfoDate.toString()+getString(R.string.inform_confirm_restore), runDownload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });

//            Runnable runDownload = new Runnable() {
//                @Override
//                public void run() {
//                    DriveUtil.download(BackupRestoreDrive.this);
//                }
//            };
//            ShowDialog.confirmDialog(this, getString(R.string.restore), getString(R.string.inform_confirm_restore), runDownload);

        });

        swcMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isLogin) {
                    setSwitch();
                }
            }
        });

        swcFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSwitchFile();
            }
        });

        rgReminder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                radioButton = (RadioButton) rgReminder.findViewById(rgReminder.getCheckedRadioButtonId());
                String selected = radioButton.getText().toString();
                List<RadioCheckModel> listItem = ListValue.list_backup_reminder2(BackupRestoreDrive.this);

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

    private void setSwitch() {
        if (isLogin) {
            SharedPreferences.Editor editor = getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
            editor.putBoolean("master_device", swcMaster.isChecked());
            editor.apply();

            if (swcMaster.isChecked()) {
                btnUpload.setEnabled(true);
                btnUpload.setBackgroundColor(Color.parseColor("#FF005EB8"));
//                btnUpload.setBackgroundColor(Color.WHITE);
                btnDownload.setEnabled(false);
                btnDownload.setBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                btnUpload.setEnabled(false);
                btnUpload.setBackgroundColor(Color.parseColor("#E0E0E0"));
                btnDownload.setEnabled(true);
                btnDownload.setBackgroundColor(Color.parseColor("#FF005EB8"));
//                btnDownload.setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void setSwitchFile() {
        SharedPreferences.Editor editor = getSharedPreferences("masterDevice", Context.MODE_PRIVATE).edit();
        editor.putBoolean("include_file", swcFile.isChecked());
        editor.apply();
    }

    private void updateUI(boolean enable, GoogleSignInAccount account) {
        if (enable) {
            txtUser.setText(String.format(getString(R.string.logged_as), account.getEmail()));
            btnLogin.setText(getString(R.string.log_out));
            btnUpload.setEnabled(true);
            btnDownload.setEnabled(true);
            btnUpload.setBackgroundColor(Color.parseColor("#FF005EB8"));
            btnDownload.setBackgroundColor(Color.parseColor("#FF005EB8"));
//            btnUpload.setBackgroundColor(Color.WHITE);
//            btnDownload.setBackgroundColor(Color.WHITE);
            setSwitch();
        } else {
            btnLogin.setText(getString(R.string.login));
            btnUpload.setEnabled(false);
            btnUpload.setBackgroundColor(Color.parseColor("#E0E0E0"));
            btnDownload.setEnabled(false);
            btnDownload.setBackgroundColor(Color.parseColor("#E0E0E0"));
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
//
//    public void upload() {
//        SharedPreferences sharedPreferences = this.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
//        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);
//
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setTitle(getString(R.string.inform_upload_gdrive));
//        progressDialog.setMessage(getString(R.string.inform_please_wait));
//        progressDialog.setCancelable(false);
//        int maxProgress = 0;
//        if (isIncludeFile) {
//            maxProgress = Global.getMaxProgress(this, last_backup_media) + 1;
//            progressDialog.setMax(maxProgress);
//        } else {
//            progressDialog.setMax(1);
//        }
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.done), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                progressDialog.dismiss();//dismiss dialog
//            }
//        });
//        progressDialog.show();
//        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
//
//        //create folder dulu
//        driveServiceHelper.createFolder("PasienQu 2", this).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//                Log.d("eror DriveUtil", e.toString());
//            }
//        });
//
//        //cek dulu apakah user ingin menyertakan media
//        if (isIncludeFile) {
//            uploadWithMedia(progressDialog);
//        }
//
//        //backup database
//        int finalMaxProgress = maxProgress;
//        JApplication.getInstance().db.execSQL("VACUUM");
//        driveServiceHelper.createFileDB(DATABASE_PATH, "pasienqu", "db")
//                .addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        last_backup_data = Global.serverNowLong();
//                        backupDriveModel.setLast_backup_data(last_backup_data);
//                        backupDriveTable.update(backupDriveModel);
//
//                        if (isIncludeFile){
//                            progressDialog.setProgress(finalMaxProgress);
//                        }else{
//                            progressDialog.setProgress(1);
//                        }
////                        progressDialog.dismiss();2
////                        Toast.makeText(getApplicationContext(), getString(R.string.inform_upload_success), Toast.LENGTH_SHORT).show();
//                        showCompleteProgress(progressDialog, getString(R.string.inform_upload_success));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), getString(R.string.inform_upload_failed), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//
//    private void uploadWithMedia(ProgressDialog progressDialog) {
//        //backup media prescriotion file
//        ArrayList<RecordModel> ListMediaPrescription = recordTable.getRecordPrescriptionFile(last_backup_media);
//
//        int progres = 0;
//        for (int i = 0; i < ListMediaPrescription.size(); i++) {
//            if (ListMediaPrescription.get(i).getPrescription_file().equals("")) {
//                continue;
//            }
//            String filePath = JConst.mediaLocationPath + "/" + ListMediaPrescription.get(i).getPrescription_file();
//            String fileName = ListMediaPrescription.get(i).getPrescription_file();
//            String mimeType = fileName.substring(fileName.lastIndexOf("."));
//
////                //copy file ke folder DriveUtil
////                String newPath = "";
////                try {
////                    java.io.File srcFile = new java.io.File(filePath);
////                    newPath = FileUtil.copyFileDrive(srcFile, fileName);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            progres += 1;
//            int finalProgres = progres;
//            driveServiceHelper.createFileMedia(filePath, fileName, mimeType)
//                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            last_backup_media = Global.serverNowLong();
//                            backupDriveModel.setLast_backup_media(last_backup_media);
//                            backupDriveTable.update(backupDriveModel);
//                            progressDialog.setProgress(finalProgres);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("eror DriveUtil", e.toString());
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), getString(R.string.inform_upload_failed), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        //backup media File
//        ArrayList<RecordFileModel> ListMediaFile = recordFileTable.getRecords(last_backup_media);
//
//        for (int i = 0; i < ListMediaFile.size(); i++) {
//            String filePath = JConst.mediaLocationPath + "/" + ListMediaFile.get(i).getRecord_file();
//            String fileName = ListMediaFile.get(i).getRecord_file();
//            String mimeType = ListMediaFile.get(i).getMime_type();
//
//            progres += 1;
//            int finalProgres = progres;
//            driveServiceHelper.createFileMedia(filePath, fileName, mimeType)
//                    .addOnSuccessListener(new OnSuccessListener<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            last_backup_media = Global.serverNowLong();
//                            backupDriveModel.setLast_backup_media(last_backup_media);
//                            backupDriveTable.update(backupDriveModel);
//                            progressDialog.setProgress(finalProgres);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("eror DriveUtil", e.toString());
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), getString(R.string.inform_upload_failed), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//
//    private void downloadFile() {
//        SharedPreferences sharedPreferences = BackupRestoreDrive.this.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
//        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);
//        last_restore = sharedPreferences.getLong("last_download", 0);
//
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setTitle(getString(R.string.inform_download_gdrive));
//        progressDialog.setMessage(getString(R.string.progress_downloading_database));
//        progressDialog.setCancelable(false);
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.done), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                progressDialog.dismiss();//dismiss dialog
//            }
//        });
//        progressDialog.setProgress(0);
//        progressDialog.setMax(1);
////        if (isIncludeFile) {
////            progressDialog.setMax(Global.getMaxProgress(this, last_restore) + 1);
////        } else {
////            progressDialog.setMax(1);
////        }
//        progressDialog.show();
//        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
//
//        String fileName = "pasienqu";
//
//        driveServiceHelper.downloadDB(fileName)
//                .addOnSuccessListener(new OnSuccessListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        //cek dulu apakah user ingin menyertakan media
//                        if (isIncludeFile) {
//                            int process = 0;
//                            progressDialog.setProgress(1);
//                            progressDialog.setMessage("Downloading Media...");
//                            progressDialog.setMax(Global.getMaxProgress(BackupRestoreDrive.this, last_restore));
//                            downloadWithMedia(progressDialog, process);
//                        } else {
////                            Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
//                            progressDialog.setProgress(1);
//                            showCompleteProgress(progressDialog, getString(R.string.inform_restore_success));
//                            isRestoreSuccess = true;
//                        }
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_failed), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void downloadWithMedia(ProgressDialog progressDialog, int process) {
//        SharedPreferences sharedPreferences = BackupRestoreDrive.this.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
//        last_restore = sharedPreferences.getLong("last_download", 0);
//
//        //masukin Last download di shared pref
//        Global.setLastDownloadToSharedPref(this, last_restore);
//
//        //download media prescriotion file
//        ArrayList<RecordModel> ListMediaPrescription = recordTable.getRecordPrescriptionFile(last_restore);
//        for (int i = 0; i < ListMediaPrescription.size(); i++) {
//            process += 1;
//            String fileName = ListMediaPrescription.get(i).getPrescription_file();
//            long write_date = ListMediaPrescription.get(i).getWrite_date();
//
//            java.io.File file = new java.io.File(JConst.mediaLocationPath + "/" + fileName);
//            if (!file.exists()) {
//                int finalProcess = process;
//                driveServiceHelper.listFiles(fileName, false)
//                        .addOnSuccessListener(new OnSuccessListener() {
//                            @Override
//                            public void onSuccess(Object o) {
//                                //update Last download di shared pref
////                                Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, last_restore);
//                                progressDialog.setProgress(finalProcess);
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_failed), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            } else {
//                //update Last download di shared pref
////                long lastDownloadNew = sharedPreferences.getLong("last_download", 0);  //cek ulang
////                Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, last_restore);
//                progressDialog.setProgress(process);
//            }
//        }
//
//        //backup media File
//        int count2 = 0;
//        ArrayList<RecordFileModel> ListMediaFile = recordFileTable.getRecords(last_restore);
//
//        for (int i = 0; i < ListMediaFile.size(); i++) {
//            process += 1;
//            String fileName = ListMediaFile.get(i).getRecord_file();
//            long write_date = ListMediaFile.get(i).getWrite_date();
//            java.io.File file = new java.io.File(JConst.mediaLocationPath + "/" + fileName);
//
//            count2 = i;
//            int finalCount = count2;
//            int finalProcess = process;
//            if (!file.exists()) {
//                driveServiceHelper.listFiles(fileName, false)
//                        .addOnSuccessListener(new OnSuccessListener() {
//                            @Override
//                            public void onSuccess(Object o) {
//                                //update Last download di shared pref
//                                Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, write_date);
//                                progressDialog.setProgress(finalProcess);
//                                if (finalCount == ListMediaFile.size() - 1) {
////                                    Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
////                                    progressDialog.dismiss();
//                                    showCompleteProgress(progressDialog, getString(R.string.inform_restore_success));
//                                    isRestoreSuccess = true;
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_failed), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            } else {
//                //update Last download di shared pref
//                long lastDownloadNew = sharedPreferences.getLong("last_download", 0);  //cek ulang kali aja udah pernah ke update
//                Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, lastDownloadNew);
//                progressDialog.setProgress(process);
//                if (i == ListMediaFile.size() - 1) {
//                    Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, write_date);
////                    Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
////                    progressDialog.dismiss();
//                    showCompleteProgress(progressDialog, getString(R.string.inform_restore_success));
//                    isRestoreSuccess = true;
//                }
//            }
//        }
//        if (ListMediaFile.size() == 0){
//            showCompleteProgress(progressDialog, getString(R.string.inform_restore_success));
//            isRestoreSuccess = true;
//        } else if (ListMediaFile.size() == 0 && ListMediaPrescription.size() == 0) {
//            showCompleteProgress(progressDialog, getString(R.string.inform_restore_success));
//            isRestoreSuccess = true;
//        }
//    }
//
//    private void showCompleteProgress(ProgressDialog progressDialog, String message){
//        progressDialog.setTitle(message);
//        progressDialog.setMessage("");
//        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
//        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if (isRestoreSuccess) {
//                    NotificationUtil.runNotification();
//                }
//            }
//        });
//    }

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

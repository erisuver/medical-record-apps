package com.orion.pasienqu_2.activities.more.google_drive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.data_table.BackupDriveTable;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.BackupDriveModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordModel;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class DriveUtil {
    private static final String DATABASE_PATH = JApplication.getInstance().lokasi_db;
    private static DriveServiceHelper driveServiceHelper;

    public DriveUtil() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(JApplication.getInstance());
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(JApplication.getInstance(), Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("My Drive Tutorial").build();
        driveServiceHelper = new DriveServiceHelper(googleDriveService);
    }

    private static void getLastSignedGoogle(Activity context) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(JApplication.getInstance());
        if (account == null) {
            Toast.makeText(context, context.getString(R.string.error_not_signin_drive_yet), Toast.LENGTH_LONG).show();
            return;
        }
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(JApplication.getInstance(), Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("My Drive Tutorial").build();
        driveServiceHelper = new DriveServiceHelper(googleDriveService);


    }


    public static void upload(Activity context){
        getLastSignedGoogle(context);
        //initial
        BackupDriveTable backupDriveTable = new BackupDriveTable(context);
        BackupDriveModel backupDriveModel = backupDriveTable.getRecord();
        final long last_update_media = backupDriveTable.getLastUpdateMedia();
        final long last_update_data = backupDriveTable.getLastUpdateData();

        SharedPreferences sharedPreferences = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(context.getString(R.string.inform_upload_gdrive));
        progressDialog.setMessage(context.getString(R.string.inform_please_wait));
        progressDialog.setCancelable(false);
        int maxProgress = 0;
        if (isIncludeFile) {
            maxProgress = Global.getMaxProgress(context, last_update_media) + 1;
            progressDialog.setMax(maxProgress);
        } else {
            progressDialog.setMax(1);
        }
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();//dismiss dialog
            }
        });
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

        //create folder dulu
        driveServiceHelper.createFolder("PasienQu 2", context).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.d("eror DriveUtil", e.toString());
            }
        });

        //cek dulu apakah user ingin menyertakan media
        if (isIncludeFile) {
            uploadWithMedia(context, progressDialog);
        }

        //backup database
        int finalMaxProgress = maxProgress;
        JApplication.getInstance().db.execSQL("VACUUM");
        driveServiceHelper.createFileDB(DATABASE_PATH, "pasienqu", "db")
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        backupDriveModel.setLast_backup_data(Global.serverNowLong());
                        backupDriveModel.setLast_backup_media(backupDriveTable.getLastUpdateMedia());
                        backupDriveTable.update(backupDriveModel);

//                        if (last_update_data == 0) {
//                            backupDriveTable.insert(backupDriveModel);
//                        }else{
//                            backupDriveTable.update(backupDriveModel);
//                        }

                        if (isIncludeFile){
                            progressDialog.setProgress(finalMaxProgress);
                        }else{
                            progressDialog.setProgress(1);
                        }
                        showCompleteProgress(progressDialog, context.getString(R.string.inform_upload_success));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Global.infoDialog(context, context.getString(R.string.inform_upload_failed), e.getLocalizedMessage());
            }
        });

    }




    private static void uploadWithMedia(Activity context, ProgressDialog progressDialog){
        //initial
        RecordTable recordTable = new RecordTable(context);
        RecordFileTable recordFileTable = new RecordFileTable(context);
        BackupDriveTable backupDriveTable = new BackupDriveTable(context);
        BackupDriveModel backupDriveModel = backupDriveTable.getRecord();
        long last_update = backupDriveTable.getLastUpdateMedia();

        //backup media prescriotion file
        ArrayList<RecordModel> ListMediaPrescription = recordTable.getRecordPrescriptionFile(last_update);

        int progres = 0;
        for (int i = 0; i < ListMediaPrescription.size(); i++) {
            progres += 1;
            String fileName = ListMediaPrescription.get(i).getPrescription_file();
            String filePath = JConst.mediaLocationPath + "/" + fileName;
            java.io.File file = new java.io.File(filePath);

            if(file.exists() && !fileName.isEmpty()) {
                String mimeType = fileName.substring(fileName.lastIndexOf("."));
                int finalProgres = progres;
                driveServiceHelper.createFileMedia(filePath, fileName, mimeType)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                backupDriveModel.setLast_backup_media(Global.serverNowLong());
                                backupDriveTable.update(backupDriveModel);
                                progressDialog.setProgress(finalProgres);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("eror DriveUtil", e.toString());
                        progressDialog.dismiss();
                        Global.infoDialog(context, context.getString(R.string.inform_upload_failed), e.getMessage());
                    }
                });
            }else{
                progressDialog.setProgress(progres);
            }
        }

        //backup media File
        ArrayList<RecordFileModel> ListMediaFile = recordFileTable.getRecords(last_update);

        for (int i = 0; i < ListMediaFile.size(); i++) {
            progres += 1;
            String fileName = ListMediaFile.get(i).getRecord_file();
            String filePath = JConst.mediaLocationPath + "/" + fileName;

            java.io.File file = new java.io.File(filePath);
            if(file.exists() && !fileName.isEmpty()) {
                String mimeType = ListMediaFile.get(i).getMime_type();
                int finalProgres = progres;
                driveServiceHelper.createFileMedia(filePath, fileName, mimeType)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                backupDriveModel.setLast_backup_media(Global.serverNowLong());
                                backupDriveTable.update(backupDriveModel);
                                progressDialog.setProgress(finalProgres);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("eror DriveUtil", e.toString());
                        progressDialog.dismiss();
                        Global.infoDialog(context, context.getString(R.string.inform_upload_failed), e.getMessage());
                    }
                });
            }else{
                progressDialog.setProgress(progres);
            }
        }
    }


    private static void showCompleteProgress(ProgressDialog progressDialog, String message){
        progressDialog.setTitle(message);
        progressDialog.setMessage("");
        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
    }

    public static void download(Activity context, Runnable runnable) {
        getLastSignedGoogle(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);
        long last_restore = sharedPreferences.getLong("last_download", 0);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(context.getString(R.string.inform_download_gdrive));
        progressDialog.setMessage("Downloading Database...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();//dismiss dialog
                Runnable runRestartApp = new Runnable() {
                    @Override
                    public void run() {
                        context.finishAffinity();
                        System.exit(0);
                    }
                };
                ShowDialog.infoDialogWithRunnable(context, context.getString(R.string.information), "Restore Berhasil, Silahkan buka ulang Aplikasi Pasienqu", runRestartApp);
            }
        });
        progressDialog.setProgress(0);
        progressDialog.setMax(1);
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

        String fileName = "pasienqu";

        driveServiceHelper.downloadDB(fileName)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        //cek dulu apakah user ingin menyertakan media
                        if (isIncludeFile) {
                            int process = 0;
                            progressDialog.setProgress(1);
                            progressDialog.setMessage("Downloading Media...");
                            progressDialog.setMax(Global.getMaxProgress(context, last_restore));
                            downloadWithMedia(context, progressDialog, process);
                        } else {
//                            Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
                            progressDialog.setProgress(1);
                            showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
                        }
                        runnable.run();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Global.infoDialogRun(context,  context.getString(R.string.inform_restore_failed), e.getMessage(), runnable);
                    }
                });
    }

    public static void download(Activity context) {
        getLastSignedGoogle(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        boolean isIncludeFile = sharedPreferences.getBoolean("include_file", false);
        long last_restore = sharedPreferences.getLong("last_download", 0);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(context.getString(R.string.inform_download_gdrive));
        progressDialog.setMessage("Downloading Database...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();//dismiss dialog
                Runnable runRestartApp = new Runnable() {
                    @Override
                    public void run() {
                        context.finishAffinity();
                        System.exit(0);
                    }
                };
                ShowDialog.infoDialogWithRunnable(context, context.getString(R.string.information), "Restore Berhasil, Silahkan buka ulang Aplikasi Pasienqu", runRestartApp);
            }
        });
        progressDialog.setProgress(0);
        progressDialog.setMax(1);
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);

        String fileName = "pasienqu";

        driveServiceHelper.downloadDB(fileName)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        //cek dulu apakah user ingin menyertakan media
                        if (isIncludeFile) {
                            int process = 0;
                            progressDialog.setProgress(1);
                            progressDialog.setMessage("Downloading Media...");
                            progressDialog.setMax(Global.getMaxProgress(context, last_restore));
                            downloadWithMedia(context, progressDialog, process);
                        } else {
//                            Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
                            progressDialog.setProgress(1);
                            showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String error = e.getMessage();
                        if (error.contains("400")){
                            Global.infoDialog(context,  context.getString(R.string.inform_restore_failed), context.getResources().getString(R.string.backup_file_not_found));
                        }
                    }
                });
    }

    private static  void downloadWithMedia(Activity context, ProgressDialog progressDialog, int process) {
        //initial
        RecordTable recordTable = new RecordTable(context);//initial
        RecordFileTable recordFileTable = new RecordFileTable(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        long last_restore = sharedPreferences.getLong("last_download", 0);

        //masukin Last download di shared pref
        Global.setLastDownloadToSharedPref(context, last_restore);

        //check dir media
        java.io.File folder = new java.io.File(JConst.mediaLocationPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //download media prescriotion file
        ArrayList<RecordModel> ListMediaPrescription = recordTable.getRecordPrescriptionFile(last_restore);
        for (int i = 0; i < ListMediaPrescription.size(); i++) {
            process += 1;
            String fileName = ListMediaPrescription.get(i).getPrescription_file();
            long write_date = ListMediaPrescription.get(i).getWrite_date();

            java.io.File file = new java.io.File(JConst.mediaLocationPath + "/" + fileName);
            if (!file.exists()) {
                int finalProcess = process;
                driveServiceHelper.listFiles(fileName, false)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                progressDialog.setProgress(finalProcess);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Global.infoDialog(context,  context.getString(R.string.inform_restore_failed), e.getMessage());
                            }
                        });
            } else {
                //update Last download di shared pref
//                long lastDownloadNew = sharedPreferences.getLong("last_download", 0);  //cek ulang
//                Global.setLastDownloadToSharedPref(BackupRestoreDrive.this, last_restore);
                progressDialog.setProgress(process);
            }
        }

        //backup media File
        int count2 = 0;
        ArrayList<RecordFileModel> ListMediaFile = recordFileTable.getRecords(last_restore);

        for (int i = 0; i < ListMediaFile.size(); i++) {
            process += 1;
            String fileName = ListMediaFile.get(i).getRecord_file();
            long write_date = ListMediaFile.get(i).getWrite_date();
            java.io.File file = new java.io.File(JConst.mediaLocationPath + "/" + fileName);

            count2 = i;
            int finalCount = count2;
            int finalProcess = process;
            if (!file.exists()) {
                driveServiceHelper.listFiles(fileName, false)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                //update Last download di shared pref
                                Global.setLastDownloadToSharedPref(context, write_date);
                                progressDialog.setProgress(finalProcess);
                                if (finalCount == ListMediaFile.size() - 1) {
//                                    Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
                                    showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Global.infoDialog(context,  context.getString(R.string.inform_restore_failed), e.getMessage());
                            }
                        });
            } else {
                //update Last download di shared pref
                long lastDownloadNew = sharedPreferences.getLong("last_download", 0);  //cek ulang kali aja udah pernah ke update
                Global.setLastDownloadToSharedPref(context, lastDownloadNew);
                progressDialog.setProgress(process);
                if (i == ListMediaFile.size() - 1) {
                    Global.setLastDownloadToSharedPref(context, write_date);
//                    Toast.makeText(getApplicationContext(), getString(R.string.inform_restore_success), Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
                    showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
                }
            }
        }
        if (ListMediaFile.size() == 0){
            showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
        }
        if (ListMediaFile.size() == 0 && ListMediaPrescription.size() == 0) {
            //jika tidak ada file maka jadikan proses ke 1/1
            progressDialog.setMax(1);
            progressDialog.setProgress(1);
            showCompleteProgress(progressDialog, context.getString(R.string.inform_restore_success));
        }
    }

}

package com.orion.pasienqu_2.activities.more.import_data_v1;

import static com.orion.pasienqu_2.Routes.url_file;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orion.pasienqu_2.DBConnV1;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.more.export.ExportActivity2;
import com.orion.pasienqu_2.activities.more.export.ExportSuccessActivity;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.v1.CatatanMedisTable;
import com.orion.pasienqu_2.data_table.v1.DataPasienTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.ExportExcel;
import com.orion.pasienqu_2.globals.FilePath;
import com.orion.pasienqu_2.globals.FileUtil;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.ZipUtils;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ImportDataV1Activity extends CustomAppCompatActivity {
    private Button btnStart;
    private Uri uri;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private String dbLocation;
    private Switch swcClear;

    // keperluan download
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String destination = "";
    private String errorDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data_v1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.import_database));
        CreateView();
        InitClass();
        EventClass();
    }


    private void CreateView() {
        btnStart = (Button) findViewById(R.id.btnStart);
        swcClear = (Switch) findViewById(R.id.swcClear);
    }

    private void InitClass() {
        dbLocation = JApplication.getInstance().dbV1Location;
        swcClear.setChecked(true);
    }

    private void EventClass() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startImport();

                if (!Global.CheckConnectionInternet(ImportDataV1Activity.this)) {
                    ShowDialog.infoDialog(ImportDataV1Activity.this, "PasienQu", getString(R.string.must_be_online));
                    return;
                }
                //cari file sesuai email
                String email = JApplication.getInstance().loginCompanyModel.getEmail();
                email = email.replace("@gmail.com", "agmail.zip");
                //mulai proses download
                new DownloadFileFromURL().execute(Routes.url_folder_file() + email);
            }
        });
    }

    private void getFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    private void startImport() {
//        String location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/Pasienqu/pasienqu.db";
        String location = JConst.sharedFolderLocation;
        String filename = location+"/backup.db";
        String locationZip = location + "/pasienqu.zip";

        //cek folder
        File folder = new File(location);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //proses unziping
        try {
            ZipUtils.unzip(locationZip, location);  //unzip file
            File fileZip = new File(locationZip);  //delete file .zip sesudah restore
            fileZip.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(filename);
        if(file.exists()) {
            boolean isAlreadyImport = SharedPrefsUtils.getBooleanPreference(this, "already_import", false);
            if(!swcClear.isChecked() && isAlreadyImport){
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        showDialogImport(filename);
                    }
                };
                ShowDialog.confirmDialog(ImportDataV1Activity.this, getString(R.string.import_database),getString(R.string.content_already_import), run);
            }else {
                showDialogImport(filename);
            }
        }else{
            Toast.makeText(ImportDataV1Activity.this, getString(R.string.backup_file_version_1_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogImport(String filename){
        Runnable runExport = new Runnable() {
            @Override
            public void run() {
                ImportDataV1Activity.this.restoreDBNew(filename);
            }
        };
        ShowDialog.confirmDialog(ImportDataV1Activity.this, ImportDataV1Activity.this.getString(R.string.import_database), getString(R.string.do_you_restore_from_previous_version), runExport);
    }

    private void restoreDBNew(String FileName){
        {
            try
            {
                String currentDBPathshm = FileName+"-shm";
                String backupDBPathshm = dbLocation+"-shm";
                String currentDBPathwal = FileName+"-wal";
                String backupDBPathwal = dbLocation+"-wal";
                File currentDBshm = new File(currentDBPathshm);
                File backupDBshm = new File(backupDBPathshm);
                File currentDBwal = new File(currentDBPathwal);
                File backupDBwal = new File(backupDBPathwal);

                String currentDBPath = FileName;
                String backupDBPath = dbLocation;
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);

                if (currentDBshm.exists()) {
                    FileChannel src = new FileInputStream(currentDBshm).getChannel();
                    FileChannel dst = new FileOutputStream(backupDBshm).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                //wal
                if (currentDBwal.exists()) {
                    FileChannel src = new FileInputStream(currentDBwal).getChannel();
                    FileChannel dst = new FileOutputStream(backupDBwal).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }


                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                //mulai import
                SyncToV2();

            }
            catch (Exception e) {
                Toast.makeText(ImportDataV1Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
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


    private void manageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
    }

    private void GetPermission() {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){  //kalo android 13 ga butuh permission
            //gausah ngapa ngapain
        }else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permission request code is just an int
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permission request code is just an int
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
            }
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetPermission();
                    manageExternalStoragePermission();
                } else {
                    // Permission Denied
                    if(JApplication.getInstance().isFirstTimeRequest) {
                        JApplication.getInstance().isFirstTimeRequest = false;
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void SyncToV2(){
        DataPasienTable dataPasienTable = new DataPasienTable(ImportDataV1Activity.this);
        CatatanMedisTable catatanMedisTable = new CatatanMedisTable(ImportDataV1Activity.this);
        RecordTable recordTable = new RecordTable(ImportDataV1Activity.this);

        //progress
        int maxProgress = 0;
        ProgressDialog progressDialog = new ProgressDialog(ImportDataV1Activity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getString(R.string.title_import_patient));
        progressDialog.setCancelable(false);

        int maxPatient = dataPasienTable.getTotalDatas();
        int maxMedic = catatanMedisTable.getTotalDatas();
        maxProgress = maxPatient;// + catatanMedisTable.getTotalDatas();  //set total data pasien
        progressDialog.setProgress(0);
        progressDialog.setMax(maxProgress);
        progressDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String uuid_location = sharedPreferences.getString("uuidLocation","");
        final int id_location = JApplication.getInstance().workLocationTable.getLocationIDByUuId(uuid_location);
        Thread mThread =
                new Thread() {
            @Override
            public void run() {
                int offset = 0;

                while (offset <= maxPatient) {
                    dataPasienTable.syncTo2(swcClear.isChecked(), progressDialog, offset);
                    offset += 500;
                }

                progressDialog.setMessage(getString(R.string.title_import_record));
                progressDialog.setMax(maxMedic);

                offset = 0;
                while (offset <= maxMedic) {
                    catatanMedisTable.syncToV2(swcClear.isChecked(), progressDialog,  offset, id_location);
                    offset += 500;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage(getString(R.string.preparing_data));
                        recordTable.updateIdPasienMedicalAfterSyncV1();
                        progressDialog.dismiss();

                        SharedPrefsUtils.setBooleanPreference(ImportDataV1Activity.this, "already_import", true);
                        Global.infoDialog(ImportDataV1Activity.this, getString(R.string.information), "Import Successfully");
                    }
                });
            }
        };
        mThread.start();

//        Toast.makeText(ImportDataV1Activity.this, getString(R.string.import_success), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            errorDownload = "";
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                String location = JConst.sharedFolderLocation;
                File folder = new File(location);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                String destination = location+ "/pasienqu.zip";
                OutputStream output = new FileOutputStream(destination);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                errorDownload = e.getMessage();
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);
            if (errorDownload.isEmpty()) {
                startImport();
            }else{
                Toast.makeText(ImportDataV1Activity.this, errorDownload, Toast.LENGTH_LONG).show();
            }
        }

    }

}
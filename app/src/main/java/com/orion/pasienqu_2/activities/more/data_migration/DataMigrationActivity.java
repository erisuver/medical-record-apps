package com.orion.pasienqu_2.activities.more.data_migration;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.MessagePattern;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
//import retrofit2.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.MainActivity;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.globals.AESUtils;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.FileUtil;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.VolleyMultipartRequest;
import com.orion.pasienqu_2.globals.VolleySingleton;
import com.orion.pasienqu_2.globals.retrofit.ApiService;
import com.orion.pasienqu_2.globals.retrofit.MigrationMedia;
import com.orion.pasienqu_2.globals.retrofit.MigrationResponse;
import com.orion.pasienqu_2.globals.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataMigrationActivity extends CustomAppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private Button btnUpload, btnDownload, btnUploadMedia;
    private String dbLocation;
    private SwitchCompat swcFileDownload, swcFileUpload;

    private SQLiteDatabase db;

    // keperluan download
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    public static final int progress_spinner_type = 1;
    private String errorDownload;
    public int countTableDMP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_migration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.data_migration));

//        JApplication.getInstance().real_url = Routes.IP_ADDRESS;
//        JApplication.getInstance().updateIPSharePref(Routes.IP_ADDRESS);
//        JApplication.getInstance().real_url = JApplication.getInstance().real_url+"/"+Routes.NAMA_API+"/public/";
        CreateView();
        InitClass();
        EventClass();
    }


    private void CreateView() {
        btnUpload = findViewById(R.id.btnUpload);
        btnDownload = findViewById(R.id.btnDownload);
        swcFileDownload = findViewById(R.id.swcFileDownload);
        swcFileUpload = findViewById(R.id.swcFileUpload);
        btnUploadMedia = findViewById(R.id.btnUploadMedia);
    }

    private void InitClass() {
        dbLocation = JApplication.getInstance().dbV1Location;
        swcFileDownload.setChecked(false);
        swcFileUpload.setChecked(false);
        db = JApplication.getInstance().db;
    }

    private void EventClass() {
        btnUpload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(DataMigrationActivity.this)) {
                ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                return;
            }
/*
            Runnable runCheckData = new Runnable() {
                @Override
                public void run() {
                    Runnable runUpload = new Runnable() {
                        @Override
                        public void run() {
                            UploadData();
                        }
                    };
                    checkDataPasienquOnline(runUpload, false);
                }
            };
*/
            Runnable runConfirm = new Runnable() {
                @Override
                public void run() {
                    Runnable runUpload = new Runnable() {
                        @Override
                        public void run() {
                            UploadData();
                        }
                    };
                    ShowDialog.confirmDialog(DataMigrationActivity.this, String.valueOf(getTitle()), getString(R.string.confirm_data_migration_2), runUpload);
                }
            };

            ShowDialog.confirmDialog(DataMigrationActivity.this, String.valueOf(getTitle()), getString(R.string.confirm_data_migration), runConfirm);
        });

        btnUploadMedia.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(DataMigrationActivity.this)) {
                ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                return;
            }



            Runnable runCheckData = new Runnable() {
                @Override
                public void run() {
                    Runnable runMigrationMedia = new Runnable() {
                        @Override
                        public void run() {

                            pDialog = new ProgressDialog(DataMigrationActivity.this);
                            pDialog.setIndeterminate(false);
                            pDialog.setMax(100);
                            pDialog.setProgress(99);
                            pDialog.setMessage("Progress..");
                            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            pDialog.setCancelable(false);
                            pDialog.show();
                            migrationMedia();
                        }
                    };
                    checkDataPasienquOnline(runMigrationMedia, true);
                }
            };

            ShowDialog.confirmDialog(DataMigrationActivity.this, String.valueOf(getTitle()), getString(R.string.confirm_data_migration), runCheckData);
        });

        btnDownload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(DataMigrationActivity.this)) {
                ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                return;
            }
            DownloadData();
        });
    }

    private void DownloadData() {
    }

    private void UploadData() {
        new UploadFileDML(this).execute();
    }

    private String PrepareDatabase(String tableName) {
        String hasil = "";
        try {
            // Lakukan query SQL untuk mengambil semua data dari tabel pasien
            String selectQuery = "SELECT * FROM "+tableName;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = 0;
                String head = "";
                if (tableName.equals("pasienqu_note_template")){
                    head = " INSERT INTO " + tableName + " (" + fieldListTable(tableName) + ", user_id) VALUES ";
                }else if (tableName.equals("pasienqu_patient") || tableName.equals("pasienqu_record") || tableName.equals("pasienqu_billing")  || tableName.equals("pasienqu_work_location")) {
                    head = " INSERT INTO " + tableName + " (id_lokal," + fieldListTable(tableName) + ", company_id, user_id) VALUES ";
                }else {
                    head = " INSERT INTO " + tableName + " (" + fieldListTable(tableName) + ", company_id, user_id) VALUES ";
                }
                StringBuilder dmlBuilder = new StringBuilder();

                String[] columnNames = cursor.getColumnNames();
                do {
                    StringBuilder valuesBuilder = new StringBuilder();
                    for (String columnName : columnNames) {
                        int columnIndex = cursor.getColumnIndexOrThrow(columnName);
                        int columnType = cursor.getType(columnIndex); // Mendapatkan tipe data kolom
                        String value = "";
                        // Mengambil nilai dari cursor sesuai dengan nama kolom
                        if (columnType == Cursor.FIELD_TYPE_INTEGER && (columnName.equals("patient_id") || columnName.equals("billing_id") || columnName.equals("medical_record_id") || columnName.equals("work_location_id"))){
                            value = "-" + cursor.getString(columnIndex);
                        }else{
                            value = cursor.getString(columnIndex);
                        }

                        if (columnName.equals("first_name") || columnName.equals("surname") || columnName.equals("email") || columnName.equals("contact_no") || columnName.equals("address_street_1") || columnName.equals("address_street_2") ){
                            //masukan ke param
                            if (value != null && !value.isEmpty()) {
                                value = AESUtils.encrypt(value);
                            }
                        }

                        // Jika nama kolom adalah "id," lanjutkan ke iterasi berikutnya karena mysql auto increment
                        if ((columnName.equals("id") && !tableName.equals("pasienqu_patient") && !tableName.equals("pasienqu_record") && !tableName.equals("pasienqu_billing") && !tableName.equals("pasienqu_work_location")) ||
                                (tableName.equals("pasienqu_note_template") && columnName.equals("company_id"))) {
                            continue;
                        }

                        //cek field active ubah falue karena di mysql konsepnya pake T dan F
                        if (columnName.equals("active") || columnName.equals("is_temp")){
                            if(value.equals("true")){
                                value = "T";
                            }else{
                                value = "F";
                            }
                        }

                        if (value != null){
//                            value = value.replace("'", "\\'").replace("\"", "\\\"").replace("\\", "\\\\");
                            value = value.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"");
                        }
                        if (value == null && (columnName.equals("month") || columnName.equals("total_billing")  || columnName.equals("billing_id") || columnName.equals("create_date") || columnName.equals("write_date"))) {
                            value = "0";
                        }

                        // Memeriksa tipe data kolom dan menambahkan tanda kutip jika tipe datanya adalah string
                        if (columnType == Cursor.FIELD_TYPE_STRING) {
                            valuesBuilder.append("'");
                        }

                        valuesBuilder.append(value);

                        // Memeriksa tipe data kolom dan menutup tanda kutip jika tipe datanya adalah string
                        if (columnType == Cursor.FIELD_TYPE_STRING) {
                            valuesBuilder.append("'");
                        }

                        // Jika bukan kolom terakhir, tambahkan koma
                        if (!columnName.equals(columnNames[columnNames.length - 1])) {
                            valuesBuilder.append(", ");
                        }

                    }
                    int company_id = JApplication.getInstance().loginCompanyModel.getId();
                    String user_id = "'"+JApplication.getInstance().loginCompanyModel.getEmail()+"'";
                    valuesBuilder.append(", ").append("ISI_COMPANYID").append(", ").append(user_id);

                    // Buat string dml
                    if (count == 0) {
                        dmlBuilder.append(head).append(" (").append(valuesBuilder.toString()).append(")");
                    } else {
                        dmlBuilder.append(", (").append(valuesBuilder.toString()).append(")");
                        dmlBuilder.append(System.lineSeparator()); //untuk menutup tutup query
                    }


                    count++;

                    // 3. Set batas 50 dan tulis ke berkas dmpTemp.txt jika sudah mencapai batas
                    if (count % 50 == 0 ) {
                        dmlBuilder.append(System.lineSeparator()); //untuk menutup tutup query
                        dmlBuilder.append(";/*RUN*/ ").append(System.lineSeparator()); //untuk menutup tutup query
                        dmlBuilder.append(System.lineSeparator()); //untuk menutup tutup query
                        count = 0;
                    }
                } while (cursor.moveToNext());
                dmlBuilder.append(System.lineSeparator()); //untuk menutup tutup query
                dmlBuilder.append(";/*RUN*/ ").append(System.lineSeparator()); //untuk menutup tutup query
                dmlBuilder.append(System.lineSeparator()); //untuk menutup tutup query

//                dmlBuilder.append(";/*RUN*/ ").append(System.lineSeparator()); //untuk menutup tutup query
                hasil = dmlBuilder.toString().trim();
                hasil = hasil.replace(", ,", ", "); //untuk pencegahan jika ada value kosong

                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasil;
    }

    // Fungsi untuk menulis string dml ke berkas dmpTemp.txt
    private void writeDMLToFile(String dml) {
//        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File folder = new File(JConst.DMPLocationPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, "dmpTemp.txt");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ByteArrayInputStream bis = new ByteArrayInputStream(dml.getBytes())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String fieldListTable(String table_name) {
        String result = "";
        // Query untuk mendapatkan informasi skema tabel pasienqu_patient
        String schemaQuery = "PRAGMA table_info(" + table_name + ")";
        Cursor schemaCursor = db.rawQuery(schemaQuery, null);

        if (schemaCursor != null) {
            StringBuilder fieldList = new StringBuilder();

            // Iterasi melalui informasi skema untuk mendapatkan nama kolom
            while (schemaCursor.moveToNext()) {
                String columnName = schemaCursor.getString(schemaCursor.getColumnIndexOrThrow("name"));

                // Pengecekan untuk kolom "id"
                if (columnName.equals("id")) {
                    continue; // Lanjutkan ke iterasi berikutnya jika nama kolom adalah "id"
                }

                fieldList.append(columnName);

                // Jika bukan kolom terakhir, tambahkan koma
                if (!schemaCursor.isLast()) {
                    fieldList.append(", ");
                }
            }

            schemaCursor.close();
            result = fieldList.toString();
        }
        return result;
    }

    private void getFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permission request code is just an int
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
            }
        }
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setMessage("Progress..");
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            case progress_spinner_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.setMessage("Progress..");
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    class UploadFileDML extends AsyncTask<String, String, String> {
        private Context context;

        public UploadFileDML(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
            pDialog.setMessage("Creating DML...");
        }

        @Override
        protected String doInBackground(String... params) {
            // Di sini, Anda dapat menempatkan logika pembuatan berkas dmpTemp.txt
            // Selama proses, Anda dapat memanggil publishProgress() untuk memperbarui progress dialog
            // Contoh: publishProgress("10%"); // Mengupdate progress dialog ke 10%
            // Setelah selesai, kembalikan hasil atau pesan sukses.

            List<String> tableNames = new ArrayList<>();
            tableNames.add("pasienqu_patient");
            tableNames.add("pasienqu_record");
            tableNames.add("pasienqu_billing");
            tableNames.add("pasienqu_appointment");
            tableNames.add("pasienqu_note_template");
            tableNames.add("pasienqu_billing_template");
            tableNames.add("pasienqu_work_location");
            tableNames.add("pasienqu_billing_item");
            tableNames.add("pasienqu_record_diagnosa");
            tableNames.add("pasienqu_record_file");

            StringBuilder dmp = new StringBuilder();
            int count = 0;
            for (String tableName : tableNames) {
                publishProgress("" + ((count * 100) / tableNames.size()));
                // Lakukan sesuatu dengan dml
                String dml = PrepareDatabase(tableName);
                dmp.append(System.lineSeparator()); //untuk menutup tutup query
                dmp.append(dml);
                dmp.append(System.lineSeparator()); //untuk menutup tutup query
                count++;
            }

            //Tulis data ke berkas dmpTemp.txt
            if (dmp.length() > 0) {
                writeDMLToFile(dmp.toString());
            }

            return "dmpTemp.txt created successfully!";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // Memperbarui progress dialog dengan nilai baru
            pDialog.setMessage("Creating DML ");
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Proses selesai, tutup progress dialog
//            pDialog.dismiss();
            // Tampilkan pesan hasil atau lakukan tindakan setelah selesai
            // Contoh: Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            migrationDatabase();
        }
    }

//    public void migrationDatabase() {
//        // ...
//        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
//
//        //migrasi file
//        List<MultipartBody.Part> mediaParts = new ArrayList<>();
//        if (swcFileUpload.isChecked()) {
//            File folder = new File(JConst.mediaLocationPath);
//            File[] files = folder.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile()) {
//                        RequestBody requestFileMedia = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                        MultipartBody.Part mediaPart = MultipartBody.Part.createFormData("mediaFiles", file.getName(), requestFileMedia);
//                        mediaParts.add(mediaPart);
//                    }
//                }
//            }
//        }
//
//
//        // Mengambil konten file DMP
//        File pathDML = new File(JConst.DMPLocationPath);
//        File fileDmp = new File(pathDML, "dmpTemp.txt");
//        RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), fileDmp);
//        MultipartBody.Part dmpPart = MultipartBody.Part.createFormData("dmp", fileDmp.getName(), requestFile);
//
//        // Mengambil nilai lain dari param
//        String folder = "company_" + JApplication.getInstance().loginCompanyModel.getId();
//        RequestBody folderBody = RequestBody.create(MediaType.parse("text/plain"), folder);
//        RequestBody companyIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(JApplication.getInstance().loginCompanyModel.getId()));
//        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), JApplication.getInstance().loginCompanyModel.getEmail());
//
////        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody, mediaParts);
////        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody, mediaParts);
//        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody);
//
//        call.enqueue(new Callback<MigrationResponse>() {
//            @Override
//            public void onResponse(Call<MigrationResponse> call, Response<MigrationResponse> response) {
//                // Tanggapi respons
//                if (response.isSuccessful()) {
//                    MigrationResponse data = response.body();
//                    ShowDialog.infoDialog(DataMigrationActivity.this, data.toString(), getString(R.string.migration_success));
//                    // Lakukan sesuatu dengan data
//                } else {
//                    // Tangani respon error
//                    // Misalnya, dapat Anda tampilkan pesan kesalahan ke pengguna
//                    Toast.makeText(DataMigrationActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
//                }
//                pDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<MigrationResponse> call, Throwable t) {
//                Toast.makeText(DataMigrationActivity.this, "GAGAL", Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        });
//    }
    public void migrationDatabase(){
        pDialog.setMessage("Uploading...");
        String url = Routes.url_migrasi_data_upload();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    if (status.equals(JConst.STATUS_API_SUCCESS)) {

                        if (swcFileUpload.isChecked()){
                            migrationMedia();
                        }else {
                            pDialog.dismiss();
                            ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.migration_success));
                        }
                    }else{
                        Toast.makeText(DataMigrationActivity.this, status, Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                    pDialog.dismiss();
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //isi value
                String folder = "company_" + JApplication.getInstance().loginCompanyModel.getId();

                //masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("folder", folder);
                params.put("company_id", String.valueOf(JApplication.getInstance().loginCompanyModel.getId()));
                params.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                //upload DML
                File pathDML = new File(JConst.DMPLocationPath);
                File fileDmp = new File(pathDML, "dmpTemp.txt");
                String fieldName = "dmp";
                DataPart dataPart = new DataPart("dmpTemp.txt", getFileDataFromPath(fileDmp.getPath()), "text/plain");
                params.put(fieldName, dataPart);

                //upload media
//                if (swcFileUpload.isChecked()){
//                    // Folder yang akan diunggah
//                    File folder = new File(JConst.mediaLocationPath);
//                    File[] files = folder.listFiles();
//                    if (files != null) {
//                        for (File file : files) {
//                            if (file.isFile()) {
////                                // Baca isi file menjadi string
////                                String fileContents = readFileContents(file);
////                                String name = file.getName();
////                                DataPart dataPartMedia = new DataPart(file.getName(), fileContents.getBytes(), "text/plain");
////                                params.put(name, dataPartMedia);
//
//                                try {
//                                    Uri newUri = Uri.fromFile(file);
//                                    InputStream iStream = getContentResolver().openInputStream(newUri);
//                                    byte [] fileBytes = Global.getBytes2(iStream);
//                                    String name = file.getName();
//                                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//                                    String mimeType = mimeTypeMap.getMimeTypeFromExtension(FileUtil.getMime(DataMigrationActivity.this, newUri));
//                                    DataPart dataPartMedia = new DataPart(file.getName(), fileBytes, mimeType);
//
//                                    params.put(name, dataPartMedia);
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                }

                return params;
            }
        };
        int timeout = 5*60*1000; // 5 menit
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }


//    public void migrationDatabase() {
//        // ...
//        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
//
//        //migrasi file
//        List<MultipartBody.Part> mediaParts = new ArrayList<>();
//        if (swcFileUpload.isChecked()) {
//            File folder = new File(JConst.mediaLocationPath);
//            File[] files = folder.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile()) {
//                        RequestBody requestFileMedia = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                        MultipartBody.Part mediaPart = MultipartBody.Part.createFormData("mediaFiles", file.getName(), requestFileMedia);
//                        mediaParts.add(mediaPart);
//                    }
//                }
//            }
//        }
//
//
//        // Mengambil konten file DMP
//        File pathDML = new File(JConst.DMPLocationPath);
//        File fileDmp = new File(pathDML, "dmpTemp.txt");
//        RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), fileDmp);
//        MultipartBody.Part dmpPart = MultipartBody.Part.createFormData("dmp", fileDmp.getName(), requestFile);
//
//        // Mengambil nilai lain dari param
//        String folder = "company_" + JApplication.getInstance().loginCompanyModel.getId();
//        RequestBody folderBody = RequestBody.create(MediaType.parse("text/plain"), folder);
//        RequestBody companyIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(JApplication.getInstance().loginCompanyModel.getId()));
//        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), JApplication.getInstance().loginCompanyModel.getEmail());
//
////        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody, mediaParts);
////        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody, mediaParts);
//        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody);
//
//        call.enqueue(new Callback<MigrationResponse>() {
//            @Override
//            public void onResponse(Call<MigrationResponse> call, Response<MigrationResponse> response) {
//                // Tanggapi respons
//                if (response.isSuccessful()) {
//                    MigrationResponse data = response.body();
//                    ShowDialog.infoDialog(DataMigrationActivity.this, data.toString(), getString(R.string.migration_success));
//                    // Lakukan sesuatu dengan data
//                } else {
//                    // Tangani respon error
//                    // Misalnya, dapat Anda tampilkan pesan kesalahan ke pengguna
//                    Toast.makeText(DataMigrationActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
//                }
//                pDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<MigrationResponse> call, Throwable t) {
//                Toast.makeText(DataMigrationActivity.this, "GAGAL", Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        });
//    }



    public void migrationMedia(){
        pDialog.setMessage("Uploading media...");
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.migration_success));
                pDialog.dismiss();
            }
        };

        Runnable runFailed = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DataMigrationActivity.this, getString(R.string.error_connection_timeout), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        };

        MigrationMedia.migrationMedia(DataMigrationActivity.this, run, runFailed, pDialog);
//        String url = Routes.url_migrasi_media();
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                String resultResponse = new String(response.data);
//                try {
//                    JSONObject result = new JSONObject(resultResponse);
//                    String status = result.getString("status");
//                    if (status.equals(JConst.STATUS_API_SUCCESS)) {
//                        pDialog.dismiss();
//                        ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.migration_success));
//                    }else{
//                        Toast.makeText(DataMigrationActivity.this, status, Toast.LENGTH_SHORT).show();
//                        pDialog.dismiss();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    pDialog.dismiss();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                    pDialog.dismiss();
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String status = response.getString("status");
//                        String message = response.getString("message");
//
//                        Log.e("Error Status", status);
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message+" Please login again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message+ " Check your inputs";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message+" Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        pDialog.dismiss();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
//                pDialog.dismiss();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                //isi value
//                String folder = "company_" + JApplication.getInstance().loginCompanyModel.getId();
//
//                //masukan ke param
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("folder", folder);
//                params.put("company_id", String.valueOf(JApplication.getInstance().loginCompanyModel.getId()));
//                params.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//
//                // Folder yang akan diunggah
//                File folder = new File(JConst.mediaLocationPath);
//                File[] files = folder.listFiles();
//                if (files != null) {
//                    for (File file : files) {
//                        if (file.isFile()) {
//                            try {
//                                Uri newUri = Uri.fromFile(file);
//                                InputStream iStream = getContentResolver().openInputStream(newUri);
//                                byte [] fileBytes = Global.getBytes2(iStream);
//                                String name = file.getName();
//                                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//                                String mimeType = mimeTypeMap.getMimeTypeFromExtension(FileUtil.getMime(DataMigrationActivity.this, newUri));
//                                DataPart dataPartMedia = new DataPart(file.getName(), fileBytes, mimeType);
//
//                                params.put(name, dataPartMedia);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//
//                return params;
//            }
//        };
//        int timeout = 5*60*1000; // 5 menit
//        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                timeout,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private byte[] getFileDataFromPath(String path) {
        File file = new File(path);
        byte[] data = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String readFileContents(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            fileInputStream.close();
            return byteArrayOutputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void checkDataPasienquOnline(Runnable runUpload, boolean isUploadMedia) {
        ProgressDialog loading = Global.createProgresSpinner(DataMigrationActivity.this, getString(R.string.loading));
        loading.show();

        String filter = "?email=" + JApplication.getInstance().loginCompanyModel.getEmail();
        String url = Routes.url_check_data_user() + filter;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    int count = new JSONObject(response).getJSONObject("data").getInt("count");
                    if (isUploadMedia){
                        if (count > 0){
                            runUpload.run();
                        }else{
                            ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.inform_account_unregistered));
                        }
                    }else{
                        if (count == 0){
                            runUpload.run();
                        }else{
                            ShowDialog.infoDialog(DataMigrationActivity.this, getString(R.string.information), getString(R.string.inform_account_already_registered));
                        }
                    }
                    loading.dismiss();
                } else if (status.equals(JConst.STATUS_API_FAILED)) {
                    String pesan = new JSONObject(response).getString("error");
                    loading.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

}
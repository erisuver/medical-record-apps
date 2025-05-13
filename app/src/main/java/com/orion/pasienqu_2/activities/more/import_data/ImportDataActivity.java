package com.orion.pasienqu_2.activities.more.import_data;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.FilePath;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;
import java.util.jar.JarEntry;

public class ImportDataActivity extends CustomAppCompatActivity {
    private Button btnStart;
    private ImageButton btnImport;
    private Uri uri;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.import_database));
        CreateView();
        InitClass();
        EventClass();
        GetPermission();
    }


    private void CreateView() {
        btnImport = (ImageButton) findViewById(R.id.btnImport);
        btnStart = (Button) findViewById(R.id.btnStart);
    }

    private void InitClass() {

    }

    private void EventClass() {
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImport();
            }
        });
    }

    private void getFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    private void startImport() {
//        String extension = "";
//
//        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
//            final MimeTypeMap mime = MimeTypeMap.getSingleton();
//            extension = mime.getExtensionFromMimeType(ImportDataActivity.this.getContentResolver().getType(uri));
//            JSONObject jsonObject = new JSONObject(mime.);
//
//        } else {
//            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
//        }
////        filePath.substring(filePath.lastIndexOf(".") + 1);
//
//        if (extension.toUpperCase().equals("JSON")) {
//            //code import
//        }else{
//            Toast.makeText(ImportDataActivity.this, R.string.error_import_data, Toast.LENGTH_SHORT).show();
//        }

        try {

            String selectedFilePath = FilePath.getPath(ImportDataActivity.this, uri);
            final File file = new File(selectedFilePath);

            FileInputStream stream = new FileInputStream(selectedFilePath);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
            /*  String jsonStr = "{\n\"data\": [\n    {\n        \"id\": \"1\",\n        \"title\": \"Farhan Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"2\",\n        \"title\": \"Noman Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"3\",\n        \"title\": \"Ahmad Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"4\",\n        \"title\": \"Mohsin Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"5\",\n        \"title\": \"Haris Shah\",\n        \"duration\": 10\n    }\n  ]\n\n}\n";
             */
            JSONObject jsonObj = new JSONObject(jsonStr);

            //loading
            ProgressDialog loading = new ProgressDialog(ImportDataActivity.this);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.setMessage("Loading...");
            loading.show();
            Thread mThread = new Thread() {
                @Override
                public void run() {
//            // Getting data JSON Array nodes
                    try {
                        JSONArray pasien = jsonObj.getJSONArray("data_pasien");
                        PatientTable patientTable = JApplication.getInstance().patientTable;
                        ArrayList<PatientModel> arrayList = new ArrayList<>();
                        for (int i = 0; i < pasien.length(); i++) {
                            JSONObject json = pasien.getJSONObject(i);
                            PatientModel patientModel = new PatientModel();
                            patientModel.setPatient_id(json.getString("id_pasien") + i);
                            String name = json.getString("nama");
                            patientModel.setName(name);
                            String[] splitStr = name.trim().split("\\s+");
                            if (splitStr.length > 0) {
                                patientModel.setFirst_name(splitStr[0]);
                                if (splitStr.length > 1) {
                                    String lastName = splitStr[1];
                                    for (int j = 2; j < splitStr.length; j++) {
                                        lastName += " " + splitStr[j];
                                    }
                                    patientModel.setSurname(lastName);
                                } else {
                                    patientModel.setSurname("");
                                }
                            } else {
                                patientModel.setFirst_name(name);
                            }

                            patientModel.setRegister_date(Global.getMillisDateFmt(json.getString("tgl_daftar"), "yyyy-MM-dd"));
                            patientModel.setGender_id(json.getInt("jenis_kelamin"));
                            patientModel.setDate_of_birth(Global.getMillisDateFmt(json.getString("tgl_lahir"), "yyyy-MM-dd"));
                            patientModel.setOccupation(json.getString("pekerjaan"));
                            patientModel.setContact_no(json.getString("no_telp"));
                            patientModel.setAddress_street_1(json.getString("alamat"));
                            patientModel.setPatient_remark_1(json.getString("keterangan_1"));
                            patientModel.setPatient_remark_2(json.getString("keterangan_2"));
                            patientModel.setUuid(UUID.randomUUID().toString());
                            patientModel.setAge(Global.getAgeMonth(patientModel.getDate_of_birth(), true, false));
                            patientModel.setId(json.getInt("seq"));
                            patientTable.insert(patientModel, false);
                            arrayList.add(patientModel);
                        }

                        JSONArray catatan_medis = jsonObj.getJSONArray("catatan_medis");
                        RecordTable recordTable = JApplication.getInstance().recordTable;

                        for (int i = 0; i < catatan_medis.length(); i++) {
                            JSONObject json = catatan_medis.getJSONObject(i);
                            RecordModel recordModel = new RecordModel();
                            recordModel.setUuid(UUID.randomUUID().toString());

                            recordModel.setRecord_date(Global.getMillisDateFmt(json.getString("tanggal"), "yyyy-MM-dd"));
                            recordModel.setWeight(json.getDouble("berat"));
                            recordModel.setTemperature(json.getDouble("temperatur"));
                            recordModel.setBlood_pressure_diastolic(json.getInt("tensi_1"));
                            recordModel.setAnamnesa(json.getString("anamnesa"));
                            recordModel.setPhysical_exam(json.getString("pemeriksaan_fisik"));
                            recordModel.setDiagnosa(json.getString("diagnosa"));
                            recordModel.setTherapy(json.getString("therapi"));
                            String kode = "";
                            String patientNameId = "";
                            int patient_id = json.getInt("seq_pasien");
                            for (int j = 0; j < arrayList.size(); j++) {
                                if (arrayList.get(j).getId() == patient_id) {
                                    kode = arrayList.get(j).getPatient_id();
                                    patientNameId = arrayList.get(j).getPatientNameId();
                                    break;
                                }
                            }
                            patient_id = patientTable.getIdByIdPatient(kode);
                            recordModel.setPatient_id(patient_id);
                            recordModel.setName(patientNameId);
                            recordModel.setId(json.getInt("seq"));
                            recordTable.insert(recordModel, false);

                        }
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Data berhasil di import", Snackbar.LENGTH_SHORT).show();
                        loading.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ImportDataActivity.this, getString(R.string.import_failed), Toast.LENGTH_SHORT).show();
                    }

                }
            };
            mThread.start();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ImportDataActivity.this, getString(R.string.import_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    uri = data.getData();
                    String stringUri = uri.toString();

                } catch (Exception e) {
                    e.printStackTrace();
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


    private void GetPermission() {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){  //kalo android 13 ga butuh permission
            //gausah ngapa ngapain
        }else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
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

}
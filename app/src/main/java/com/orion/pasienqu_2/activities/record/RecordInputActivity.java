package com.orion.pasienqu_2.activities.record;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import static org.apache.log4j.NDC.clear;

import static java.util.Collections.addAll;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Environment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.ZoomImage.ZoomImageActivity;
import com.orion.pasienqu_2.activities.billing.BillingInputActivity;
import com.orion.pasienqu_2.activities.calendar.CalendarInputActivity;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.activities.more.billing_template.BillingTemplateInputActivity;
import com.orion.pasienqu_2.activities.more.export.ExportActivity2;
import com.orion.pasienqu_2.activities.more.export.ExportSuccessActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.adapter.IcdAdapter;
import com.orion.pasienqu_2.adapter.RecordFileAdapter;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.FileUtil;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.LoginInformationModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordDiagnosaModel;
import com.orion.pasienqu_2.models.RecordFileModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.orion.pasienqu_2.models.WorkLocationModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecordInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtRecordDate, txtAnamnesa, txtPhysicalExam, txtWeight, txtTemperatur, txtSystolic, txtDiastolic, txtDiagnosa, txtIcd, txtTherapy;
    private TextInputLayout errPatient, errWeight, errTemperature, errLocation, errTypePatient;
    private AutoCompleteTextView txtPatient;
    private TextView txtBilling;
    private AutoCompleteTextView spinLocation, spinpatientType;
    private ImageButton  btnAddPatient, btnAnamnesa, btnPhysicalExam, btnDiagnosa, btnICD, btnTheraphy, btnFile, btnBilling;
    private Button btnSave, btnCancel, btnPicture, btnDelete;
    private ImageView imgPicture;
    private RecyclerView rcvIcd;
    public IcdAdapter icdAdapter;
    public List<RecordDiagnosaModel> ListIcd = new ArrayList<>();
    private RecyclerView rcvFile;
    public RecordFileAdapter fileAdapter;
    public List<RecordFileModel> ListFile = new ArrayList<>();
    private String uuid, billingUuid = "";
    private int recordId, patientId, billingId, locationId, patientTypeId;
    private String mode = "";
    private List<String> listStringLocation;
    private List<String> listIdWorkLocation;
    private List<String> listStringPatient;
    private List<String> listStringBilling;
    private ArrayList<WorkLocationModel> listLocation;
    private ArrayList<PatientModel> listPatient;
    private ArrayList<BillingModel> listBilling = new ArrayList<>();
    private RecordTable recordTable;
    private WorkLocationTable workLocationTable;
    private PatientTable patientTable;
    private BillingTable billingTable;
    private Uri uri;
    private Bitmap idDefaultImg;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 111;
    public List<BillingModel> ListBilling = new ArrayList<>();
    private boolean isInputFromPatient = false;
    private String currentPhotopath;
    public List<BillingModel> ListBillingIds = new ArrayList<>();
    private List<String> listStringGroupPatient;
    private List<String> listIdGroupPatient;
    private List<String> listIdBilling = new ArrayList<>();
    private List<String> listUuidBilling = new ArrayList<>();
    private Uri imageUri;
    private long mLastClickTime = 0;
    private String imagePath = "";
    private String mediaLocation = JConst.mediaLocationPath;
    private String prescriptionFile;
    private final int REQ_CODE_CAMERA = 1;
    private final int REQ_CODE_GALLERY = 2;
    private final int REQ_CODE_FILE_CAMERA = 3;
    private final int REQ_CODE_FILE_GALLERY = 4;
    private KeyListener keyEdit;
    private ProgressBar loadingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_input);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        txtRecordDate = (TextInputEditText) findViewById(R.id.txtRecordDate);
        spinLocation = (AutoCompleteTextView) findViewById(R.id.spinLocation);
        txtPatient = (AutoCompleteTextView) findViewById(R.id.txtPatient);
        errPatient = (TextInputLayout) findViewById(R.id.layoutPatient);
        txtAnamnesa = (TextInputEditText) findViewById(R.id.txtAnamnesa);
        txtPhysicalExam = (TextInputEditText) findViewById(R.id.txtPhysicalExam);
        txtWeight = (TextInputEditText) findViewById(R.id.txtWeight);
        errWeight = (TextInputLayout) findViewById(R.id.layoutWeight);
        txtTemperatur = (TextInputEditText) findViewById(R.id.txtTemperature);
        errTemperature = (TextInputLayout) findViewById(R.id.layoutTemperature);
        txtSystolic = (TextInputEditText) findViewById(R.id.txtSystolic);
        txtDiastolic = (TextInputEditText) findViewById(R.id.txtDiastolic);
        txtDiagnosa = (TextInputEditText) findViewById(R.id.txtDiagnosa);
        txtTherapy = (TextInputEditText) findViewById(R.id.txtTheraphy);
        imgPicture = (ImageView) findViewById(R.id.imgPicture);
        txtBilling = (TextView) findViewById(R.id.txtBilling);
        errLocation = (TextInputLayout)findViewById(R.id.layoutWorkLocation);
        errTypePatient = (TextInputLayout) findViewById(R.id.layoutpatientType) ;

        btnAddPatient = (ImageButton) findViewById(R.id.btnAddPatient);
        btnAnamnesa = (ImageButton) findViewById(R.id.btnAnamnesa);
        btnPhysicalExam = (ImageButton) findViewById(R.id.btnPhysicalExam);
        btnDiagnosa = (ImageButton) findViewById(R.id.btnDiagnosa);
        btnICD = (ImageButton) findViewById(R.id.btnIcd);
        btnTheraphy = (ImageButton) findViewById(R.id.btnTheraphy);
        btnPicture = (Button) findViewById(R.id.btnPicture);
        btnFile = (ImageButton) findViewById(R.id.btnFiles);
        btnBilling = (ImageButton) findViewById(R.id.btnBilling);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        workLocationTable = ((JApplication) getApplicationContext()).workLocationTable;
        patientTable = ((JApplication) getApplicationContext()).patientTable;
        billingTable = ((JApplication) getApplicationContext()).billingTable;
        recordTable = ((JApplication) getApplicationContext()).recordTable;

        rcvIcd = (RecyclerView) findViewById(R.id.rcvIcd);
        icdAdapter = new IcdAdapter(RecordInputActivity.this, ListIcd, R.layout.record_icd_list_item);

        rcvFile = (RecyclerView) findViewById(R.id.rcvFiles);
        fileAdapter = new RecordFileAdapter(RecordInputActivity.this, RecordInputActivity.this, ListFile, R.layout.record_file_list_item, mode);

        spinpatientType = (AutoCompleteTextView) findViewById(R.id.spinpatientType);
        loadingText = findViewById(R.id.loadingText);
        loadingText.setVisibility(View.GONE);
    }

    private void InitClass(){
        keyEdit = txtPatient.getKeyListener();
        billingId = 0;
        patientId = 0;
//        idDefaultImg = getResources().getIdentifier("drawable/defaultimg", null, null);
        idDefaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.defaultimg);
        imgPicture.setImageBitmap(idDefaultImg);
        prescriptionFile = "";
        btnDelete.setVisibility(View.GONE);

        fillAdapter();  //isi semua list item

        //set default location yg dipilih
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        if (!loginInformationModel.getUuidLocation().equals("")) {
            WorkLocationTable workLocationTable = JApplication.getInstance().workLocationTable;
            WorkLocationModel workLocationModel = workLocationTable.getDataByUuid(loginInformationModel.getUuidLocation());
            spinLocation.setText(workLocationModel.getName());
            locationId = workLocationModel.getId();
        }else{
            spinLocation.setText("");
            locationId = 0;
        }


        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        patientId = extra.getInt("patient_id");
        isInputFromPatient = extra.getBoolean("patient_input");
        if (uuid.equals("")){
            mode = "add";
            fileAdapter.setMode(mode);
        }else {
            mode = "detail";
            fileAdapter.setMode(mode);
            loadData();
        }

        if (patientId != 0){
            PatientModel patientModel = patientTable.getRecordById(patientId);
            txtPatient.setText(patientModel.getPatientNameId());
        }

        //isi patient
/*        patientTable = new PatientTable(RecordInputActivity.this);
        listStringPatient = new ArrayList<>();
        listPatient = patientTable.getRecords();
        String patientName = "";
        for (int i = 0; i < listPatient.size(); i++){
            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(RecordInputActivity.this));
            if (patientId == listPatient.get(i).getId()) {
                patientName = listPatient.get(i).getPatientNameId();
            }
        }
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(RecordInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
        // Create a custom ArrayAdapter with custom filtering
        patientAdapter.getFilter().filter(getPatientFilter(patientAdapter).toString());
        txtPatient.setAdapter(patientAdapter);
        txtPatient.setText(patientName);*/

        if (isInputFromPatient){
            getpatientType(patientId);
        }

        setTitleInput();
        resetData();
        setEnabledComponent();
    }

    private void EventClass(){
        btnAddPatient.setOnClickListener(view -> {
            Intent s = new Intent(RecordInputActivity.this, PatientInputActivity.class);
            s.putExtra("uuid", "");
            s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(s,51);
        });

        txtPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                String selection = (String) adapterView.getItemAtPosition(position);

                for (int i = 0; i < listPatient.toArray().length; i++) {
                    if (listPatient.get(i).getIDPatientNameGenderAge(RecordInputActivity.this).equals(selection)) {
                        patientId = listPatient.get(i).getId();
                        txtPatient.setText(listPatient.get(i).getPatientNameId());
                        break;
                    }
                }

                getpatientType(patientId);
            }
        });

        txtPatient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String value = v.getText().toString();
                    if (value.length() < 3){
                        return false;
                    }else {
                        performSearch(value);
                        return true;
                    }
                }
                return false;
            }
        });

        txtPatient.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // Kehilangan fokus, atur hint jika tidak ada teks
                    if (txtPatient.getText().length() == 0) {
                        txtPatient.setHint(R.string.hint_search);
                    }
                } else {
                    // Mendapatkan fokus, hilangkan hint
                    txtPatient.setHint("");
                }
            }
        });

        spinLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinLocation.showDropDown();
            }
        });

        spinLocation.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                locationId = Integer.parseInt(listIdWorkLocation.get(position));
            }
        });

        btnAnamnesa.setOnClickListener(view -> {
                ShowDialog.showTemplate(RecordInputActivity.this, txtAnamnesa, uuid);
        });

        btnPhysicalExam.setOnClickListener(view -> {
                ShowDialog.showTemplate(RecordInputActivity.this, txtPhysicalExam, uuid);
        });

        btnDiagnosa.setOnClickListener(view -> {
                ShowDialog.showTemplate(RecordInputActivity.this, txtDiagnosa, uuid);
        });

        btnTheraphy.setOnClickListener(view -> {
                ShowDialog.showTemplate(RecordInputActivity.this, txtTherapy, uuid);
        });

        btnPicture.setOnClickListener(view -> {
                showPictureDialog(true);
        });

        btnFile.setOnClickListener(view -> {
            showPictureDialog(false);
//            GetPermission();
//            getFile();
        });

        btnBilling.setOnClickListener(view -> {
            if (isValid()) {
                if (TextUtils.isEmpty(txtPatient.getText())) {
                    errPatient.setError(getString(R.string.error_patient));
                    Toast.makeText(this, getString(R.string.field_must_be_fill), Toast.LENGTH_LONG).show();
                    return;
                }else{
                    errPatient.setErrorEnabled(false);
                }
                if (mode.equals("add")) {
                    recordId = (recordTable.getMaxId())+1;
                    Intent i = new Intent(RecordInputActivity.this, BillingInputActivity.class);
                    i.putExtra("uuid", billingUuid);
                    i.putExtra("billing_id", billingId);
                    i.putExtra("record_id", recordId);
                    i.putExtra("patient_id", patientId);
                    i.putExtra("medical_record", true);
                    i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, 41);
                }else if (mode.equals("edit")){
                    Intent i = new Intent(RecordInputActivity.this, RecordBillingActivity.class);
                    i.putExtra("uuid", billingUuid);
                    i.putExtra("billing_id", billingId);
                    i.putExtra("record_id", recordId);
                    i.putExtra("patient_id", patientId);
                    i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, 200);
                }else if (mode.equals("detail")){
                    Intent i = new Intent(RecordInputActivity.this, RecordBillingActivity.class);
                    i.putExtra("uuid", billingUuid);
                    i.putExtra("billing_id", billingId);
                    i.putExtra("record_id", recordId);
                    i.putExtra("patient_id", patientId);
                    i.putExtra("mode_detail", true);
                    i.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, 200);
                }
            }else{
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.field_must_be_fill), Snackbar.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(view -> {
            //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            if(saveForm()) {
//                syncUpData();
                //save billing
                if(mode.equals("add")) {
                    GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
                    globalTable.unSaveTemp("pasienqu_billing", billingUuid);

                    //update medical record id di tabel biling karena ada kasus saat pertama kali input billing ga terkait ( penyebabnya gara sudah pernah clear database)
                    int newId = recordTable.getMaxId();
                    globalTable.updateMedicalRecordId(newId, billingUuid);
                }else{
                    //save temp jika edit mode (billing lebih dari satu)
                    for(int i = 0; i < listBilling.size(); i++) {
                        if (listIdBilling.get(i).equals(String.valueOf(listBilling.get(i).getId()))) {
                            billingUuid = listBilling.get(i).getUuid();
                            GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
                            globalTable.unSaveTemp("pasienqu_billing", billingUuid);
                        }
                    }
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        btnCancel.setOnClickListener(view -> {
                onBackPressed();
                Global.hideSoftKeyboard(RecordInputActivity.this);
        });

        btnICD.setOnClickListener(view -> {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                RecordDiagnosaModel model = new RecordDiagnosaModel();
                model.setListIcd(ListIcd);
                }
            };

            ShowDialog.showICD10(RecordInputActivity.this, txtIcd, uuid, icdAdapter, runnable, ListIcd);
        });

        imgPicture.setOnClickListener(view -> {
            try{
                Intent i = new Intent(getApplicationContext(), ZoomImageActivity.class);
                i.putExtra("gambar", Global.getImageAsByteArray(imgPicture));
                startActivity(i);
            }catch (Exception e){
                Log.d("Error", "onClick: "+e.toString());
            }
        });

        spinpatientType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                patientTypeId = Integer.parseInt(listIdGroupPatient.get(position));
            }
        });

        btnDelete.setOnClickListener(view -> {
            Runnable runDelete = new Runnable() {
                @Override
                public void run() {
                    prescriptionFile = "";
                    imgPicture.setImageBitmap(null);
                    btnDelete.setVisibility(View.GONE);
                }
            };
            ShowDialog.confirmDialog(RecordInputActivity.this, getString(R.string.delete), getString(R.string.confirm_delete_picture), runDelete);
        });

        txtRecordDate.setOnClickListener(view -> {
            if (!mode.equals("detail")) {
                Global.datePickerClick(RecordInputActivity.this, txtRecordDate, view);
            }
        });
    }

    private void performSearch(String SearchKey) {
        loadingText.setVisibility(View.VISIBLE);
        Thread mThread = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    public void run() {
                        patientTable.setSearchQuery(SearchKey);
                        listStringPatient = new ArrayList<>();
                        listPatient = patientTable.getRecords();
                        for (int i = 0; i < listPatient.size(); i++){
                            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(RecordInputActivity.this));
                        }
                        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(RecordInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
                        // Create a custom ArrayAdapter with custom filtering
                        patientAdapter.getFilter().filter(getPatientFilter(patientAdapter).toString());
                        txtPatient.setAdapter(patientAdapter);
                        txtPatient.setText(SearchKey);
                        txtPatient.setSelection(SearchKey.length());
                        loadingText.setVisibility(View.GONE);
                    }
                });
            }
        };
        mThread.start();
    }

    private void getpatientType(int patientId) {
        patientTypeId = patientTable.getpatientTypeById(patientId);
        for (int i = 0; i < listStringGroupPatient.size(); i++){
            if (listIdGroupPatient.get(i).equals(String.valueOf(patientTypeId))){
                spinpatientType.setText(listStringGroupPatient.get(i));
            }
        }
    }


    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.medical_record_title)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.medical_record_title)));
        } else if (mode.equals("detail")) {
            this.setTitle(String.format(getString(R.string.detail_title), getString(R.string.medical_record_title)));
        }
    }

    private void fillAdapter(){


        //isi location
        listStringLocation = ListValue.list_work_location(getApplicationContext());
        listIdWorkLocation = ListValue.list_id_work_location(getApplicationContext());

        String[] mStringArrayWorkLocation = new String[listStringLocation.size()];
        mStringArrayWorkLocation = listStringLocation.toArray(mStringArrayWorkLocation);
        CustomAutoCompleteAdapter workLocationAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArrayWorkLocation);
        spinLocation.setAdapter(workLocationAdapter);
        locationId = 0;

        //isi Patient Type
        listStringGroupPatient = ListValue.list_patient_type(this);
        listIdGroupPatient = ListValue.list_id_patient_type(this);

        String[] mStringArraypatientType = new String[listStringGroupPatient.size()];
        mStringArraypatientType = listStringGroupPatient.toArray(mStringArraypatientType);
        CustomAutoCompleteAdapter patientTypeAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArraypatientType);
        spinpatientType.setAdapter(patientTypeAdapter);



//        //isi patient
//        patientTable = new PatientTable(RecordInputActivity.this);
//        listStringPatient = new ArrayList<>();
//        listPatient = patientTable.getRecords();
//        String patientName = "";
//        for (int i = 0; i < listPatient.size(); i++){
//            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge());
//            if (patientId == listPatient.get(i).getId()) {
//                patientName = listPatient.get(i).getPatientNameId();
//            }
//        }
//        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(RecordInputActivity.this, R.layout.spinner_item_style, listStringPatient);
//        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        txtPatient.setAdapter(patientAdapter);
//        txtPatient.setText(patientName);

        //isi adaper icd
        rcvIcd.setLayoutManager(new GridLayoutManager(RecordInputActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvIcd.setAdapter(icdAdapter);

        //isi adaper file
        rcvFile.setLayoutManager(new GridLayoutManager(RecordInputActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvFile.setAdapter(fileAdapter);
    }

    private void resetData(){
        if(mode.equals("add")) {
            txtRecordDate.setText(Global.serverNow());
            txtWeight.setText("0,0");
            txtTemperatur.setText("0,0");
            txtSystolic.setText("0");
            txtDiastolic.setText("0");
            txtBilling.setText("0");
        }
    }

    private void setEnabledComponent(){
        Global.setEnabledClickText(txtRecordDate, false);
        Global.setEnabledAutoCompleteText(txtPatient, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtAnamnesa, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtPhysicalExam, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtWeight, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtSystolic, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtTemperatur, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtDiastolic, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtDiagnosa, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtTherapy, !mode.equals("detail"));
        if (mode.equals("detail")) {
            spinLocation.setEnabled(false);
            spinpatientType.setEnabled(false);
            btnAddPatient.setVisibility(View.GONE);
            btnAnamnesa.setVisibility(View.GONE);
            btnPhysicalExam.setVisibility(View.GONE);
            btnDiagnosa.setVisibility(View.GONE);
            btnICD.setVisibility(View.GONE);
            btnTheraphy.setVisibility(View.GONE);
            btnPicture.setVisibility(View.GONE);
            btnFile.setVisibility(View.GONE);
//            btnBilling.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            errLocation.setEnabled(false);
            errTypePatient.setEnabled(false);
            txtPatient.setKeyListener(null);
            btnDelete.setVisibility(View.GONE);
            txtRecordDate.setEnabled(false);

//            txtPatient.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//            txtPatient.setSelected(true);
        }else {
            spinLocation.setEnabled(true);
            spinpatientType.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnAddPatient.setVisibility(View.VISIBLE);
            btnAnamnesa.setVisibility(View.VISIBLE);
            btnPhysicalExam.setVisibility(View.VISIBLE);
            btnDiagnosa.setVisibility(View.VISIBLE);
            btnICD.setVisibility(View.VISIBLE);
            btnTheraphy.setVisibility(View.VISIBLE);
            btnPicture.setVisibility(View.VISIBLE);
            btnFile.setVisibility(View.VISIBLE);
//            btnBilling.setVisibility(View.VISIBLE);
            errLocation.setEnabled(true);
            errTypePatient.setEnabled(true);
            txtPatient.setKeyListener(keyEdit);
            txtRecordDate.setEnabled(true);

            if (isInputFromPatient){
                btnAddPatient.setVisibility(View.GONE);
                Global.setEnabledAutoCompleteText(txtPatient, false);
            }

//            if (!prescriptionFile.isEmpty()){
            if (!TextUtils.isEmpty(prescriptionFile)){
                btnDelete.setVisibility(View.VISIBLE);
            }
        }
        invalidateOptionsMenu();
    }


    private void getFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(intent, REQ_CODE_FILE_GALLERY);
    }

    private void pictureFile(){

    }

    private void loadData(){
        RecordModel data = recordTable.getRecordById(uuid);
        txtRecordDate.setText(Global.getDateFormated(data.getRecord_date()));
        locationId = data.getWork_location_id();
        patientId = data.getPatient_id();
        recordId = data.getId();
//        txtPatient.setText(data.getName());
        PatientModel patientModel = patientTable.getRecordById(patientId);
        txtPatient.setText(patientModel.getPatientNameId());

        txtWeight.setText(String.format("%.2f", data.getWeight()));
        txtTemperatur.setText(String.format("%.2f", data.getTemperature()));
        txtSystolic.setText(String.valueOf(data.getBlood_pressure_systolic()));
        txtDiastolic.setText(String.valueOf(data.getBlood_pressure_diastolic()));
        txtAnamnesa.setText(data.getAnamnesa());
        txtPhysicalExam.setText(data.getPhysical_exam());
        txtDiagnosa.setText(data.getDiagnosa());
        txtTherapy.setText(data.getTherapy());

        //patientType
        patientTypeId = data.getpatient_type_id();
        for (int i = 0; i < listStringGroupPatient.size(); i++){
            if (listIdGroupPatient.get(i).equals(String.valueOf(patientTypeId))){
                spinpatientType.setText(listStringGroupPatient.get(i));
            }
        }

        //location
        for (int i = 0; i < listIdWorkLocation.size(); i++){
            if (listIdWorkLocation.get(i).equals(String.valueOf(locationId))){
                spinLocation.setText(listStringLocation.get(i));
            }
        }
        //pic
//        String picture = data.getPrescription_file();
//        if(picture.equals("")){
//            imgPicture.setImageBitmap(idDefaultImg);
//        }else if (!picture.equals("false")) {
//            byte[] imageByte = Base64.decode(picture, Base64.DEFAULT);
//            imgPicture.setImageBitmap(BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length));
//        }
        prescriptionFile = data.getPrescription_file();
//        if(prescriptionFile.equals("")){
        if(TextUtils.isEmpty(prescriptionFile)){
            imgPicture.setImageBitmap(idDefaultImg);
        }else{
            File imgFile = new  File(mediaLocation+"/"+prescriptionFile);
            if(imgFile.exists()){
                Bitmap decodeBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap bitmap = null;
                try {
                    bitmap = Global.rotateImageIfRequired(this, decodeBitmap, Uri.fromFile(imgFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgPicture.setImageBitmap(bitmap);
            }
        }
        //jika ada gambar munculin btn delete
        if (!TextUtils.isEmpty(prescriptionFile)){
            btnDelete.setVisibility(View.VISIBLE);
        }

        //load icd
        icdAdapter.addModels(data.getIcd_ids());
        icdAdapter.notifyDataSetChanged();
        //load file
//        RecordFileTable recordFileTable = new RecordFileTable(RecordInputActivity.this);
//        ListFile = recordFileTable.getRecordsById(uuid);


        fileAdapter.addModels(data.getFile_ids());
        fileAdapter.notifyDataSetChanged();


        txtBilling.setText(Global.FloatToStrFmt(data.getTotal_billing()));
        billingId = data.getBilling_id();
        BillingModel dataBilling = billingTable.getRecordById(billingId);
        billingUuid = dataBilling.getUuid();
//        txtBilling.setText(Global.FloatToStrFmt(dataBilling.getTotal_amount()));

        ListBilling = data.getBilling_ids();
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtPatient.getText())) {
            errPatient.setError(getString(R.string.error_patient));
        }else{
            errPatient.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(spinLocation.getText())) {
            errLocation.setError(getString(R.string.error_work_location));
        }else{
            errLocation.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(txtWeight.getText())) {
            errWeight.setError(getString(R.string.error_weight));
            return false;
        }else{
            errWeight.setErrorEnabled(false);
        }
        if (TextUtils.isEmpty(txtTemperatur.getText())) {
            errTemperature.setError(getString(R.string.error_temperature));
            return false;
        }else{
            errTemperature.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isAlreadyFill() {
        return  !TextUtils.isEmpty(txtPatient.getText()) ||
                !TextUtils.isEmpty(spinLocation.getText());
    }

    private boolean saveForm(){
        if (this.isValid()) {
            String anamnesa, name, physical_exam, diagnosa, therapy, prescription_file;
            int id, systolic, diastolic, work_location_id;
            long record_date, create_date, write_date;
            double weight,temperature, total_billing;

            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }
            record_date = Global.getMillisDate(txtRecordDate.getText().toString());
//            work_location_id = spinLocation.getSelectedItemPosition();
            name = txtPatient.getText().toString().trim();
//            weight = Global.StrFmtToFloat(txtWeight.getText().toString().replace('.', ','));
//            temperature = Global.StrFmtToFloat(txtTemperatur.getText().toString().replace('.', ','));
            weight = Global.StrFmtToFloatInput(txtWeight.getText().toString());
            temperature = Global.StrFmtToFloatInput(txtTemperatur.getText().toString());
            systolic = Integer.parseInt(txtSystolic.getText().toString());
            diastolic = Integer.parseInt(txtDiastolic.getText().toString());
            anamnesa = txtAnamnesa.getText().toString().trim();
            physical_exam = txtPhysicalExam.getText().toString().trim();
            diagnosa = txtDiagnosa.getText().toString().trim();
            therapy = txtTherapy.getText().toString().trim();
//            prescription_file = Global.getImageAsByteArray(imgPicture);
            total_billing = Global.StrFmtToFloat(txtBilling.getText().toString());
            create_date = Global.serverNowLong();
            write_date = Global.serverNowLong();

            if (!imagePath.equals("")) {
                prescription_file = getUidFile(imagePath, imageUri);
            } else {
                prescription_file = "";
            }

            //cek apakah ada perubahan pda gambar
            if (TextUtils.isEmpty(prescriptionFile) || (!prescriptionFile.equals(prescription_file) && !TextUtils.isEmpty(prescription_file))) {
                //delete ajah file yg lama biar ga menuh menuhin memori ngab
                File imgFile = new  File(mediaLocation+"/"+prescriptionFile);
                if(imgFile.exists()){
                    imgFile.delete();
                }
                prescriptionFile = prescription_file;
            }

            switch(this.mode.toString()) {
                case "add": {
                    RecordModel newData;
                    newData = new RecordModel(0, uuid, record_date, locationId, patientId, name,
                                                        weight, temperature, systolic, diastolic, anamnesa, physical_exam,
                                                        diagnosa, therapy, prescriptionFile, total_billing, billingId, patientTypeId,
                                                        create_date, write_date);

                    newData.setIcd_ids(ListIcd);
                    newData.setFile_ids(ListFile);
                    newData.setBilling_ids(ListBilling);

                    if (!recordTable.insert(newData, true)){
                        return false;
                    }
                    break;
                }

                case "edit": {
                    RecordModel data = recordTable.getRecordById(uuid);
                    data.setRecord_date(record_date);
                    data.setWork_location_id(locationId);
                    data.setPatient_id(patientId);
                    data.setName(name);
                    data.setWeight(weight);
                    data.setTemperature(temperature);
                    data.setBlood_pressure_systolic(systolic);
                    data.setBlood_pressure_diastolic(diastolic);
                    data.setAnamnesa(anamnesa);
                    data.setDiagnosa(diagnosa);
                    data.setPhysical_exam(physical_exam);
                    data.setTherapy(therapy);
                    data.setPrescription_file(prescriptionFile);
                    data.setTotal_billing(total_billing);
                    data.setBilling_id(billingId);
                    data.setIcd_ids(ListIcd);
                    data.setFile_ids(ListFile);
                    data.setpatient_type_id(patientTypeId);
                    data.setCreate_date(create_date);
                    data.setWrite_date(write_date);

                    if (!recordTable.update(data, true)){
                        return false;
                    }
                    break;
                }
            }



            return true;
        }
        return false;
    }

    private void showPictureDialog(boolean isOnlyPicture){
        LayoutInflater inflaterDetail;
        View alertLayoutDetail;
        inflaterDetail = getLayoutInflater();
        alertLayoutDetail = inflaterDetail.inflate(R.layout.layout_dialog_picture, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(RecordInputActivity.this);
        alert.setView(alertLayoutDetail);
        alert.setCancelable(true);

        final AlertDialog dialogDet = alert.create();
        dialogDet.show();
        dialogDet.setCancelable(true);
        dialogDet.setCanceledOnTouchOutside(true);

        final CardView gallery = (CardView) alertLayoutDetail.findViewById(R.id.crdGalery);
        final CardView camera = (CardView) alertLayoutDetail.findViewById(R.id.crdCamera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnlyPicture) {
                    galeryPicture(REQ_CODE_GALLERY); // jika yg di klik adalah btn picture => (munculkan only picture file)
                }else{
                    GetPermission();  //jika yg di klik adalah btn file => (munculkan all file)
                }
                dialogDet.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnlyPicture) {
                    cameraPicture(REQ_CODE_CAMERA);
                }else{
                    cameraPicture(REQ_CODE_FILE_CAMERA);
                }
                dialogDet.dismiss();
            }
        });
        dialogDet.show();
    }

    private void cameraPicture(int reqCode) {

        String fileName = "photo";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(fileName,".jpg", storageDir);
            currentPhotopath = imageFile.getAbsolutePath();
            imageUri = FileProvider.getUriForFile(RecordInputActivity.this,
                    getApplicationContext().getPackageName() + ".provider", imageFile);

            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePicture, reqCode);

            imagePath = currentPhotopath;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void galeryPicture(int reqCode) {
        try {
//            if (ActivityCompat.checkSelfPermission(RecordInputActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(RecordInputActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                if (reqCode == REQ_CODE_GALLERY) {
                    startActivityForResult(galleryIntent, REQ_CODE_GALLERY);
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQ_CODE_FILE_GALLERY:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getClipData() != null){
                            for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                                uri = data.getClipData().getItemAt(index).getUri();
                                Log.d("filesUri [" + uri + "] : ", String.valueOf(uri) );

                                try {
                                    InputStream iStream = getContentResolver().openInputStream(uri);
                                    addFile(Global.getBytes(iStream), FileUtil.getFileName(this, uri), FileUtil.getMime(this, uri), uri);
                                }catch (FileNotFoundException e){
                                }catch (IOException e){
                                }
                            }
                        }else{
                            uri = data.getData();
                            Log.d("fileUri: ", String.valueOf(uri));
//                            String path = FileUtil.getPath(RecordInputActivity.this, uri);
//                            File file = new File(path);
//                            addFile(Global.getStringFile(file), getFileName(uri), getMime(uri));

                            try {
                                InputStream iStream = getContentResolver().openInputStream(uri);
                                addFile(Global.getBytes(iStream), FileUtil.getFileName(this, uri), FileUtil.getMime(this, uri), uri);
                            }catch (FileNotFoundException e){
                            }catch (IOException e){
                            }
                        }

                    }
                }
            break;

            case REQ_CODE_FILE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream iStream = getContentResolver().openInputStream(imageUri);
                        addFile(Global.getBytes(iStream), FileUtil.getFileName(this, imageUri), FileUtil.getMime(this, imageUri), imageUri);
                    }catch (FileNotFoundException e){
                    }catch (IOException e){
                    }

                }else{
                    imagePath = "";
                    imageUri = null;
                }
                break;

            //ambil dari camera
            case REQ_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        imgPicture.setImageBitmap(Global.handleSamplingAndRotationBitmap(this, imageUri));
                        btnDelete.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    imagePath = "";
                    imageUri = null;
                }
            break;

            //ambil dari galery
            case REQ_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
//                    imagePath = Global.getPathFromUri(getApplicationContext(), imageUri);
                    imagePath = FileUtil.getFilePathFromURI(imageUri, this);

                    try {
                        imgPicture.setImageBitmap(Global.handleSamplingAndRotationBitmap(this, imageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    imgPicture.setImageURI(selectedImage);
                }
            break;

            case 41:
                if (resultCode == RESULT_OK) {
                    billingId = billingTable.getMaxId();
                    double totalBilling = 0;
                    listBilling = billingTable.getTempRecords();
                    listStringBilling = new ArrayList<>();

                    for (int i = 0; i < listBilling.size(); i++) {
                        if (billingId == listBilling.get(i).getId()) {
                            totalBilling = listBilling.get(i).getTotal_amount();
                            billingUuid = listBilling.get(i).getUuid();
                        }
                    }
                    txtBilling.setText(Global.FloatToStrFmt(totalBilling));
                    ListBilling = listBilling;


                }
                break;

            case 51:
                if (resultCode == RESULT_OK) {
                    patientId = patientTable.getMaxId();
//                    String patientName = "";
//                    listStringPatient = new ArrayList<>();
//                    listPatient = patientTable.getRecords();
//
//                    for (int i = 0; i < listPatient.size(); i++) {
//                        listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(RecordInputActivity.this));
//                        if (patientId == listPatient.get(i).getId()) {
//                            patientName = listPatient.get(i).getPatientNameId();
//                        }
//                    }
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                            R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
//                    txtPatient.setAdapter(adapter);
//                    txtPatient.setText(patientName);

                    PatientModel patientModel = patientTable.getRecordById(patientId);
                    txtPatient.setText(patientModel.getPatientNameId());
                    getpatientType(patientId);
                }
                break;

            case 200:
                if (resultCode == RESULT_OK) {
                    Bundle extra = data.getExtras();
                    String total= extra.getString("total_billing");

                    //dapetin id billing yg lebih dari satu buat  atur save temp nanti
                    listBilling = billingTable.getTempRecords();
                    for (int j = 0; j < listBilling.size(); j++) {
                        listIdBilling.add(String.valueOf(listBilling.get(j).getId()));
                        listUuidBilling.add(listBilling.get(j).getUuid());
                    }
                    listIdBilling.size();
                    txtBilling.setText(total);
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        //delete savetemp billing
        if(mode.equals("add")) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    billingTable.deleteWithItem(billingUuid, billingId);
                    finish();
                }
            };

            if (isAlreadyFill()){
                ShowDialog.confirmDialog(RecordInputActivity.this, getString(R.string.app_name), getString(R.string.confirm_cancel_input), runnable);
            }else {
                finish();
            }

        }else{
            //delete billing lebih dari satu saat mode edit
            for (int i = 0; i < listBilling.size(); i++) {
                billingId = Integer.parseInt(listIdBilling.get(i));
                billingUuid = listUuidBilling.get(i);
                billingTable.deleteWithItem(billingUuid, billingId);
            }
            finish();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_edit:
                this.mode = "edit";
                fileAdapter.setMode(mode);
                setEnabledComponent();
                setTitleInput();
                return true;
            case R.id.menu_view:
                this.mode = "detail";
                fileAdapter.setMode(mode);
                setEnabledComponent();
                setTitleInput();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_record", uuid, "pasienqu.medical.record");

                        //update juga ke billing terkait
                        for (int i = 0; i < ListBilling.size(); i++) {
                            billingUuid = ListBilling.get(i).getUuid();
                            globalTable.archive("pasienqu_billing", billingUuid, "pasienqu.medical.record");
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(RecordInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.medical_record_title)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_record", uuid, "pasienqu.medical.record");

                        //update juga ke billing terkait
                        for (int i = 0; i < ListBilling.size(); i++) {
                            billingUuid = ListBilling.get(i).getUuid();
                            globalTable.unarchive("pasienqu_billing", billingUuid, "pasienqu.medical.record");
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(RecordInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.medical_record_title)), runUnarchive);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_record_input, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(false); //item edit
        menu.getItem(1).setVisible(false); //item view
        menu.getItem(2).setVisible(false); //item archive
        menu.getItem(3).setVisible(false); //item unarchive
        if ((this.mode != null) && (this.mode.equals("detail"))) {
            menu.getItem(0).setVisible(true);
        }
        if ((this.mode != null) && (this.mode.equals("edit"))){
            menu.getItem(1).setVisible(true);
        }
        if ((this.mode != null) && (!this.mode.equals("add"))) {
            GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
            boolean isArchive = globalTable.isArchived("pasienqu_record", uuid);
            menu.getItem(2).setVisible(!isArchive);
            menu.getItem(3).setVisible(isArchive);
        }
        return true;
    }


    private void addFile(String file, String fileName, String mimeType,  Uri uri){
        RecordFileModel recordFileModel = new RecordFileModel();
        recordFileModel.setFile_name(fileName);
        recordFileModel.setMime_type(mimeType);
//        if (mimeType.equals("jpg")){
//            Bitmap image = Global.BitmapFileFromUri(uri, this);
//            ImageView tempImagePicture = new ImageView(this);
//            tempImagePicture.setImageBitmap(image);
//        }
        String newPath = FileUtil.getFilePathFromURI(uri, this);
        file = getUidFile(newPath, uri);
        recordFileModel.setRecord_file(file);
        recordFileModel.setCreate_date(Global.serverNowLong());
        recordFileModel.setWrite_date(Global.serverNowLong());

        fileAdapter.addModel(recordFileModel);
//        fileAdapter.notifyDataSetChanged();
    }


    private void GetPermission(){
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){  //kalo android 13 ga butuh permission
            getFile();
        }else if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permission request code is just an int
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS); //permisison request code is just an int
            }
        }else {
            getFile();
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

    private void syncUpData(){
        if(Global.CheckConnectionInternet(RecordInputActivity.this)){
            SyncUp.sync_all(RecordInputActivity.this, getApplicationContext(), ()->{});
        }
    }

    private String getUidFile(String srcLocation, Uri fileUri){
        String mimetype = FileUtil.getMime(this, fileUri);
        File srcCompressed = null;

        File folder = new File(JConst.mediaLocationPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = UUID.randomUUID().toString() +"."+mimetype;
        String dstLocation = folder +"/"+ fileName;

        File src = new File(srcLocation);
        if (mimetype.equals("jpg")) {
//            srcCompressed = Global.compressBitmapFile(src);
            srcCompressed = Global.handleRotateCompressedBitmapFile(src, fileUri, this);
        }
        File dst = new File(dstLocation);

        try {
            if (mimetype.equals("jpg")) {
                FileUtil.copyFile(srcCompressed, dst);
            }else{
                FileUtil.copyFile(src, dst);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    // Define the custom filter method
    private Filter getPatientFilter(final ArrayAdapter<String> adapter) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                if (constraint != null) {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (String item : listStringPatient) {
                        // Modify this condition to match your filtering criteria
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                if (results != null && results.count > 0) {
                    adapter.addAll((List<String>) results.values);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetInvalidated();
                }
            }
        };
    }
}

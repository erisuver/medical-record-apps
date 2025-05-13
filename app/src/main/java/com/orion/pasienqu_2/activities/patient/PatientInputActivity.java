package com.orion.pasienqu_2.activities.patient;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.more.data_migration.DataMigrationActivity;
import com.orion.pasienqu_2.activities.more.note_template.NoteTemplateInputActivity;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.activities.satu_sehat.location.LocationSatuSehatInputActivity;
import com.orion.pasienqu_2.adapter.CustomAutoCompleteAdapter;
import com.orion.pasienqu_2.adapter.HistoryRecordAdapter;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.RecordModel;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class PatientInputActivity extends CustomAppCompatActivity {

    private TextInputEditText txtPatientId, txtFirstName, txtSurname, txtAge, txtMonth, txtDateBirth, txtDescription;
    private TextInputEditText txtRegisterDate, txtIdNumber, txtEmail, txtOccupation, txtContact, txtAddress1, txtAddress2, txtRemark1, txtRemark2;
    private TextInputLayout errPatientId, errFirstName, errGender, errDateBirth, errEmail, layoutIdNumber;
    private AutoCompleteTextView spinGender, spinpatientType;
    private Button btnSave, btnCancel;
    private ImageButton btnRemark;
    private ExpandableLayout expandProfile;//, expandMedicRecord;
    private TextView tvExTitle, tvNameDet, tvAgeDet, tvPatientType;
    private PatientTable patientTable;
    private GenderTable genderTable;
    private RecordTable recordTable;
    private List<String>listStringGender;
    private List<String> listStringRecord;
    private ArrayList<GenderModel> listGender;
    private ArrayList<RecordModel> listRecord;
    public HistoryRecordAdapter historyRecordAdapter;
    public List<RecordModel> ListItemRecord = new ArrayList<>();
    private RecyclerView rcvLoad;
    private View layoutBody, layoutDetail;
    private int patientId;
    private String uuid;
    private String mode = "";
    private int genderId;
    private int recordId;
    private TextView tvTitleProfile, tvTitleRecord;
    private boolean isInputMedical = false;

    private List<String> listStringGroupPatient;
    private List<String> listIdGroupPatient;
    private int patientTypeId;
    private TextInputLayout txtLayoutDescription;

    private long mLastClickTime = 0;
    private boolean isDateOfBirth = false;
    private TextInputEditText txtIdNumberInput;
    private AppCompatImageView imgSatuSehat, imgSatuSehatInput;
    private PatientModel dataPatient = new PatientModel();
    private boolean isCallFromSatuSehat;
    private boolean isBelumAdaClient;
    private final int REQUEST_LOV_PATIENT_SATU_SEHAT = 999;
    private boolean isTglSalingTerkait = true; //flag untuk membedakan apakah perlu saling ubah antara datebirt dan umur.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_input);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        layoutBody = findViewById(R.id.layoutBody);
        txtPatientId = findViewById(R.id.txtPatientId);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtSurname = findViewById(R.id.txtSurname);
        spinGender = findViewById(R.id.spinGender);
        txtAge = findViewById(R.id.txtAge);
        txtMonth = findViewById(R.id.txtMonth);
        txtDateBirth = findViewById(R.id.txtDateBirth);
        errPatientId = findViewById(R.id.layoutPatientId);
        errFirstName = findViewById(R.id.layoutFirstName);
        errGender = findViewById(R.id.layoutGender);
        errDateBirth = findViewById(R.id.layoutDateBirth);
        errEmail = findViewById(R.id.layoutEmail);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        spinpatientType = findViewById(R.id.spinpatientType);
        txtDescription = findViewById(R.id.txtDescription);
        txtLayoutDescription = findViewById(R.id.layoutDescription);

        patientTable = ((JApplication) getApplicationContext()).patientTable;
        genderTable = ((JApplication) getApplicationContext()).genderTable;
        recordTable = ((JApplication) getApplicationContext()).recordTable;
        
        //patient detail
        layoutDetail = findViewById(R.id.layoutDetail);
        tvNameDet = findViewById(R.id.tvNameDet);
        tvAgeDet = findViewById(R.id.tvAgeDet);
        tvPatientType = findViewById(R.id.tvPatientType);

        //profile
        expandProfile = findViewById(R.id.expandable);
        tvTitleProfile = expandProfile.parentLayout.findViewById(R.id.tvExTitle);
        txtRegisterDate = findViewById(R.id.txtRegisterDate);
        txtIdNumber = findViewById(R.id.txtIdNumber);
        txtEmail = findViewById(R.id.txtEmail);
        txtOccupation = findViewById(R.id.txtOccupation);
        txtContact = findViewById(R.id.txtContact);
        txtAddress1 = findViewById(R.id.txtAddress1);
        btnRemark = findViewById(R.id.btnRemark);
        txtAddress2 = findViewById(R.id.txtAddress2);
        txtRemark1 = findViewById(R.id.txtRemark1);
        txtRemark2 = findViewById(R.id.txtRemark2);
        
        //medical record
//        expandMedicRecord = (ExpandableLayout) findViewById(R.id.expandableRecord);
//        tvTitleRecord = (TextView) expandMedicRecord.parentLayout.findViewById(R.id.tvExTitle);
//        rcvLoad = (RecyclerView) expandMedicRecord.secondLayout.findViewById(R.id.rcvLoad);
        tvTitleRecord = findViewById(R.id.tvTitleRecord);
        rcvLoad = findViewById(R.id.rcvLoad);
        historyRecordAdapter = new HistoryRecordAdapter(PatientInputActivity.this, ListItemRecord, R.layout.patient_record_history_list);

        //penambahan satu sehat
        txtIdNumberInput = findViewById(R.id.txtIdNumberInput);
        imgSatuSehatInput = findViewById(R.id.imgSatuSehatInput);
        imgSatuSehat = findViewById(R.id.imgSatuSehat);
        layoutIdNumber = findViewById(R.id.layoutIdNumber);
    }

    private void InitClass(){
        genderId = 0;
        tvTitleRecord.setText(getString(R.string.medical_records) +"(3)");
        txtRegisterDate.setText(Global.serverNow());
        recordId = 0;
        //isi gender
        listGender = genderTable.getRecords();
        listStringGender = new ArrayList<>();
        for (int i = 0; i < listGender.size(); i++){
            listStringGender.add(listGender.get(i).getName(PatientInputActivity.this));
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listStringGender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(genderAdapter);

        //isi Patient Type
        listStringGroupPatient = ListValue.list_patient_type(this);
        listIdGroupPatient = ListValue.list_id_patient_type(this);

        String[] mStringArraypatientType = new String[listStringGroupPatient.size()];
        mStringArraypatientType = listStringGroupPatient.toArray(mStringArraypatientType);
        CustomAutoCompleteAdapter patientTypeAdapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, mStringArraypatientType);
        spinpatientType.setAdapter(patientTypeAdapter);
        //default type = umum
        spinpatientType.setText(getString(R.string.patient_type_general));
        patientTypeId = 1;



        //auto id
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        boolean isAutoId = sharedPreferences.getBoolean("autogenerate_patient_id", true);
        if(isAutoId) {
            String maxId = patientTable.getMaxPatientId();
            int autoId = Integer.parseInt(maxId)+1;
            //cek apakah auto id sudah ada / samaan di database
            int count = Global.getCount(JApplication.getInstance().db, "pasienqu_patient", " patient_id = '" +String.format("%07d", autoId)+"'");
            if (count > 0){
                autoId += 1;
            }
            txtPatientId.setText(String.format("%07d", autoId));
            Global.setEnabledClickText(txtPatientId, false);
        }

        rcvLoad.setLayoutManager(new GridLayoutManager(PatientInputActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(historyRecordAdapter);

        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            uuid = extra.getString("uuid");
            isCallFromSatuSehat = extra.getBoolean("isCallFromSatuSehat");
            if (uuid.equals("")) {
                mode = "add";
            } else if (isCallFromSatuSehat) {
                mode = "edit";
                loadData();
            } else {
                mode = "detail";
                loadData();
            }
        }

        setEnabledComponent();
        setTitleInput();
    }

    private void LoadHisoryRecord() {
        historyRecordAdapter.removeAllModel();
        historyRecordAdapter.addModels(recordTable.getRecordsHistory(patientId));
        historyRecordAdapter.notifyDataSetChanged();
    }

    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.patient)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.patient)));
        } else if (mode.equals("detail")) {
            this.setTitle(String.format(getString(R.string.detail_title), getString(R.string.patient)));
        }
    }

    private void EventClass(){
        expandProfile.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandProfile.toggleLayout();
                if(expandProfile.isExpanded()){
                    tvTitleProfile.setTextColor(Color.parseColor("#FF000000"));
                }else{
                    tvTitleProfile.setTextColor(Color.parseColor("#FF005EB8"));
                }
            }
        });

//        expandMedicRecord.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                expandMedicRecord.toggleLayout();
//                if(expandMedicRecord.isExpanded()){
//                    tvTitleRecord.setTextColor(Color.parseColor("#FF000000"));
//                }else{
//                    tvTitleRecord.setTextColor(Color.parseColor("#FF005EB8"));
//                }
//            }
//        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if(saveForm()) {
//                    syncUpData();

//                    if (!txtIdNumberInput.getText().toString().isEmpty()){
//                        cekSatuSehatByNIK(true);
//                    }else{
//                        setResult(RESULT_OK);
//                        finish();
//                    }

                    setResult(RESULT_OK);
                    finish();
                }
                btnSave.setEnabled(true);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Global.hideSoftKeyboard(PatientInputActivity.this);
            }
        });

        btnRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uuid = "";
                ShowDialog.showTemplate(PatientInputActivity.this, txtRemark1, uuid);
            }
        });


        spinGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinGender.showDropDown();
            }
        });

        spinGender.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
               
                for (int i = 0; i < listStringGender.toArray().length; i++) {
                    if (listStringGender.get(i).equals(selection)) {
                        genderId = i+1;
                        break;
                    }
                }
            }
        });

//        txtDateBirth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        getAge();
//                    }
//                };
//                Global.dtpClickDisableFutureDate(PatientInputActivity.this, txtDateBirth, view, false, runnable);
//            }
//        });
        txtDateBirth.setOnClickListener(view -> {
            isDateOfBirth = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    getAge();
                    isDateOfBirth = false;
                }
            };
            ShowDialog.birthDateDialog(this, txtDateBirth, runnable);
        });

        txtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int age, month = 0;
                if (txtAge.getText().toString().equals("")){
                    age = 0;
                }else{
                    age = Integer.parseInt(txtAge.getText().toString());
                }
                if (txtMonth.getText().toString().equals("")){
                    month = 0;
                }else{
                    month = Integer.parseInt(txtMonth.getText().toString());
                }

                if (!isDateOfBirth && isTglSalingTerkait) {
                    Global.getBirthofDate(age, month, txtDateBirth, txtDateBirth.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int age, month = 0;
                if (txtAge.getText().toString().equals("")){
                    age = 0;
                }else{
                    age = Integer.parseInt(txtAge.getText().toString());
                }
                if (txtMonth.getText().toString().equals("")) {
                    month = 0;
                }else if (Integer.parseInt(txtMonth.getText().toString())  > 11){
                    txtMonth.setText("1");
                    Snackbar.make(findViewById(R.id.layout_patient_input), getString(R.string.patient_month_input_error), Snackbar.LENGTH_LONG).show();
                }else{
                    month = Integer.parseInt(txtMonth.getText().toString());
                }

                if (!isDateOfBirth && isTglSalingTerkait) {
                    Global.getBirthofDate(age, month, txtDateBirth, txtDateBirth.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spinpatientType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                if (position > 0){
                    txtDescription.requestFocus();
                    txtLayoutDescription.setVisibility(View.VISIBLE);
                }else{
                    txtDescription.setText("");
                    txtLayoutDescription.setVisibility(View.GONE);
                }

                patientTypeId = Integer.parseInt(listIdGroupPatient.get(position));
            }
        });

        imgSatuSehatInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtIdNumberInput.getText().toString().trim())) {
                    String pesan = "Nomor Identifikasi Belum di isi. ";
                    showDialogLovPatient(pesan);
                    return;
                }

                /*if (Global.getCount(JApplication.getInstance().db, "pasienqu_patient"," identification_no = '" + txtIdNumberInput.getText().toString().trim() + "' and uuid <> '"+dataPatient.getUuid()+"'") > 0 ) {
                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_unique), getString(R.string.identification_number)), Toast.LENGTH_SHORT).show();
                    return;
                }*/
                cekSatuSehatByNIK();
            }
        });

    }

    private void showDialogLovPatient(String pesan){
        Runnable runLovSearchPatient = new Runnable() {
            @Override
            public void run() {
                txtIdNumberInput.setText("");
                txtIdNumber.setText("");
                JApplication.patientIHS = "";
                Intent intent = new Intent(PatientInputActivity.this, LovPatientSatuSehatActivity.class);
                intent.putExtra("name", txtFirstName.getText().toString().trim() + " " + txtSurname.getText().toString().trim());
                intent.putExtra("date_birth", txtDateBirth.getText().toString());
                intent.putExtra("gender", genderId);
                startActivityForResult(intent, REQUEST_LOV_PATIENT_SATU_SEHAT);
            }
        };
        ShowDialog.confirmDialog(PatientInputActivity.this, getString(R.string.information), pesan+"Apakah anda ingin mencari berdasarkan nama, tanggal lahir dan jenis kelamin?", runLovSearchPatient);
    }

    private void cekSatuSehatByNIK() {
        //validasi
        if (!Global.CheckConnectionInternet(PatientInputActivity.this)) {
            ShowDialog.infoDialog(PatientInputActivity.this, getString(R.string.information), getString(R.string.must_be_online));
            return;
        }

        //runnable
        Runnable runTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgSatuSehatInput.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
                imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
            }
        };
        Runnable runTidakTerdaftar = new Runnable() {
            @Override
            public void run() {
                imgSatuSehatInput.setBackgroundResource(R.drawable.ic_satu_sehat_search_gray);
                imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_gray);
                String pesan = "Nomor Identifikasi yang diinput tidak terdaftar. ";
                showDialogLovPatient(pesan);
            }
        };
        Runnable runCheckPatientByNIK = new Runnable() {
            @Override
            public void run() {
                GlobalSatuSehat.checkPatientByNIK(PatientInputActivity.this, runTerdaftar, runTidakTerdaftar, txtIdNumberInput.getText().toString().trim());
            }
        };

        //jalankan
        GlobalSatuSehat.checkTokenSatuSehat(PatientInputActivity.this, runCheckPatientByNIK);
    }

    private void setEnabledComponent(){
        //profile layout
        Global.setEnabledTextInputEditText(txtRegisterDate, false);
        Global.setEnabledClickText(txtDateBirth, false);
        Global.setEnabledTextInputEditText(txtIdNumber, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtEmail, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtOccupation, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtContact, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtAddress1, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtAddress2, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtRemark1, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtRemark2, !mode.equals("detail"));


        if (mode.equals("detail")) {
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            layoutBody.setVisibility(View.GONE);
            layoutDetail.setVisibility(View.VISIBLE);
//            expandMedicRecord.setVisibility(View.VISIBLE);
//            expandMedicRecord.collapse();
            expandProfile.collapse();
            btnRemark.setVisibility(View.GONE);
            imgSatuSehat.setVisibility(View.VISIBLE);
            layoutIdNumber.setVisibility(View.VISIBLE);
            imgSatuSehat.setEnabled(false);
            rcvLoad.setVisibility(View.VISIBLE);
        }else {
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            layoutBody.setVisibility(View.VISIBLE);
            layoutDetail.setVisibility(View.GONE);
//            expandMedicRecord.setVisibility(View.GONE);
//            expandMedicRecord.setShowSpinner(false);
            tvTitleRecord.setVisibility(View.GONE);
            tvTitleProfile.setTextColor(Color.parseColor("#FF005EB8"));
            expandProfile.expand();
            imgSatuSehat.setVisibility(View.GONE);
            layoutIdNumber.setVisibility(View.GONE);
            imgSatuSehat.setEnabled(true);
            rcvLoad.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();
    }

    private void getAge(){
        long LDateNow = Global.serverNowLong();
        Long LDateBirth = Global.getMillisDate(txtDateBirth.getText().toString());

        Date dateBirth, dateNow;
        dateBirth = new Date(Integer.parseInt(Global.getTahun(LDateBirth)), Integer.parseInt(Global.getBulan(LDateBirth)),
                Integer.parseInt(Global.getHari(LDateBirth)));
        dateNow = new Date(Integer.parseInt(Global.getTahun(LDateNow)), Integer.parseInt(Global.getBulan(LDateNow)),
                Integer.parseInt(Global.getHari(LDateNow)));

        this.txtAge.setText(String.valueOf(Global.getUmur(dateNow, dateBirth, true)));
        this.txtMonth.setText(String.valueOf(Global.getUmur(dateNow, dateBirth, false)));
    }

    private int getAgeMonth(boolean age, boolean month){
        int result = 0;

        long LDateNow = Global.serverNowLong();
        Long LDateBirth = Global.getMillisDate(txtDateBirth.getText().toString());

        Date dateBirth, dateNow;
        dateBirth = new Date(Integer.parseInt(Global.getTahun(LDateBirth)), Integer.parseInt(Global.getBulan(LDateBirth)),
                Integer.parseInt(Global.getHari(LDateBirth)));
        dateNow = new Date(Integer.parseInt(Global.getTahun(LDateNow)), Integer.parseInt(Global.getBulan(LDateNow)),
                Integer.parseInt(Global.getHari(LDateNow)));

        if (age && !month){  //dapetin umur aja
            result = Global.getUmur(dateNow, dateBirth, true);
        }else if (!age && month){ //dapetin bulan aja
            result = Global.getUmur(dateNow, dateBirth, false);
        }else { //dapetin semua
            result = Global.getUmur(dateNow, dateBirth, true);
            result = Global.getUmur(dateNow, dateBirth, false);
        }

        return result;
    }


    private void loadData(){
        dataPatient = patientTable.getRecordByUuId(uuid);

        patientId = dataPatient.getId();
        uuid = dataPatient.getUuid();
        txtPatientId.setText(dataPatient.getPatient_id());
        txtFirstName.setText(dataPatient.getFirst_name());
        txtSurname.setText(dataPatient.getSurname());
        txtDateBirth.setText(Global.getDateFormated(dataPatient.getDate_of_birth()));
        txtRegisterDate.setText(Global.getDateFormated(dataPatient.getRegister_date()));
        txtIdNumber.setText(dataPatient.getIdentification_no());
        txtIdNumberInput.setText(dataPatient.getIdentification_no());
        txtEmail.setText(dataPatient.getEmail());
        txtOccupation.setText(dataPatient.getOccupation());
        txtContact.setText(dataPatient.getContact_no());
        txtAddress1.setText(dataPatient.getAddress_street_1());
        txtAddress2.setText(dataPatient.getAddress_street_2());

        if (dataPatient.getPatient_remark_1().equals(JConst.remark_1_value)){
            txtRemark1.setText(JConst.remark_1_text);
        }else if  (dataPatient.getPatient_remark_1().equals(JConst.remark_2_value)){
            txtRemark1.setText(JConst.remark_2_text);
        }else if  (dataPatient.getPatient_remark_1().equals(JConst.remark_3_value)){
            txtRemark1.setText(JConst.remark_3_text);
        }else if  (dataPatient.getPatient_remark_1().equals(JConst.remark_4_value)){
            txtRemark1.setText(JConst.remark_4_text);
        }else if  (dataPatient.getPatient_remark_1().equals(JConst.remark_5_value)){
            txtRemark1.setText(JConst.remark_5_text);
        }else {
            txtRemark1.setText(dataPatient.getPatient_remark_1());
        }

        txtRemark2.setText(dataPatient.getPatient_remark_2());
        txtDescription.setText(dataPatient.getDescription());

        genderId = dataPatient.getGender_id();
        for (int i = 0; i < listGender.size(); i++){
            if (listGender.get(i).getId() == genderId) {
                spinGender.setText(listStringGender.get(i), false);
            }
        }

        patientTypeId = dataPatient.getPatient_type_id();
        String patientTypeText = "";
        for (int i = 0; i < listStringGroupPatient.size(); i++){
            if (listIdGroupPatient.get(i).equals(String.valueOf(patientTypeId))){
                patientTypeText = listStringGroupPatient.get(i);
            }
        }
        spinpatientType.setText(patientTypeText);
        if (patientTypeId > 1 ){
            txtLayoutDescription.setVisibility(View.VISIBLE);
        }

        getAge();
        long LDateBirth = Global.getMillisDate(txtDateBirth.getText().toString());
        tvNameDet.setText(dataPatient.getPatientNameId());
        tvNameDet.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvNameDet.setSelected(true);

        tvAgeDet.setText(dataPatient.getGenderInitialStr(this)+"/"+getAgeMonth(true, false) + " " + getString(R.string.years) + ", "+ getAgeMonth(false, true) +" "+ getString(R.string.months)
                        + " ("+Global.getDateFormated(LDateBirth, "dd MMM yyyy")+")");
//        tvNameDet.setText(dataPatient.getPatientNameGenderAge());
//        tvAgeDet.setText(dataPatient.getAge() + " " + getString(R.string.years) + ", "+ dataPatient.getMonth() +" "+ getString(R.string.months));
        tvPatientType.setText(patientTypeText);
        int totalRecord = recordTable.getCountRecordPatient(patientId);
        tvTitleRecord.setText(getString(R.string.medical_records) +" ("+ totalRecord +")");

        if (isInputMedical){
            ListItemRecord = recordTable.getRecordsHistory(patientId);
            historyRecordAdapter.removeAllModel();
            historyRecordAdapter.addModels(ListItemRecord);
            historyRecordAdapter.notifyDataSetChanged();
            recreate(); //sementara buat refress list history record saat pertama kali tambahh record
        }
//       LoadHisoryRecord();
        ListItemRecord = dataPatient.getMedical_record_ids();
        historyRecordAdapter.removeAllModel();
        historyRecordAdapter.addModels(ListItemRecord);
        historyRecordAdapter.notifyDataSetChanged();
//        ViewGroup.LayoutParams params= rcvLoad.getLayoutParams();
//        params.height = 1150 * ListItemRecord.size();
//        rcvLoad.setLayoutParams(params);

        if (!TextUtils.isEmpty(dataPatient.getPatient_ihs())){
            imgSatuSehatInput.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
            imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(txtPatientId.getText().toString().trim())) {
            errPatientId.setError(getString(R.string.error_patient_id));
//            Snackbar.make(findViewById(R.id.layout_patient_input), getString(R.string.field_must_be_fill), Snackbar.LENGTH_LONG).show();
        }else{
            errPatientId.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtFirstName.getText().toString().trim())) {
            errFirstName.setError(getString(R.string.error_first_name));
        }else{
            errFirstName.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(spinGender.getText().toString().trim())) {
            errGender.setError(getString(R.string.error_gender));
        }else{
            errGender.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtDateBirth.getText())) {
            errDateBirth.setError(getString(R.string.error_date_birth));
        }else{
            errDateBirth.setErrorEnabled(false);
        }

//        if(!TextUtils.isEmpty(txtEmail.getText().toString().trim()) && !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
//            errEmail.setError(getString(R.string.error_invalid_email));
//        }

        return true;
    }



    private boolean saveForm() {
        if (this.isValid()) {
            String patient_id, firstName, surname, idNumber, email, occupation, address1, address2,
                    contact, patientRemark1, patientRemark2, description;
            int gender, age, month;
            long dateofBirth, registerDate;

            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }

            patient_id = txtPatientId.getText().toString().trim();
            firstName = txtFirstName.getText().toString().trim();
            surname = txtSurname.getText().toString().trim();
//            gender = genderId+1;
            if (!txtAge.getText().toString().equals("")) {
                age = Integer.parseInt(txtAge.getText().toString().trim());
            }else{
                age = 0;
            }
            if (!txtMonth.getText().toString().equals("")) {
                month = Integer.parseInt(txtMonth.getText().toString().trim());
            }else{
                month = 0;
            }
            dateofBirth = Global.getMillisDate(txtDateBirth.getText().toString());
            registerDate = Global.getMillisDate(txtRegisterDate.getText().toString());
            idNumber = txtIdNumberInput.getText().toString().trim();//txtIdNumber.getText().toString().trim();
            email = txtEmail.getText().toString().trim().trim();
            occupation = txtOccupation.getText().toString().trim();
            contact = txtContact.getText().toString().trim();
            address1 = txtAddress1.getText().toString().trim();
            address2 = txtAddress2.getText().toString().trim();
            patientRemark1 = txtRemark1.getText().toString().trim();
            patientRemark2 = txtRemark2.getText().toString().trim();
            description = txtDescription.getText().toString().trim();

            switch(this.mode) {
                case "add": {
                    PatientModel newData;
                    newData = new PatientModel(0, uuid, patient_id, "", firstName, surname, registerDate, genderId, dateofBirth,
                                                            age, month, idNumber, email, occupation, contact, address1, address2, patientRemark1, patientRemark2, patientTypeId, description);
                    newData.setMedical_record_ids(ListItemRecord);

                    String patientIHS = "";
                    if (!TextUtils.isEmpty(JApplication.patientIHS)){
                        patientIHS = JApplication.patientIHS;
                    }
                    newData.setPatient_ihs(patientIHS);//di kosongin nanti dibawah ada pengecekan lagi
                    if (!patientTable.insert(newData, true)){
                        return false;
                    }
                    break;
                }
                case "edit": {
                    PatientModel data = patientTable.getRecordByUuId(uuid);
                    String patientIHS = "";
                    if (TextUtils.isEmpty(JApplication.patientIHS)){
                        patientIHS = data.getPatient_ihs();
                    }else{
                        patientIHS = JApplication.patientIHS;
                    }
                    data.setPatient_ihs(patientIHS); //di kosongin nanti dibawah ada pengecekan lagi
                    data.setPatient_id(patient_id);
                    data.setFirst_name(firstName);
                    data.setSurname(surname);
                    data.setGender_id(genderId);
                    data.setAge(age);
                    data.setMonth(month);
                    data.setDate_of_birth(dateofBirth);
                    data.setRegister_date(registerDate);
                    data.setGender_id(genderId);
                    data.setIdentification_no(idNumber);
                    data.setEmail(email);
                    data.setOccupation(occupation);
                    data.setContact_no(contact);
                    data.setAddress_street_1(address1);
                    data.setAddress_street_2(address2);
                    data.setPatient_remark_1(patientRemark1);
                    data.setPatient_remark_2(patientRemark2);
                    data.setMedical_record_ids(ListItemRecord);
                    data.setPatient_type_id(patientTypeId);
                    data.setDescription(description);
                    if (!patientTable.update(data)){
                        return false;
                    }
                    break;
                }
            }
            JApplication.patientIHS = ""; //reset
            return true;
        }
        return false;
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                setEnabledComponent();
                setTitleInput();
                return true;
            case R.id.menu_view:
                this.mode = "detail";
                setEnabledComponent();
                setTitleInput();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
//                        globalTable.archive("pasienqu_patient", uuid, "pasienqu.patient");

                        //archive ngefek juga ke medical record dan billing terkait
                        if (ListItemRecord.size() != 0) {
                            for (int i = 0; i < ListItemRecord.size(); i++) {
                                String recordUuids = ListItemRecord.get(i).getUuid();
                                int recordIds = ListItemRecord.get(i).getId();
                                globalTable.archivePatient(uuid, patientId, recordUuids, recordIds);
                            }
                        }else{
                            globalTable.archivePatient(uuid, patientId, "", 0);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(PatientInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.patient)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
//                        globalTable.unarchive("pasienqu_patient", uuid, "pasienqu.patient");

                        //unarchive ngefek juga ke medical record dan billing terkait
                        if (ListItemRecord.size() != 0) {
                            for (int i = 0; i < ListItemRecord.size(); i++) {
                                String recordUuids = ListItemRecord.get(i).getUuid();
                                int recordIds = ListItemRecord.get(i).getId();
                                globalTable.unArchivePatient(uuid, patientId, recordUuids, recordIds);
                            }
                        }else{
                            globalTable.unArchivePatient(uuid, patientId, "", 0);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(PatientInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.patient)), runUnarchive);
                return true;

            case R.id.menu_add_record:
                Intent s = new Intent(PatientInputActivity.this, RecordInputActivity.class);
                s.putExtra("uuid", "");
                s.putExtra("patient_id", patientId);
                s.putExtra("patient_input", true);
                s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(s,1);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_patient_input, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
        boolean isArchive = globalTable.isArchived("pasienqu_patient", uuid);

        menu.getItem(0).setVisible(false); //item add record
        menu.getItem(1).setVisible(false); //item edit
        menu.getItem(2).setVisible(false); //item view
        menu.getItem(3).setVisible(false); //item archive
        menu.getItem(4).setVisible(false); //item unarchive
        if ((this.mode != null) && (this.mode.equals("detail"))) {
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(true);
            menu.getItem(3).setVisible(true);
        }
        if ((this.mode != null) && (this.mode.equals("edit"))){
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
        }

        if ((this.mode != null) && (!this.mode.equals("add"))) {
            menu.getItem(3).setVisible(!isArchive);
            menu.getItem(4).setVisible(isArchive);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    recordId = recordTable.getMaxId();
                    String recordDate = "";
                    String Anamnesa = "";
                    String PhysicalExam = "";
                    String Diagnosa = "";
                    String Therapy = "";

                    listRecord = recordTable.getRecords();
                    listStringRecord = new ArrayList<>();

                    for (int i = 0; i < listRecord.size(); i++) {
                        if (recordId == listRecord.get(i).getId()) {
                            recordDate = Global.getDateFormated(listRecord.get(i).getRecord_date());
                            Anamnesa = listRecord.get(i).getAnamnesa();
                            PhysicalExam = listRecord.get(i).getPhysical_exam();
                            Diagnosa = listRecord.get(i).getDiagnosa();
                            Therapy = listRecord.get(i).getTherapy();
                        }
                    }

//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                        R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
//                txtPatient.setAdapter(adapter);
//                txtPatient.setText(patientName);
//                    break;

                }
                isInputMedical = true;
                loadData();
                break;
            case 100:  //edit istory medical record
                recreate();
                break;
            case REQUEST_LOV_PATIENT_SATU_SEHAT:
                if (resultCode == RESULT_OK) {
                    Bundle extra = data.getExtras();
                    String patientIHSNumber = extra.getString("patient_ihs_number");
                    String name = extra.getString("name");
                    String birtDate = extra.getString("birth_date");
                    int gender = extra.getInt("gender");

                    if (!TextUtils.isEmpty(patientIHSNumber)) {
                        JApplication.patientIHS = patientIHSNumber;
                        txtIdNumber.setText(patientIHSNumber);
                        txtIdNumberInput.setText(patientIHSNumber);
                        imgSatuSehatInput.setBackgroundResource(R.drawable.ic_satu_sehat_colored);
                        imgSatuSehat.setBackgroundResource(R.drawable.ic_satu_sehat_colored);

                        Runnable runReplaceData = new Runnable() {
                            @Override
                            public void run() {
                                //name
                                txtSurname.setText("");
                                txtFirstName.setText(name);
                                //birthdate
                                txtDateBirth.setText(birtDate);
                                isTglSalingTerkait = false;
                                getAge();
                                isTglSalingTerkait = true;
                                //gender
                                if (gender > 0) {
                                    for (int i = 0; i < listGender.size(); i++) {
                                        if (listGender.get(i).getId() == gender) {
                                            genderId = listGender.get(i).getId();
                                            spinGender.setText(listStringGender.get(i), false);
                                        }
                                    }
                                }
                            }
                        };
                        ShowDialog.confirmDialog(this, getString(R.string.patient), "Data Pasien Satu Sehat telah didapatkan. Apakah data sudah sesuai dan akan diupdate ke data pasien?", runReplaceData);
                    }
                }
                break;
        }
    }

    private void syncUpData(){
        if(Global.CheckConnectionInternet(PatientInputActivity.this)){
            SyncUp.sync_all(PatientInputActivity.this, getApplicationContext(), ()->{});
        }
    }
}

package com.orion.pasienqu_2.activities.billing;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import static org.apache.log4j.NDC.clear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.billing_template.BillingTemplateInputActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.adapter.BillingInputAdapter;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncUp;
import com.orion.pasienqu_2.models.BillingItemModel;
import com.orion.pasienqu_2.models.BillingModel;
import com.orion.pasienqu_2.models.PatientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillingInputActivity extends CustomAppCompatActivity {
    private TextInputEditText txtBillingDate, txtNotes;
    private TextInputLayout errDate, errPatient;
    private AutoCompleteTextView txtPatient;
    private TextView txtBillingTotal;
    private Button btnSave, btnCancel;
    private ImageButton btnAddItem, btnNotes, btnItems, btnAddPatient;
    private String mode = "";
    private String uuid;
    private int patientId;
    private BillingTable billingTable;
    public List<BillingItemModel> ListItems = new ArrayList<>();
    private RecyclerView rcvLoad;
    private BillingInputAdapter mAdapter;
    private List<String> listStringPatient;
    private ArrayList<PatientModel> listPatient;
    private Menu menu;
    private PatientTable patientTable;
    private boolean isMedicalRecord;
    private GlobalTable globalTable;
    private int recordId;
    private long mLastClickTime = 0;
    private boolean isMedicalRecordEdit;
    private boolean isMedicalRecordDetail;
    private ProgressBar loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_input);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        txtBillingDate = (TextInputEditText) findViewById(R.id.txtBillingDate);
        errDate = (TextInputLayout) findViewById(R.id.layoutBillingDate);
        errDate = (TextInputLayout) findViewById(R.id.layoutPatient);
        txtPatient = (AutoCompleteTextView) findViewById(R.id.txtPatient);
        txtNotes = (TextInputEditText) findViewById(R.id.txtNotes);
        txtBillingTotal = (TextView) findViewById(R.id.txtBillingTotal);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAddPatient = (ImageButton) findViewById(R.id.btnAddPatient);
        btnAddItem = (ImageButton) findViewById(R.id.btnAddItem);
        btnNotes = (ImageButton) findViewById(R.id.btnNotes);
        btnItems = (ImageButton) findViewById(R.id.btnItems);

        billingTable = ((JApplication) getApplicationContext()).billingTable;
        patientTable = ((JApplication) getApplicationContext()).patientTable;
        globalTable = ((JApplication) getApplicationContext()).globalTable;
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        Runnable runnableOnAmountChange = new Runnable() {
            @Override
            public void run() {
                calcTotal();
            }
        };
        mAdapter = new BillingInputAdapter(BillingInputActivity.this, ListItems, R.layout.billing_input_list_item, runnableOnAmountChange);
        loadingText = findViewById(R.id.loadingText);
        loadingText.setVisibility(View.GONE);
    }

    private void InitClass(){
        patientId = 0;
        isMedicalRecord = false;
        isMedicalRecordEdit = false;

        Bundle extra = this.getIntent().getExtras();
        patientId = extra.getInt("patient_id");
        uuid = extra.getString("uuid");
        recordId = extra.getInt("record_id");
        isMedicalRecord = extra.getBoolean("medical_record");
        isMedicalRecordEdit = extra.getBoolean("medical_record_edit");
        isMedicalRecordDetail = extra.getBoolean("mode_detail");

        if (patientId != 0){
            PatientModel patientModel = patientTable.getRecordById(patientId);
            txtPatient.setText(patientModel.getPatientNameId());
        }

        if (uuid.equals("")){
            mode   = "add";
        }else{
            mode   = "edit";
            loadData();
        }

        if (mode.equals("add")){
            txtBillingDate.setText(Global.serverNow());
            txtBillingTotal.setText("0");
        }

        this.setTitleInput();

        rcvLoad.setLayoutManager(new GridLayoutManager(BillingInputActivity.this, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

        //isi patient
        /*PatientTable patientTable = new PatientTable(BillingInputActivity.this);
        listStringPatient = new ArrayList<>();
        listPatient = patientTable.getRecords();
        String patientName = "";
        for (int i = 0; i < listPatient.size(); i++){
            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(BillingInputActivity.this));
            if (patientId == listPatient.get(i).getId()) {
                patientName = listPatient.get(i).getPatientNameId();
            }
        }
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(BillingInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
        // Create a custom ArrayAdapter with custom filtering
        patientAdapter.getFilter().filter(getPatientFilter(patientAdapter).toString());
        txtPatient.setAdapter(patientAdapter);
        txtPatient.setText(patientName);*/

        Global.setEnabledClickText(txtBillingDate, false);
        if (isMedicalRecord){
            btnAddPatient.setVisibility(View.GONE);
            Global.setEnabledAutoCompleteText(txtPatient, false);
        }

        //jika mode detail
        if(isMedicalRecordDetail){
            mAdapter.isDetail = true;
            this.setTitle(String.format(getString(R.string.detail_title), getString(R.string.title_billing)));
            modeDetail();
        }

    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fungsi mencegah duplicate save karena btnSave.setEnabled(false) tidak berhasil
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if(saveForm()) {
                    setResult(RESULT_OK);
                    onBackPressed();
//                    if (!isMedicalRecord){
//                        syncUpData();
//                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtBillingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.datePickerClick(BillingInputActivity.this, txtBillingDate, view);
            }
        });

        btnNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog.showTemplate(BillingInputActivity.this, txtNotes, uuid);
            }
        });

        btnItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog.showBillingTemplate(BillingInputActivity.this, mAdapter, uuid);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItems();
            }
        });

        btnAddPatient.setOnClickListener(view -> {
            Intent s = new Intent(BillingInputActivity.this, PatientInputActivity.class);
            s.putExtra("uuid", "");
            s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(s,1);
        });


        txtPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);

                for (int i = 0; i < listPatient.toArray().length; i++) {
                    if (listPatient.get(i).getIDPatientNameGenderAge(BillingInputActivity.this).equals(selection)) {
                        patientId = listPatient.get(i).getId();
                        txtPatient.setText(listPatient.get(i).getPatientNameId());
                        break;
                    }
                }
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
    }

    private void AddItems() {
        BillingItemModel data = new BillingItemModel();
        mAdapter.addModel(data);
        mAdapter.isReqFocus = true;
//        mAdapter.notifyDataSetChanged();
    }


    private void loadData(){
        BillingModel data = billingTable.getRecordByUuid(uuid);

        txtBillingDate.setText(Global.getDateFormated(data.getBilling_date()));
        patientId = data.getPatient_id();
//        txtPatient.setText(data.getName());
        PatientModel patientModel = patientTable.getRecordById(patientId);
        txtPatient.setText(patientModel.getPatientNameId());

        txtNotes.setText(data.getNotes());
        txtBillingTotal.setText(Global.FloatToStrFmt(data.getTotal_amount()));
        recordId = data.getMedical_record_id();
//        ListItems = data.getBilling_item_ids();
        mAdapter.addModels(data.getBilling_item_ids());
        mAdapter.notifyDataSetChanged();
    }

    private boolean isValid() {
        if (patientId==0) {
            errDate.setError(getString(R.string.error_patient_id));
        } else {
            errDate.setErrorEnabled(false);
        }

        if (patientId !=0 && ListItems.size() == 0){  //pengecekan jika ga nginput item
            Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_no_item), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean saveForm() {
        if (this.isValid()) {

            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }

            long billing_date = Global.getMillisDate(txtBillingDate.getText().toString());
            String notes = txtNotes.getText().toString().trim();

            //pengecekan item kosoooooongggg
            String items = "";
            double amont = 0;
            for (int i = 0; i < ListItems.size(); i++){
                items = ListItems.get(i).getName();
                amont = ListItems.get(i).getAmount();
                if (items.equals("") && amont == 0){
                    mAdapter.removeModel(i);
                    i--;
                }
            }


            double total_amount = Global.StrFmtToFloat(txtBillingTotal.getText().toString());

            String name = txtPatient.getText().toString().trim();

            switch(this.mode.toString()) {
                case "add": {
                    BillingModel newData;
                    newData = new BillingModel(0, uuid, billing_date, patientId, notes, total_amount, name);
                    newData.setBilling_item_ids(ListItems);
                    if (isMedicalRecord){
                        newData.setMedical_record_id(recordId);
                        if (!billingTable.insert(newData)) {
                            return false;
                        }
                    }else {
                        if (!billingTable.insert(newData, true)) {
                            return false;
                        }
                    }

                    break;
                }

                case "edit": {
                    BillingModel Data = billingTable.getRecordByUuid(uuid);
                    Data.setBilling_date(billing_date);
                    Data.setPatient_id(patientId);
                    Data.setNotes(notes);
                    Data.setTotal_amount(total_amount);
                    Data.setName(name);
                    Data.setBilling_item_ids(ListItems);
                    Data.setMedical_record_id(recordId);

                    if (isMedicalRecord){
                        billingTable.setTemp(true);
                    }
                    if (!billingTable.update(Data, true)){
                        return false;
                    }
                    if (!isMedicalRecord && !isMedicalRecordEdit){ //jika ubah billing dari menu billing maka ubah totalan di medical record terkait
                        int newTotalBilling = billingTable.getSumByMedicalRecord(recordId);
                        globalTable.updateTotalBillingMedicalRecord(recordId, newTotalBilling);
                    }
                    break;
                }
            }

            if (isMedicalRecord){
                globalTable.saveTemp("pasienqu_billing", uuid);
            }
            return true;
        }
        return false;
    }

    private void setTitleInput() {
        if(this.mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.title_billing)));
        }else if(this.mode.equals("edit")){
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.title_billing)));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive_unarchive, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_billing", uuid, "pasienqu.billing");
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(BillingInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.title_billing)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_billing", uuid, "pasienqu.billing");
                        setResult(RESULT_OK);
                        finish();
                    }
                };
                ShowDialog.confirmDialog(BillingInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.title_billing)), runUnarchive);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        boolean isArchive = globalTable.isArchived("pasienqu_billing", uuid);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        if (mode.equals("edit")) {
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        if (isMedicalRecordDetail){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    private void calcTotal(){
        double total = 0;
        for (int i = 0; i < ListItems.size(); i++){
            total = total + ListItems.get(i).getAmount();
        }
        txtBillingTotal.setText(Global.FloatToStrFmt(total));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    patientId = patientTable.getMaxId();
//                    String patientName = "";
//                    listStringPatient = new ArrayList<>();
//                    listPatient = patientTable.getRecords();
//                    for (int i = 0; i < listPatient.size(); i++) {
//                        listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(BillingInputActivity.this));
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
                }
                break;
        }
    }

    private void syncUpData(){
        if(Global.CheckConnectionInternet(BillingInputActivity.this)){
            SyncUp.sync_all(BillingInputActivity.this, getApplicationContext(), ()->{});
        }
    }

    private void modeDetail(){
        Global.setEnabledTextInputEditText(txtBillingDate, false);
        Global.setEnabledTextInputEditText(txtNotes, false);
        Global.setEnabledAutoCompleteTextView(txtPatient, false);
        btnAddItem.setVisibility(View.GONE);
        btnNotes.setVisibility(View.GONE);
        btnAddPatient.setVisibility(View.GONE);
        btnItems.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
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
                            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(BillingInputActivity.this));
                        }
                        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(BillingInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
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
}

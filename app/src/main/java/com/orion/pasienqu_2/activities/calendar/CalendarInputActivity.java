package com.orion.pasienqu_2.activities.calendar;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import static org.apache.log4j.NDC.clear;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.Settings;
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
import com.orion.pasienqu_2.activities.billing.BillingInputActivity;
import com.orion.pasienqu_2.activities.lov.LovCheckActivity;
import com.orion.pasienqu_2.activities.patient.PatientInputActivity;
import com.orion.pasienqu_2.activities.record.RecordInputActivity;
import com.orion.pasienqu_2.data_table.AppointmentTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.globals.AutoStartHelper;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.AppointmentModel;
import com.orion.pasienqu_2.models.LovCheckModel;
import com.orion.pasienqu_2.models.PatientModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CalendarInputActivity extends CustomAppCompatActivity {

    private TextInputEditText txtAppointmentDate, txtReminder, txtNotes;
    private TextInputLayout errWorkLocation, errPatient;
    private AutoCompleteTextView spinWorkLocation, txtPatient;
    private Button btnSave, btnCancel;
    private ImageButton btnAddPatient, btnReminder;
    private AppointmentTable appointmentTable;
    private PatientTable patientTable;
    private List<String> listStringWorkLocation;
    private List<String> listIdWorkLocation;
    private List<String> listStringPatient;
    private List<String> listIdPatient;
    private ArrayList<LovCheckModel> listReminder;
    private ArrayList<PatientModel> listPatient;
    private int id;
    private String uuid;
    private String mode = "";
    private String valueReminder = "";
    private int idPatient;
    private int idWorkLocation;
    private long mLastClickTime = 0;
    private String mCustomValueReminder = "";
    private TextView txtStatus;
    private Button btnSetting;
    private NotificationUtil notificationUtil = new NotificationUtil(this);
    private Notification notification = new Notification();
    private ProgressBar loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_input);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        txtAppointmentDate = (TextInputEditText) findViewById(R.id.txtAppointmentDate);
        spinWorkLocation = (AutoCompleteTextView) findViewById(R.id.spinWorkLocation);
        txtPatient = (AutoCompleteTextView) findViewById(R.id.txtPatient);
        errPatient = (TextInputLayout) findViewById(R.id.layoutPatient);
        errWorkLocation = (TextInputLayout) findViewById(R.id.layoutWorkLocation);
        txtReminder = (TextInputEditText) findViewById(R.id.txtReminder);
        txtNotes = (TextInputEditText) findViewById(R.id.txtNotes);
        btnReminder = (ImageButton) findViewById(R.id.btnReminder);
        btnAddPatient = (ImageButton) findViewById(R.id.btnAddPatient);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSetting = (Button) findViewById(R.id.btnSetting);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        loadingText = findViewById(R.id.loadingText);
        loadingText.setVisibility(View.GONE);

        patientTable = ((JApplication) getApplicationContext()).patientTable;
    }

    private void InitClass() {
        Bundle extra = this.getIntent().getExtras();
        uuid = extra.getString("uuid");
        String appointmentDate = extra.getString("appointment_date");
        txtAppointmentDate.setText(appointmentDate);
        Global.setEnabledClickAutoCompleteText(spinWorkLocation, false);

        listStringWorkLocation = ListValue.list_work_location(getApplicationContext());
        listIdWorkLocation = ListValue.list_id_work_location(getApplicationContext());
        ArrayAdapter<String> workLocationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listStringWorkLocation);
        workLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinWorkLocation.setAdapter(workLocationAdapter);
        idWorkLocation = 0;

        listReminder = ListValue.list_reminder(CalendarInputActivity.this);
        appointmentTable = ((JApplication) getApplicationContext()).appointmentTable;

//        listStringPatient = new ArrayList<>();
//        listPatient = patientTable.getRecords();
//        if (listPatient.isEmpty()) {
//            listStringPatient.add(getString(R.string.data_not_found));
//        }else {
//            for (int i = 0; i < listPatient.size(); i++) {
//                listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge());
//            }
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
//        txtPatient.setAdapter(adapter);


        if (uuid.equals("")) {
            mode = "add";
        } else {
            mode = "edit";
            loadData();
        }

        //isi patient
        /*patientTable = new PatientTable(CalendarInputActivity.this);
        listStringPatient = new ArrayList<>();
        listPatient = patientTable.getRecords();
        String patientName = "";
        for (int i = 0; i < listPatient.size(); i++){
            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(CalendarInputActivity.this));
            if (idPatient == listPatient.get(i).getId()) {
                patientName = listPatient.get(i).getPatientNameId();
            }
        }
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(CalendarInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
        // Create a custom ArrayAdapter with custom filtering
        patientAdapter.getFilter().filter(getPatientFilter(patientAdapter).toString());
        txtPatient.setAdapter(patientAdapter);
        txtPatient.setText(patientName);*/

        setEnabledComponent();
        setTitleInput();
    }

    private void setTitleInput() {
        if (mode.equals("add")) {
            this.setTitle(String.format(getString(R.string.add_title), getString(R.string.appointment)));
        } else if (mode.equals("edit")) {
            this.setTitle(String.format(getString(R.string.edit_title), getString(R.string.appointment)));
        } else if (mode.equals("detail")) {
            this.setTitle(String.format(getString(R.string.detail_title), getString(R.string.appointment)));
        }
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

                if (saveForm()) {
//                    ReminderNotification();
                    setResult(RESULT_OK);
                    onBackPressed();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Global.hideSoftKeyboard(CalendarInputActivity.this);
            }
        });

        spinWorkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinWorkLocation.showDropDown();
            }
        });

        spinWorkLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                idWorkLocation = Integer.parseInt(listIdWorkLocation.get(position));
            }
        });


        btnAddPatient.setOnClickListener(view -> {
            Intent s = new Intent(CalendarInputActivity.this, PatientInputActivity.class);
            s.putExtra("uuid", "");
            s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(s, 1);
        });
        btnReminder.setOnClickListener(view -> {
            Intent s = new Intent(CalendarInputActivity.this, LovCheckActivity.class);

            //penyesuaian jika ada custom reminder
            for (int i = 0; i < listReminder.size(); i++) {
                if (i == listReminder.size() - 1) {
                    if (!mCustomValueReminder.equals("")) {
                        listReminder.get(i).setLabel(getString(R.string.ReminderCustom) + " " + mCustomValueReminder);
                        listReminder.get(i).setValue(mCustomValueReminder);
                    }
                }
            }

            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST", (Serializable) listReminder);
            s.putExtra("BUNDLE", args);
            s.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(s, 2);
        });

        txtPatient.setOnItemClickListener((adapterView, view, position, l) -> {
            String selection = (String) adapterView.getItemAtPosition(position);

            for (int i = 0; i < listPatient.toArray().length; i++) {
                if (listPatient.get(i).getIDPatientNameGenderAge(CalendarInputActivity.this).equals(selection)) {
                    idPatient = listPatient.get(i).getId();
                    txtPatient.setText(listPatient.get(i).getPatientNameId());
                    break;
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

        txtAppointmentDate.setOnClickListener(view -> {
            Global.dtpTimeClickDisablePastDate(CalendarInputActivity.this, txtAppointmentDate, view);
        });
        //langsung munculin date piceker saat awal buka aktifiti
        if (mode.equals("add")) {
            txtAppointmentDate.callOnClick();
        }

        btnSetting.setOnClickListener(view -> {
            checkBateryOptimisation();
        });
    }

    private void setEnabledComponent() {
        //profile layout
        Global.setEnabledClickText(txtAppointmentDate, false);
        Global.setEnabledTextInputEditText(txtReminder, false);
        Global.setEnabledAutoCompleteTextView(txtPatient, !mode.equals("detail"));
        Global.setEnabledTextInputEditText(txtNotes, !mode.equals("detail"));
        if (mode.equals("detail")) {
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnAddPatient.setVisibility(View.GONE);
            btnReminder.setVisibility(View.GONE);
        } else {
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        }
        spinWorkLocation.setClickable(true);
        spinWorkLocation.setFocusable(true);
        invalidateOptionsMenu();
    }

    private void loadData() {
        AppointmentModel appointmentModel = appointmentTable.getRecordByUuId(uuid);

        id = appointmentModel.getId();
        uuid = appointmentModel.getUuid();
        idWorkLocation = appointmentModel.getWork_location_id();
        idPatient = appointmentModel.getPatient_id();
//        txtPatient.setText(appointmentModel.getPatient_text());
        PatientModel patientModel = patientTable.getRecordById(idPatient);
        txtPatient.setText(patientModel.getPatientNameId());

        valueReminder = appointmentModel.getReminder();
        txtAppointmentDate.setText(Global.getDateTimeFormated(appointmentModel.getAppointment_date()));
        txtNotes.setText(appointmentModel.getNotes());
        for (int i = 0; i < listIdWorkLocation.size(); i++) {
            if (listIdWorkLocation.get(i).equals(String.valueOf(appointmentModel.getWork_location_id()))) {
                spinWorkLocation.setText(listStringWorkLocation.get(i), false);
            }
        }

        String[] reminder = valueReminder.split("\r\n|\r|\n");
        String textReminder = "";
        for (int i = 0; i < reminder.length; i++) {
            String reminderByIdx = reminder[i];
            for (int j = 0; j < listReminder.size(); j++) {
                if (reminderByIdx.equals(listReminder.get(j).getValue())) {
                    listReminder.get(j).setState(true);
                    if (i > 0) {
                        textReminder += "\n";
                    }
                    textReminder += listReminder.get(j).getLabel();
                    break;

                    //penyesuaian jika ada custom reminder
                } else if (reminderByIdx.length() > 4) {
                    listReminder.get(listReminder.size() - 1).setState(true);
                    if (i > 0) {
                        textReminder += "\n";
                    }
                    mCustomValueReminder = Global.getDateTimeFormated(Long.parseLong(reminderByIdx));
                    textReminder += listReminder.get(listReminder.size() - 1).getLabel() + " " + mCustomValueReminder;
                    break;
                }
            }
        }
        txtReminder.setText(textReminder);
    }

    private boolean isValid() {
        if (idWorkLocation == 0) {
            errWorkLocation.setError(getString(R.string.error_work_location));
        } else {
            errWorkLocation.setErrorEnabled(false);
        }

        if (idPatient == 0) {
            errPatient.setError(getString(R.string.error_patient_required));
        } else {
            errPatient.setErrorEnabled(false);
        }

        return true;
    }


    private boolean saveForm() {
        if (this.isValid()) {
            if (uuid.equals("")) {
                uuid = UUID.randomUUID().toString();
            }
            long appointmentDate = Global.getMillisDateTime(txtAppointmentDate.getText().toString());
            String notes = txtNotes.getText().toString().trim();
            String patientName = txtPatient.getText().toString().trim();

            //cek custom reminder apakah lebih dari tgl pertemuan
            if (!mCustomValueReminder.equals("")) {
                long customReminderDate = Global.getMillisDateTime(mCustomValueReminder);
                if (customReminderDate > appointmentDate) {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_custom_reminder_later), Snackbar.LENGTH_LONG).show();
                    return false;
                } else if (customReminderDate < Global.serverNowLong()) {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_remider_date_less), Snackbar.LENGTH_LONG).show();
                    return false;
                }
            }

            //munculin permision bateri optimisasi, karena ada kasus ga bisa munculin notif gara2 ini
            /**
            if (!txtReminder.equals("") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent intent = new Intent();
                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                    return false;
                }
            }
            **/


            switch (this.mode.toString()) {
                case "add": {
                    AppointmentModel newData = new AppointmentModel(id, uuid, appointmentDate, idWorkLocation, idPatient, valueReminder, notes);
                    if (!appointmentTable.insert(newData, true)) {
                        return false;
                    }
                    break;
                }
                case "edit": {
                    AppointmentModel data = appointmentTable.getRecordByUuId(uuid);
                    data.setAppointment_date(appointmentDate);
                    data.setPatient_id(idPatient);
                    data.setWork_location_id(idWorkLocation);
                    data.setReminder(valueReminder);
                    data.setNotes(notes);

                    if (!appointmentTable.update(data)) {
                        return false;
                    }
                    ;
                    break;
                }
            }


            //set notif sesuai reminder
            if (!valueReminder.isEmpty()) {
                String[] reminderString = valueReminder.split("\n");
                for (int i = 0; i < reminderString.length; i++) {
                    String reminder = reminderString[i];
                    if (reminder.length() > 4) {     //jika menit reminder lebih dari 4 digit berarti custom reminder yg masih format millies date
                        reminder = Global.getMinutes(Long.parseLong(reminder));
                    }

//                    scheduleNotification(getNotification(patientName), appointmentDate, Integer.parseInt(reminder));

                    //set isi dari notifikasi
                    notification = NotificationUtil.getNotification(patientName, spinWorkLocation.getText().toString(),
                                    txtAppointmentDate.getText().toString());
                    //masukan reminder ke jadwal notifikasi
                    NotificationUtil.scheduleNotification(notification, appointmentDate, Integer.parseInt(reminder), mCustomValueReminder);
                    //save to db local,
                    //nantinya berguna untuk menset ulang reminder ketika hp restart/mati
                    NotificationUtil.saveToReminderTable(appointmentDate, Integer.parseInt(reminder), mCustomValueReminder, patientName, spinWorkLocation.getText().toString(), txtAppointmentDate.getText().toString());
                }
            }

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
        final GlobalTable globalTable;
        globalTable = ((JApplication) getApplicationContext()).globalTable;

        switch (item.getItemId()) {
            case R.id.menu_archive:
                Runnable runArchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.archive("pasienqu_appointment", uuid, "pasienqu.appointment");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(CalendarInputActivity.this, getString(R.string.archive),
                        String.format(getString(R.string.confirm_archive), getString(R.string.appointment)), runArchive);
                return true;
            case R.id.menu_unarchive:
                Runnable runUnarchive = new Runnable() {
                    @Override
                    public void run() {
                        globalTable.unarchive("pasienqu_appointment", uuid, "pasienqu.appointment");
                        finish();
                    }
                };
                ShowDialog.confirmDialog(CalendarInputActivity.this, getString(R.string.unarchive),
                        String.format(getString(R.string.confirm_unarchive), getString(R.string.appointment)), runUnarchive);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive_unarchive, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);

        GlobalTable globalTable = ((JApplication) getApplicationContext()).globalTable;
        boolean isArchive = globalTable.isArchived("pasienqu_appointment", uuid);
        if (mode.equals("edit")) {
            menu.getItem(0).setVisible(!isArchive);
            menu.getItem(1).setVisible(isArchive);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    idPatient = patientTable.getMaxId();
//                    String patientName = "";
//                    listStringPatient = new ArrayList<>();
//                    listPatient = patientTable.getRecords();
//                    for (int i = 0; i < listPatient.size(); i++) {
//                        listStringPatient.add(listPatient.get(i).getPatientNameId());
//                        if (idPatient == listPatient.get(i).getId()) {
//                            patientName = listPatient.get(i).getPatientNameId();
//                        }
//                    }
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                            R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
//                    txtPatient.setAdapter(adapter);
//                    txtPatient.setText(patientName);

                    PatientModel patientModel = patientTable.getRecordById(idPatient);
                    txtPatient.setText(patientModel.getPatientNameId());
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle args = data.getBundleExtra("BUNDLE");
                    listReminder = (ArrayList<LovCheckModel>) args.getSerializable("ARRAYLIST");
                    String reminder = "";
                    valueReminder = "";
                    mCustomValueReminder = "";
                    for (int i = 0; i < listReminder.size(); i++) {
                        if (listReminder.get(i).isState()) {
                            if (!reminder.equals("")) {
                                reminder += "\n";
                                valueReminder += "\n";
                            }
                            reminder += listReminder.get(i).getLabel();
                            valueReminder += listReminder.get(i).getValue();

                            //isi juga variabelnya jika custom reminder
                            if (i == listReminder.size() - 1) {
                                mCustomValueReminder = Global.getDateTimeFormated(Long.parseLong(listReminder.get(i).getValue()));
                            }
                        }
                    }
                    txtReminder.setText(reminder);
                }
                break;
            case 123:
                AutoStartHelper.getInstance().getAutoStartPermission(this);
                break;
        }
    }

    private void checkBateryOptimisation() {
        //munculin permision bateri optimisasi, karena ada kasus ga bisa munculin notif gara2 ini

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivityForResult(intent, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtStatus.setText(R.string.status_battery_allow);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            txtStatus.setText(R.string.status_battery_notallow);
        } else {
            txtStatus.setText(R.string.status_battery_allow);
        }
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
                            listStringPatient.add(listPatient.get(i).getIDPatientNameGenderAge(CalendarInputActivity.this));
                        }
                        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(CalendarInputActivity.this, R.layout.custom_list_view, R.id.text_view_list_item, listStringPatient);
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
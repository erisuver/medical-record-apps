package com.orion.pasienqu_2.activities.home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.calendar.CalendarFragment;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.activities.more.MoreFragment;
import com.orion.pasienqu_2.activities.more.google_drive.DriveUtil;
import com.orion.pasienqu_2.activities.more.pin_protection.PinProtectScreenActivity;
import com.orion.pasienqu_2.activities.more.renew_subscriotion.RenewSubscriptionActivity;
import com.orion.pasienqu_2.activities.patient.PatientListFragment;
import com.orion.pasienqu_2.activities.record.RecordFragment;
import com.orion.pasienqu_2.data_table.BackupDriveTable;
import com.orion.pasienqu_2.data_table.ICDTable;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ListValue;
import com.orion.pasienqu_2.globals.PurchaseBilling;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.SyncOdoo;
import com.orion.pasienqu_2.models.ICDModel;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.RadioCheckModel;
import com.orion.pasienqu_2.utility.GetIPAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class home extends CustomAppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    HomeFragment frgHome;
    Fragment frgPatient;
    Fragment frgRecord;
    Fragment frgCalender;
    Fragment frgMore;
    BottomNavigationView navigation;

    Handler handler = new Handler();
    int delay = (24) * (60) * (60) * 1000;
    int delayBackground = (24) * (60) * (60) * 1000;
    private BottomNavigationItemView itemViewCart, itemViewOrderList;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return loadFragment(frgHome);
                case R.id.navigation_patients: {
                    return loadFragment(frgPatient);
                }
                case R.id.navigation_records: {
                    return loadFragment(frgRecord);
                }
                case R.id.navigation_calender: {
                    return loadFragment(frgCalender);
                }
                case R.id.navigation_more: {
                    return loadFragment(frgMore);
                }
            }
            return loadFragment(null);
        }
    };
    private PatientListFragment patienFragment;
    private RecordFragment recordFragment;
    private CalendarFragment calendarFragment;
    private MoreFragment moreFragment;
    private int idNavigation; //id untuk handle rotation
    private final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private boolean isLoggedOut = false;
    private final int CODE_LOAD_HOME = 111, CODE_LOAD_PATIENT = 222, CODE_LOAD_MEDICAL = 333, CODE_LOAD_MORE = 444, CODE_LOAD_CALENDER = 555;
    private final SharedPreferences sharedPrefSubscription = JApplication.getInstance().sharedPrefSubscription;
    private final SharedPreferences.Editor sharedPrefSubsEdit = JApplication.getInstance().sharedPrefSubscriptionEdit;

    private static final int APP_UPDATE_REQUEST_CODE = 123;

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private String versips = "";
    private int versiPlaystore;
    private ProgressDialog progressDialog;
    private boolean isSedangImportICD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAppDirectory();
        JApplication.currentActivity = this; //set awal currenactivity

        // Inisialisasi AppUpdateManager. xxx
        appUpdateManager = AppUpdateManagerFactory.create(this);
        // Periksa pembaruan di Play Store. xxx
        checkForAppUpdate();

//        get_real_url();
        if (Global.CheckConnectionInternet(home.this)) {
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    JApplication.getInstance().isGetUrl = true;
                }
            };
            GetIPAddress.get_ip_address(home.this, runSuccess);
        }

        setLanguage();
        setContentView(R.layout.activity_home);
        InitClass();
        GetPermission();
        if (savedInstanceState != null) {
            idNavigation = savedInstanceState.getInt("navigationId");
            handleRotatateNavigation();
        }

        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            //fungsi notifikasi on click > munculin dialog
            String notificationId = extra.getString("notif_id");
            if (notificationId != null && !notificationId.equals("")) {
                String patientName = extra.getString("patient_name");
                String locationwork = extra.getString("work_location");
                String dateAppointment = extra.getString("appointment_date");
                String message = getString(R.string.appointment_with) + patientName + getString(R.string.at) + locationwork + getString(R.string.on) + dateAppointment;

                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
                dialog.setMessage(message);
                dialog.setCancelable(true);
                dialog.show();
            }

            //handle show dialog restore
            boolean isPurchaseAfterExpire = extra.getBoolean("purchase_expired");
            if (isPurchaseAfterExpire) {
                showDialogRestore();
            }
        }


        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);

        //login
        if (!sharedPreferences.contains("username")) {
            isLoggedOut = true;
            Intent intent = new Intent(home.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            if (sharedPreferences.getBoolean("usePin", false)) {
                Intent intent = new Intent(this, PinProtectScreenActivity.class);
                intent.putExtra("first_open_app", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        //hilangkan fungsi sync
//        startActivity( new Intent(home.this, SyncronizeDataActivity.class) );

        //simpen notif
//        NotificationReceiver notificationReceiver = new NotificationReceiver();
//        NotificationUtil notificationUtil = new NotificationUtil(getApplicationContext());
//        notificationUtil.runNotification();
//        registerReceiver(notificationReceiver, new IntentFilter("notification-id"));

        if (!JApplication.getInstance().loginInformationModel.getUsername().isEmpty() && !SharedPrefsUtils.getBooleanPreference(home.this, "is_mysql", false)) {
            ShowMigrasiMySQL();
        }

    }


    private void InitClass() {
        frgHome = new HomeFragment();
        frgPatient = new PatientListFragment();
        frgRecord = new RecordFragment();
        frgCalender = new CalendarFragment();
        frgMore = new MoreFragment();
        loadFragment(frgHome);
        navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
        return currentFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForAppUpdate();
        // Buat InstallStateUpdatedListener untuk memantau proses update. xxx
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // Update telah berhasil diunduh, siap untuk diinstal.
                popupSnackbarForCompleteUpdate();
            }
        };
        // Daftarkan InstallStateUpdatedListener. xxx
        appUpdateManager.registerListener(installStateUpdatedListener);

        if (((JApplication) getApplicationContext()).isLogOn) {
            loadFragment(frgHome);
            navigation.setSelectedItemId(R.id.navigation_home);
            showIsExpired();
            ((JApplication) getApplicationContext()).isLogOn = false;
            //hilangkan fungsi sync
//            startActivity( new Intent(home.this, SyncronizeDataActivity.class) );
            Runnable runRestore = new Runnable() {
                @Override
                public void run() {
                    showDialogRestore();
                }
            };
            Runnable runDismiss = new Runnable() {
                @Override
                public void run() {
                    //importICDBaru();//importICD();
                }
            };

            //pengecekan utk munculin dialog restore database
            if (!SharedPrefsUtils.getBooleanPreference(home.this, "force_logout", false) && !JApplication.getInstance().isAfterSignup) {
                ShowDialog.confirmDialogWithDissmis(home.this, getString(R.string.title_restore_drive),
                        getString(R.string.content_restore_from_drive), runRestore, runDismiss);
            } else if (!SharedPrefsUtils.getStringPreference(home.this, "last_user").equals(JApplication.getInstance().loginInformationModel.getUsername()) && !JApplication.getInstance().isAfterSignup) {
                Global.clearDatabase();  //clear db
                ShowDialog.confirmDialogWithDissmis(home.this, getString(R.string.title_restore_drive),
                        getString(R.string.content_restore_from_drive), runRestore, runDismiss);
            }
            SharedPrefsUtils.setBooleanPreference(home.this, "force_logout", false);
            SharedPrefsUtils.setStringPreference(home.this, "last_user", JApplication.getInstance().loginInformationModel.getUsername());
        }

        if (getCurrentFragment().equals(frgHome) && !((JApplication) getApplicationContext()).isLogOn) {
            frgHome.loadData();
            ((JApplication) getApplicationContext()).isLogOn = false;
            ((JApplication) getApplicationContext()).isAfterSignup = false;
        }

        if (!isLoggedOut && !((JApplication) getApplicationContext()).isLogOn) {
            importICDBaru();//importICD();
            showWarningExpired();
            showIsExpired();
            isLoggedOut = false;
        }
        //hilangkan fungsi sync
//        handler.removeCallbacks(runTask);
//        handler = new Handler();
//        handler.postDelayed(runTask, delay);
        navigation.setFocusable(true);
        setLanguage();

        //untuk mengecek jika ada purchase pending
        boolean isPendingPurchase30 = sharedPrefSubscription.getString("status_payment_pending_30", "").equals("");
        boolean isPendingPurchase365 = sharedPrefSubscription.getString("status_payment_pending_365", "").equals("");
        if ((!isPendingPurchase30 || !isPendingPurchase365) && Global.CheckConnectionInternet(home.this)) {
            new PurchaseBilling(home.this);
        }
        if (JApplication.getInstance().isGetUrl && !JApplication.getInstance().loginInformationModel.getUsername().isEmpty() && Global.CheckConnectionInternet(home.this)) {
            checkStatusSubscription();
            cekKirimUlang();
        }
        if (!JApplication.getInstance().isSedangCekVersi && JApplication.getInstance().isGetUrl && Global.CheckConnectionInternet(home.this)) {
            check_versi(home.this);
        }

    }


    private Runnable runTask = new Runnable() {
        public void run() {
            SyncOdoo.syncUpOdoo(getApplicationContext());
            SyncOdoo.syncDownOdoo(getApplicationContext(), home.this);
            boolean isInBackground;
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            if (isInBackground) {
                handler.postDelayed(this, delayBackground);
            } else {
                handler.postDelayed(this, delay);
            }
        }
    };

    private void showDialogBackup() {
        Runnable runBackup = new Runnable() {
            @Override
            public void run() {
                DriveUtil.upload(home.this);
            }
        };

        Runnable runDismis = new Runnable() {
            @Override
            public void run() {
                showDialogExit();
            }
        };
        ShowDialog.backupRestoreDialog(home.this, runBackup, runDismis, false);
    }

    private void showDialogExit() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                finishAffinity();
            }
        };
        ShowDialog.confirmDialog(home.this, getString(R.string.quit_app_title), getString(R.string.quit_app_content), runnable);
    }

    private void showDialogRestore() {
        Runnable runRestore = new Runnable() {
            @Override
            public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        frgHome.loadData();
                        //importICDBaru();//importICD();
                        //update reminder jika ada
//                        NotificationUtil.runNotification();
                    }
                };
                DriveUtil.download(home.this, runnable);
            }
        };
        Runnable runDismis = new Runnable() {
            @Override
            public void run() {
                //importICDBaru();//importICD();
            }
        };
        ShowDialog.backupRestoreDialog(home.this, runRestore, runDismis, true);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            if (showReminderBackup()) {
                showDialogBackup();
            } else {
                showDialogExit();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.inform_back_to_exit, Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_LOAD_HOME:
                frgHome.loadData();
                break;
            case CODE_LOAD_PATIENT:
                if (resultCode == RESULT_OK) {
                    PatientListFragment patienFragment = (PatientListFragment) getCurrentFragment();
//                patienFragment.LoadData();
                    patienFragment.loadRefresh();  //load with limit
                    break;
                }
            case CODE_LOAD_MEDICAL:
                if (resultCode == RESULT_OK) {
                    RecordFragment recordFragment = (RecordFragment) getCurrentFragment();
//                recordFragment.LoadData();
                    recordFragment.loadRefresh();   //load with limit
                    break;
                }
            case CODE_LOAD_MORE:
                if (resultCode == RESULT_OK) {
                    MoreFragment moreFragment = (MoreFragment) getCurrentFragment();
                    moreFragment.loadData();
                    moreFragment.EventClass(); //jika edit profile, apply style app bar
                    break;
                }
            case CODE_LOAD_CALENDER:
                if (resultCode == RESULT_OK) {
                    CalendarFragment calenderFragment = (CalendarFragment) getCurrentFragment();
                    calenderFragment.LoadData();
                    break;
                }

        }
    }

    private void setLanguage() {
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if (!language.equals("")) {
            if (language.equals("in")) {
                Locale locale = new Locale("in");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            } else {
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        } else {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = getResources().getConfiguration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private void importICD() {
        ICDTable icdTable = new ICDTable(getApplicationContext());
        if (icdTable.getTotalDatas() < totalDataIcd()) {

            icdTable.deleteAll();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            dialog.setMax(totalDataIcd());
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.import_icd_msg));
            dialog.show();

            Thread mThread = new Thread() {
                @Override
                public void run() {

                    InputStream inputStream;
                    inputStream = getResources().openRawResource(R.raw.icd10);
                    String[] ids;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        String csvLine;
                        int i = 0;
                        SQLiteDatabase db = JApplication.getInstance().dbConn.getWritableDatabase();
                        DatabaseUtils.InsertHelper iHelp = new DatabaseUtils.InsertHelper(db, "pasienqu_icd10");
                        db.beginTransaction();
                        while ((csvLine = reader.readLine()) != null) {
                            i++;
                            dialog.setProgress(i);
                            ids = csvLine.split(",");
                            try {
                                String nama = csvLine;
                                String a = ids[0] + "," + ids[1] + ",";
                                nama = nama.replace(a, "");

                                ICDModel icdModel = new ICDModel();
                                icdModel.setId(Integer.parseInt(ids[0]));
                                icdModel.setCode(ids[1]);
                                icdModel.setName(nama);

                                //optimasi insert biar cepet ea
                                int id_index = iHelp.getColumnIndex("id");
                                int code_index = iHelp.getColumnIndex("code");
                                int name_index = iHelp.getColumnIndex("name");

                                iHelp.prepareForInsert();
                                iHelp.bind(id_index, icdModel.getId());
                                iHelp.bind(code_index, icdModel.getCode());
                                iHelp.bind(name_index, icdModel.getName());
                                iHelp.execute();
//                                icdTable.insert(icdModel, false); //save
                            } catch (Exception e) {
                                Log.e("empty", e.toString());
                            }
                        }
                        //close trans
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    } catch (IOException ex) {
                        throw new RuntimeException("Error in reading CSV file: " + ex);

                    }
                    dialog.dismiss();
                }
            };
            mThread.start();
        }
    }

    private int totalDataIcd() {
        InputStream inputStream2 = getResources().openRawResource(R.raw.icd10);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
        int total = 0;
        try {
            String csvLine2;
            while ((csvLine2 = reader2.readLine()) != null) {
                total++;
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
        return total;
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
//        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
//        }

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
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
//                    manageExternalStoragePermission();  //access all file
                } else {
                    // Permission Denied
//                    if(JApplication.getInstance().isFirstTimeRequest) {
//                        JApplication.getInstance().isFirstTimeRequest = false;
//                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
//                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleRotatateNavigation() {
        if (idNavigation == R.id.navigation_patients) {
            navigation.setSelectedItemId(R.id.navigation_patients);
        } else if (idNavigation == R.id.navigation_records) {
            navigation.setSelectedItemId(R.id.navigation_records);
        } else if (idNavigation == R.id.navigation_calender) {
            navigation.setSelectedItemId(R.id.navigation_calender);
        } else if (idNavigation == R.id.navigation_more) {
            navigation.setSelectedItemId(R.id.navigation_more);
        } else {
            navigation.setSelectedItemId(R.id.fl_container);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("navigationId", navigation.getSelectedItemId());
    }

    private boolean showReminderBackup() {
        BackupDriveTable backupDriveTable = new BackupDriveTable(this);
        long last_backup = backupDriveTable.getLastUpdateData();

        SharedPreferences prefs = getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        String reminderValue = prefs.getString("reminder_value", "2");

        List<RadioCheckModel> listRemind = ListValue.list_backup_reminder2(this);
        int valueReminder = Integer.parseInt(listRemind.get(Integer.parseInt(reminderValue) - 1).getValue());

        if (reminderValue.equals(JConst.value_never)) {
            return false;
        } else if (Global.addDay(last_backup, valueReminder) <= Global.serverNowLong()) {
            return true;
        }
        return false;
    }

    private void showWarningExpired() {
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;
        long activePeriod = Global.getMillisDateStrip(loginCompanyModel.getGrace_period_date());
        long warningPeriod = Global.addDay(Global.serverNowLong(), 7);
        long lastWarning = JApplication.getInstance().sharedPrefSubscription.getLong("last_warning", 0);

        if (Global.serverNowLong() > lastWarning && warningPeriod >= activePeriod) {
            //show dialog
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(home.this, RenewSubscriptionActivity.class));
                }
            };
            ShowDialog.confirmDialog(home.this, getString(R.string.app_name), getString(R.string.warning_expired), runnable);

            //set last show ke preference
            JApplication.getInstance().sharedPrefSubscriptionEdit.putLong("last_warning", Global.addDay(Global.serverNowWithoutTimeLong(), 1));
            JApplication.getInstance().sharedPrefSubscriptionEdit.apply();
        }

    }

    private void showIsExpired() {
        if (Global.isExpired()) {
            Intent intent = new Intent(home.this, RenewSubscriptionActivity.class);
            intent.putExtra("expired", true);
            startActivity(intent);
        }
    }

    //juli tambah
    public void get_real_url() {
        if (JApplication.getInstance().isGetUrl) {
            if (!JApplication.getInstance().isSedangCekVersi) {
                check_versi(home.this);
            }
            return;
        }
        JApplication.getInstance().real_url = Routes.URL_API_AWAL;
        String url = Routes.URL_GET_REAL_API;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Hello")) {
                    JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                } else {
                    JApplication.getInstance().real_url = response.replace("http://", "");
                    JApplication.getInstance().real_url = "http://" + JApplication.getInstance().real_url + "/" + Routes.NAMA_API + "/public/";
                    JApplication.getInstance().isGetUrl = true;

                    if (!SharedPrefsUtils.getBooleanPreference(home.this, "is_mysql", false)) {
                        GlobalMySQL.check_email_mysql(home.this, JApplication.getInstance().loginInformationModel.getUsername(), () -> {
                        });
                    }
                    if (!JApplication.getInstance().isSedangCekVersi) {
                        check_versi(home.this);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Hapus InstallStateUpdatedListener saat aktivitas dihentikan.xxx
        appUpdateManager.unregisterListener(installStateUpdatedListener);
    }

    private void checkForAppUpdate() {
        // Buat objek AppUpdateInfo.xxx
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                versiPlaystore = appUpdateInfo.availableVersionCode();
            }
        });
    }

    private void startInAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            // Mulai proses in-app update dengan menggunakan requestCode unik.xxx
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    APP_UPDATE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        // Cukup lanjutkan dan instal pembaruan.xxx
        appUpdateManager.completeUpdate();
    }


    private void ShowMigrasiMySQL() {
        ShowDialog.migrasiMySQLDialog(home.this);
    }

    private void checkStatusSubscription() {
        /**cek status subscription */
        long lastCekSubs = SharedPrefsUtils.getLongPreference(getApplicationContext(), "last_cek_subscription", 0);
        long dateNow = Global.serverNowWithoutTimeLong();
        if (dateNow > lastCekSubs && JApplication.getInstance().loginCompanyModel.getId() != 0) {
            if (SharedPrefsUtils.getBooleanPreference(home.this, "is_mysql", false)) {
                GlobalMySQL.getStatusSubsription(home.this, getApplicationContext(), false);
            } else {
//                GlobalOdoo.getStatusSubsription(home.this, getApplicationContext());
            }
        }
    }


    private void check_versi(Activity activity) {
        JApplication.getInstance().isSedangCekVersi = true;
        String url = Routes.url_cek_versi();

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response); // Mengonversi respons ke JSONObject
                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    int versiCloud = resultObject.getInt("value");
                    int versi = JApplication.getInstance().getVersionCode();

                    if (versi < versiCloud) {
                        Runnable runOk = new Runnable() {
                            @Override
                            public void run() {
                                //buka Playstore
                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.orion.pasienqu_2");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                activity.startActivity(intent);
                            }
                        };
                        Runnable runDissmis = new Runnable() {
                            @Override
                            public void run() {
                                activity.finishAffinity();
                            }
                        };

                        if (versiCloud <= versiPlaystore || versiPlaystore == 0) {
                            // mulai proses in-app update.
                            ShowDialog.confirmDialogWithDissmis(activity, "PasienQu", getString(R.string.inform_update_latest_version), runOk, runDissmis);
                        }


                    } else if (versi >= versiCloud) {
                        long lastCekVersiPlaystore = SharedPrefsUtils.getLongPreference(getApplicationContext(), "last_cek_versi_playstore", 0);
                        long dateNow = Global.serverNowWithoutTimeLong();
                        if (dateNow > lastCekVersiPlaystore) {
                            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                    int versiPlaystore = appUpdateInfo.availableVersionCode();
                                    if (versi < versiPlaystore || versiCloud < versiPlaystore) {
                                        // mulai proses in-app update.
                                        startInAppUpdate(appUpdateInfo);
                                        SharedPrefsUtils.setLongPreference(home.this, "last_cek_versi_playstore", Global.serverNowWithoutTimeLong());
                                    }
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!e.getMessage().contains("cannot") && !e.getMessage().contains("JSONObject")) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                JApplication.getInstance().isSedangCekVersi = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.finishAffinity();
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    /*private void get_ip_address(){
        progressDialog = new ProgressDialog(home.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Biarkan false jika Anda ingin mencegah pengguna menutupnya
        progressDialog.show();
        //pertama cobain konek ke ip sesuai sharedfreef
        Runnable runSukses = new Runnable() {
            @Override
            public void run() {
                Global.dismissProgressDialog(home.this, progressDialog);
                JApplication.getInstance().isGetUrl = true;
            }
        };

        Runnable runGagal = new Runnable() {
            @Override
            public void run() {
                Global.dismissProgressDialog(home.this, progressDialog);
//                Toast.makeText(JApplication.currentActivity, "Tidak terhubung ke server", Toast.LENGTH_SHORT).show();
                Runnable runRetry = new Runnable() {
                    @Override
                    public void run() {
                        get_ip_address();
                    }
                };
                ShowDialog.infoDialogWithRunnable(JApplication.currentActivity, getString(R.string.app_name), getString(R.string.error_connection_timeout), runRetry);

            }
        };
        Runnable runAmbilIPDariDrive = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_DRIVE_PROFILE;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String ip = cariNilai(response, Routes.NAMA_API);
                        ip = decryptNew(ip);
                        ip = "http://"+ip;
                        cek_koneksi(ip, runSukses, runGagal);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runGagal.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

        Runnable runAmbilIPOrionbdg = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().real_url = Routes.URL_API_AWAL;
                String url = Routes.URL_GET_REAL_API;
                StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Hello")){
                            runAmbilIPDariDrive.run();
                        }else{
                            cek_koneksi(response, runSukses, runAmbilIPDariDrive);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        runAmbilIPDariDrive.run();
                    }
                });
                JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
            }
        };

//        String ip = JApplication.getInstance().sharedPreferencesSetting.getString("ip", "");
//        cek_koneksi(ip, runSukses, runAmbilIPOrionbdg);
        String ip = "http://192.168.18.5";//"https://pasienqu.com";//JApplication.getInstance().sharedPreferencesSetting.getString("ip", "");
        cek_koneksi(ip, runSukses, runAmbilIPOrionbdg);
    }


    public static String cariNilai(String input, String cari) {
        // Split string berdasarkan newline
        String[] lines = input.split("\n");

        // Loop melalui setiap baris
        for (String line : lines) {
            // Split baris berdasarkan tanda sama (=)
            String[] parts = line.split("=");
            if (parts.length == 2 && parts[0].equals(cari)) {
                // Jika kunci ditemukan, kembalikan nilainya
                return parts[1];
            }
        }

        // Kembalikan string kosong jika kunci tidak ditemukan
        return "";
    }

    private void cek_koneksi(String url, Runnable runSukses, Runnable runGagal){
        String link = url+"/"+Routes.NAMA_API+"/public/cek_koneksi.php";
        StringRequest strReq = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JApplication.getInstance().real_url = url;
                JApplication.getInstance().updateIPSharePref(url);
                JApplication.getInstance().real_url = JApplication.getInstance().real_url+"/"+Routes.NAMA_API+"/public/";
                runSukses.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runGagal.run();
            }
        });
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static String decryptNew(String input) {
        HashMap<String, String> list = new HashMap<>();
        list.put("e9r", "a");
        list.put("asF", "b");
        list.put("ytH", "c");
        list.put("43y", "d");
        list.put("nSu", "e");
        list.put("fdQ", "f");
        list.put("uyo", "g");
        list.put("jhr", "h");
        list.put("vgb", "i");
        list.put("nm7", "j");
        list.put("cvF", "k");
        list.put("yKV", "l");
        list.put("k93", "m");
        list.put("fdk", "n");
        list.put("vfd", "o");
        list.put("34g", "p");
        list.put("320", "q");
        list.put("eds", "r");
        list.put("ehj", "s");
        list.put("ebv", "t");
        list.put("etr", "u");
        list.put("w94", "v");
        list.put("vcf", "w");
        list.put("pty", "x");
        list.put("j9f", "y");
        list.put("xje", "z");
        list.put("e92", "A");
        list.put("asD", "B");
        list.put("yFH", "C");
        list.put("4Xy", "D");
        list.put("n1u", "E");
        list.put("GdQ", "F");
        list.put("Yyo", "G");
        list.put("jJr", "H");
        list.put("v7b", "I");
        list.put("NM7", "J");
        list.put("c0F", "K");
        list.put("QeV", "L");
        list.put("kg3", "M");
        list.put("jhk", "N");
        list.put("cir", "O");
        list.put("dZi", "P");
        list.put("kR1", "Q");
        list.put("fdd", "R");
        list.put("jLi", "S");
        list.put("f9w", "T");
        list.put("DYx", "U");
        list.put("vde", "V");
        list.put("akb", "W");
        list.put("dsf", "X");
        list.put("gfv", "Y");
        list.put("jul", "Z");
        list.put("cHs", "0");
        list.put("dIe", "1");
        list.put("lgu", "2");
        list.put("mIe", "3");
        list.put("SuM", "4");
        list.put("heN", "5");
        list.put("32x", "6");
        list.put("efr", "7");
        list.put("fd8", "8");
        list.put("fUK", "9");

        StringBuilder hasil = new StringBuilder();
        StringBuilder cari = new StringBuilder();
        int charke = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            if (Character.isLetterOrDigit(currentChar)) {
                charke++;
                cari.append(currentChar);
                if (charke == 3) {
                    String temp = list.get(cari.toString());
                    if (temp == null || temp.isEmpty()) {
                        temp = cari.toString();
                    }
                    hasil.append(temp);
                    cari = new StringBuilder();
                    charke = 0;
                }
            } else {
                hasil.append(currentChar);
            }
        }

        return hasil.toString();
    }*/

    private void createAppDirectory() {
        // Dapatkan path direktori eksternal
        File externalDir = getExternalFilesDir(null);

        if (externalDir != null) {
            // Dapatkan path untuk direktori aplikasi
            File appDir = new File(externalDir, "package");

            // Buat direktori jika belum ada
            if (!appDir.exists()) {
                if (appDir.mkdirs()) {
                    Log.d("MyApp", "Direktori berhasil dibuat");
                } else {
                    Log.e("MyApp", "Gagal membuat direktori");
                }
            }
        } else {
            Log.e("MyApp", "Gagal mendapatkan path eksternal");
        }
    }

    private void importICDBaru() {
        ICDTable icdTable = new ICDTable(getApplicationContext());
        String isBaru = JApplication.getInstance().globalTable.GetStringFromTable("pasienqu_icd10", "isBaru", "limit 1");

        if (!isSedangImportICD && (isBaru.equals(JConst.FALSE_STRING) || isBaru.equals(""))) {
            isSedangImportICD = true;
            icdTable.deleteAll();
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            dialog.setMax(totalDataIcdBaru());
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.import_icd_msg));
            dialog.show();

            Thread mThread = new Thread() {
                @Override
                public void run() {

                    InputStream inputStream;
                    inputStream = getResources().openRawResource(R.raw.icd10baru);
                    String[] ids;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        String csvLine;
                        int i = 0;
                        SQLiteDatabase db = JApplication.getInstance().dbConn.getWritableDatabase();
                        DatabaseUtils.InsertHelper iHelp = new DatabaseUtils.InsertHelper(db, "pasienqu_icd10");
                        db.beginTransaction();
                        while ((csvLine = reader.readLine()) != null) {
                            i++;
                            dialog.setProgress(i);
                            ids = csvLine.split(";");
                            try {

                                ICDModel icdModel = new ICDModel();
                                icdModel.setId(Integer.parseInt(ids[0]));
                                icdModel.setCode(ids[1]);
                                icdModel.setName(ids[2]);

                                //optimasi insert biar cepet ea
                                int id_index = iHelp.getColumnIndex("id");
                                int code_index = iHelp.getColumnIndex("code");
                                int name_index = iHelp.getColumnIndex("name");
                                int isBaru_index = iHelp.getColumnIndex("isBaru");

                                iHelp.prepareForInsert();
                                iHelp.bind(id_index, icdModel.getId());
                                iHelp.bind(code_index, icdModel.getCode());
                                iHelp.bind(name_index, icdModel.getName());
                                iHelp.bind(isBaru_index, "T");
                                iHelp.execute();
                            } catch (Exception e) {
                                Log.e("empty", e.toString());
                            }
                        }
                        //close trans
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        isSedangImportICD = false;
                    } catch (IOException ex) {
                        throw new RuntimeException("Error in reading CSV file: " + ex);

                    }
                    dialog.dismiss();
                }
            };
            mThread.start();
        }
    }

    private int totalDataIcdBaru() {
        InputStream inputStream2 = getResources().openRawResource(R.raw.icd10baru);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
        int total = 0;
        try {
            String csvLine2;
            while ((csvLine2 = reader2.readLine()) != null) {
                total++;
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
        return total;
    }

    private void cekKirimUlang() {
        String url = JApplication.getInstance().globalTable.GetStringFromTable("kirim_ulang", "url", "order by id limit 1");
        String body = JApplication.getInstance().globalTable.GetStringFromTable("kirim_ulang", "body", "order by id limit 1");

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(body)) {
            postUrlBody(url, body);
        }else if (!TextUtils.isEmpty(url)){
            postUrl(url);
        }
    }

    private void postUrl(String url) {
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                String status = new JSONObject(response).getString("status");
                if (status.equals(JConst.STATUS_API_SUCCESS)) {
                    JApplication.getInstance().db.execSQL("DELETE FROM kirim_ulang WHERE url = '" + url + "'");
                    //cek jika masih ada yang nyangkut.
                    cekKirimUlang();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Global.showErrorMessage(this, error.toString());
        });
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

    private void postUrlBody(String url, String body) {
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has(JConst.STATUS_API_SUCCESS)) {
                    JApplication.getInstance().db.execSQL("DELETE FROM kirim_ulang WHERE url = '" + url + "'");
                    //cek jika masih ada yang nyangkut.
                    cekKirimUlang();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Global.showErrorMessage(this, error.toString());
            })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }

        };
        JApplication.getInstance().addToRequestQueue(strReq, JConst.tag_json_obj);
    }

}

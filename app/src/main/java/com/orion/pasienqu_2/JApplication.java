package com.orion.pasienqu_2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orion.pasienqu_2.data_table.AppointmentTable;
import com.orion.pasienqu_2.data_table.BillingItemTable;
import com.orion.pasienqu_2.data_table.BillingTable;
import com.orion.pasienqu_2.data_table.CountryTable;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.data_table.GlobalTable;
import com.orion.pasienqu_2.data_table.ICDTable;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.data_table.PractitionerTable;
import com.orion.pasienqu_2.data_table.RecordDiagnosaTable;
import com.orion.pasienqu_2.data_table.RecordFileTable;
import com.orion.pasienqu_2.data_table.RecordTable;
import com.orion.pasienqu_2.data_table.SatuSehatTokenTable;
import com.orion.pasienqu_2.data_table.SubaccountTable;
import com.orion.pasienqu_2.data_table.SyncDownTable;
import com.orion.pasienqu_2.data_table.SyncInfoTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.data_table.more.BillingTemplateTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.MySQLConnection;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;
import com.orion.pasienqu_2.models.LoginModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class JApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public DBConn dbConn;
    public DBConnV1 dbConnV1;
    public SQLiteDatabase db;
    public SQLiteDatabase dbBlob;
    private static com.orion.pasienqu_2.JApplication mInstance;
    private RequestQueue mRequestQueue;
    public static final String TAG = com.orion.pasienqu_2.JApplication.class.getSimpleName();
//    public final static NumberFormat fmt = NumberFormat.getInstance();
    public final static NumberFormat fmt = NumberFormat.getInstance(new Locale("in", "ID"));
    public final static DecimalFormat df1 = new DecimalFormat("#.##");
    private String VersionName;
    private int VersionCode;
    public boolean isLogOn;
    private RequestQueue _requestQueue;
    public String dbV1Location;
    public String dbBlobLocation;
    public boolean isGetUrl;


    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "session_id";


    private LoginModel dtLoginGlobal;
//    public LoginTable loginTable;
    public NoteTemplateTable noteTemplateTable;
    public WorkLocationTable workLocationTable;
    public PatientTable patientTable;
    public BillingTable billingTable;
    public RecordTable recordTable;
    public SubaccountTable subaccountTable;
    public GenderTable genderTable;
    public AppointmentTable appointmentTable;
    public GlobalTable globalTable;
    public BillingTemplateTable billingTemplateTable;
    public CountryTable countryTable;
    public ICDTable icdTable;
    public BillingItemTable billingItemTable;
    public RecordDiagnosaTable recordDiagnosaTable;
    public RecordFileTable recordFileTable;
    public PractitionerTable practitionerTable;
    public SatuSehatTokenTable satuSehatTokenTable;

    public SyncInfoTable syncInfoTable;
    public SyncDownTable syncDownTable;
    public boolean isSyncExpenseProof = false;
    public boolean isSyncDailyTripLog = false;

    public String SelectedLov = "";

//    public OdooConnection odooConnection; //erik tutup odoo 040325
    public MySQLConnection mySQLConnection;
    public LoginInformationModel loginInformationModel;
    public LoginCompanyModel loginCompanyModel;

    public SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferencesCompany;
    SharedPreferences encryptSharedPreferences;
    SharedPreferences sharedPrefMasterDevice;
    public SharedPreferences sharedPrefSubscription;
    public SharedPreferences.Editor sharedPrefSubscriptionEdit;

    public int globalIdx;
    public String lokasi_db;
    public int lastIdxList = 0;
    public boolean isAfterSignup = false;
    public boolean isFirstTimeRequest = true;
    public boolean isSdgCekSub = false;

    public String real_url = "";
    public boolean isSedangCekVersi = false;
    public SharedPreferences sharedPreferencesSetting;

    public static String currentActivityName;
    public static Activity currentActivity;
    public static String locationIHS;
    public static String patientIHS;
    public static String practitionerIHS;
    public static String renewSubsBody;
    public static String accessToken;
    public static long lastModifiedBackup;
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        this.dbConn = new DBConn(getApplicationContext());
        this.dbConnV1 = new DBConnV1(getApplicationContext());
        this.db = new DBConn(JApplication.this).getWritableDatabase();
        mInstance = this;
        this.isLogOn = false;
        this.dbV1Location = this.dbConnV1.getDatabaseLocation();
        GetVersion();
        this.lokasi_db = this.dbConn.getDatabaseLocation();
        this.isGetUrl = false;

//        loginTable = new LoginTable(JApplication.this);
        syncInfoTable = new SyncInfoTable(JApplication.this);
        syncDownTable = new SyncDownTable(JApplication.this);
        noteTemplateTable = new NoteTemplateTable(JApplication.this);
        workLocationTable = new WorkLocationTable(JApplication.this);
        patientTable = new PatientTable(JApplication.this);
        billingTable = new BillingTable(JApplication.this);
        recordTable = new RecordTable(JApplication.this);
        genderTable = new GenderTable(JApplication.this);
        appointmentTable = new AppointmentTable(JApplication.this);
        globalTable = new GlobalTable(JApplication.this);
        billingTemplateTable = new BillingTemplateTable(JApplication.this);
        subaccountTable = new SubaccountTable(JApplication.this);
        countryTable = new CountryTable(JApplication.this);
        icdTable = new ICDTable(JApplication.this);
        billingItemTable = new BillingItemTable(JApplication.this);
        recordDiagnosaTable = new RecordDiagnosaTable(JApplication.this);
        recordFileTable = new RecordFileTable(JApplication.this);
        loginInformationModel = new LoginInformationModel();
        loginCompanyModel = new LoginCompanyModel();

        //satusehat
        practitionerTable = new PractitionerTable(JApplication.this);
        satuSehatTokenTable = new SatuSehatTokenTable(JApplication.this);

        //create share pref
        sharedpreferences = getSharedPreferences("login_information", Context.MODE_PRIVATE);
        sharedpreferencesCompany = getSharedPreferences("login_company", Context.MODE_PRIVATE);
        encryptSharedPreferences = Global.getEncryptSharedPreference(this);
        sharedPrefMasterDevice = getSharedPreferences("masterDevice", Context.MODE_PRIVATE);
        sharedPrefSubscription = getSharedPreferences("subscription_pref", Context.MODE_PRIVATE);
        sharedPrefSubscriptionEdit = getSharedPreferences("subscription_pref", Context.MODE_PRIVATE).edit();

        //session
        if (!sharedpreferences.contains("username")){
//            odooConnection = new OdooConnection(JApplication.this);   //erik tutup odoo 040325
        }else{
            setLoginInformationBySharedPreferences();
//            setLoginCompanyBySharedPreferences();  //tutup coba pake enkripsi
            setEncryptLoginCompanyBySharedPreferences();
//            odooConnection = new OdooConnection(JApplication.this, sharedpreferences.getString("sessionId", "")); //erik tutup odoo 040325
        }
        //session mysql
        if (!sharedpreferences.contains("username")){
            mySQLConnection = new MySQLConnection(JApplication.this);
        }else{
            setLoginInformationBySharedPreferences();
            setEncryptLoginCompanyBySharedPreferences();
            mySQLConnection = new MySQLConnection(JApplication.this,
                    sharedpreferences.getString("sessionId", ""));
        }

        _requestQueue = Volley.newRequestQueue(this);

        sharedPreferencesSetting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        if (!sharedPreferencesSetting.contains("ip")){
            updateIPSharePref(Routes.IP_ADDRESS);
        }
    }

    public void updateIPSharePref(String ip){
        SharedPreferences.Editor editor = sharedPreferencesSetting.edit();
        editor.putString("ip", ip);
        editor.apply();
    }

    public void setLoginInformationBySharedPreferences(){
        loginInformationModel.setUid(sharedpreferences.getInt("uid", 0));
        loginInformationModel.setCompanyId(sharedpreferences.getInt("companyId", 0));
        loginInformationModel.setUsername(sharedpreferences.getString("username", ""));
        loginInformationModel.setDatabase(sharedpreferences.getString("database", ""));
        loginInformationModel.setTz(sharedpreferences.getString("tz", ""));
        loginInformationModel.setHost(sharedpreferences.getString("host", ""));
        loginInformationModel.setName(sharedpreferences.getString("name", ""));
        loginInformationModel.setSessionId(sharedpreferences.getString("sessionId", ""));
        loginInformationModel.setActiveUntil(sharedpreferences.getString("activeUntil", ""));
        loginInformationModel.setUuidLocation(sharedpreferences.getString("uuidLocation", ""));
        loginInformationModel.setCookie(sharedpreferences.getString(COOKIE_KEY, ""));
        loginInformationModel.setPassword(sharedpreferences.getString("password", ""));
        loginInformationModel.setAutoGeneratePatientId(sharedpreferences.getBoolean("autogenerate_patient_id", true));
        loginInformationModel.setUsePin(sharedpreferences.getBoolean("usePin", false));
        loginInformationModel.setPinProtection(sharedpreferences.getString("pinProtection", ""));
        loginInformationModel.setLanguage(sharedpreferences.getString("language", ""));
        loginInformationModel.setIs_subuser(sharedpreferences.getBoolean("is_subuser", false));
    }


    public void setLoginCompanyBySharedPreferences(){
        loginCompanyModel.setId(sharedpreferencesCompany.getInt("id", 0));
        loginCompanyModel.setCountry_id(sharedpreferencesCompany.getInt("country_id", 0));
        loginCompanyModel.setName(sharedpreferencesCompany.getString("name", ""));
        loginCompanyModel.setProduct(sharedpreferencesCompany.getString("product", ""));
        loginCompanyModel.setZip(sharedpreferencesCompany.getString("zip", ""));
        loginCompanyModel.setMember_type(sharedpreferencesCompany.getString("member_type", ""));
        loginCompanyModel.setExpired_date(sharedpreferencesCompany.getString("expired_date", ""));
        loginCompanyModel.setRegister_date(sharedpreferencesCompany.getString("register_date", ""));
        loginCompanyModel.setBilling_period(sharedpreferencesCompany.getString("billing_period", ""));
        loginCompanyModel.setStreet(sharedpreferencesCompany.getString("street", ""));
        loginCompanyModel.setStreet2(sharedpreferencesCompany.getString("street2", ""));
        loginCompanyModel.setPhone(sharedpreferencesCompany.getString("phone", ""));
        loginCompanyModel.setEmail(sharedpreferencesCompany.getString("email", ""));
        loginCompanyModel.setLatest_activation_date(sharedpreferencesCompany.getString("latest_activation_date", ""));
        loginCompanyModel.setGrace_period_date(sharedpreferencesCompany.getString("grace_period_date", ""));
        loginCompanyModel.setAutogenerate_patient_id(sharedpreferencesCompany.getString("autogenerate_patient_id", ""));
        loginCompanyModel.setIs_trial(sharedpreferencesCompany.getBoolean("is_trial", true));
    }

    public void setEncryptLoginCompanyBySharedPreferences(){
        encryptSharedPreferences = Global.getEncryptSharedPreference(this);
        loginCompanyModel.setId(encryptSharedPreferences.getInt("id", 0));
        loginCompanyModel.setCountry_id(encryptSharedPreferences.getInt("country_id", 0));
        loginCompanyModel.setName(encryptSharedPreferences.getString("name", ""));
        loginCompanyModel.setProduct(encryptSharedPreferences.getString("product", ""));
        loginCompanyModel.setZip(encryptSharedPreferences.getString("zip", ""));
        loginCompanyModel.setMember_type(encryptSharedPreferences.getString("member_type", ""));
        loginCompanyModel.setExpired_date(encryptSharedPreferences.getString("expired_date", ""));
        loginCompanyModel.setRegister_date(encryptSharedPreferences.getString("register_date", ""));
        loginCompanyModel.setBilling_period(encryptSharedPreferences.getString("billing_period", ""));
        loginCompanyModel.setStreet(encryptSharedPreferences.getString("street", ""));
        loginCompanyModel.setStreet2(encryptSharedPreferences.getString("street2", ""));
        loginCompanyModel.setPhone(encryptSharedPreferences.getString("phone", ""));
        loginCompanyModel.setEmail(encryptSharedPreferences.getString("email", ""));
        loginCompanyModel.setLatest_activation_date(encryptSharedPreferences.getString("latest_activation_date", ""));
        loginCompanyModel.setGrace_period_date(encryptSharedPreferences.getString("grace_period_date", ""));
        loginCompanyModel.setAutogenerate_patient_id(encryptSharedPreferences.getString("autogenerate_patient_id", ""));
        loginCompanyModel.setIs_trial(encryptSharedPreferences.getBoolean("is_trial", true));
        loginCompanyModel.setTrial_end_date(encryptSharedPreferences.getString("trial_end_date", ""));
        loginCompanyModel.setPurchase_token(encryptSharedPreferences.getString("purchase_token", ""));
    }

    private void GetVersion(){
        try {
            this.VersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            this.VersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static synchronized com.orion.pasienqu_2.JApplication getInstance() {
        return mInstance;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public LoginModel getDtLoginGlobal() {
        return dtLoginGlobal;
    }

    public void setDtLoginGlobal(LoginModel dtLoginGlobal) {
        this.dtLoginGlobal = dtLoginGlobal;
    }


    public RequestQueue getRequestQueue() {
        return _requestQueue;
    }


    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            String cookieMst = cookie;
            JApplication.getInstance().loginInformationModel.setCookie(cookieMst);
            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];
                SharedPreferences.Editor prefEditor = sharedpreferences.edit();
                prefEditor.putString(SESSION_COOKIE, cookie);
                prefEditor.putString(COOKIE_KEY, cookieMst);
                prefEditor.commit();
            }
        }
    }


    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = sharedpreferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }

    public void clearSharedPreperence(){
        sharedpreferences.edit().clear().commit();
        sharedpreferencesCompany.edit().clear().commit();
        encryptSharedPreferences.edit().clear().commit();
        sharedPrefMasterDevice.edit().clear().commit();
//        SharedPrefsUtils.setBooleanPreference(this, "is_mysql", false);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivityName = activity.getClass().getSimpleName();
        currentActivity = activity;
        // Di sini Anda dapat melakukan apa pun yang perlu dilakukan ketika activity aktif berubah.
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}

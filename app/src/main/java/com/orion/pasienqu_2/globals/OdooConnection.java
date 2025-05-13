package com.orion.pasienqu_2.globals;
/*
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.data_table.LoginTable;
import com.orion.pasienqu_2.data_table.SyncDownTable;
import com.orion.pasienqu_2.models.LoginInformationModel;
import com.orion.pasienqu_2.models.LoginModel;
import com.orion.pasienqu_2.models.SyncDownModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oogbox.api.odoo.OdooClient;
import oogbox.api.odoo.OdooUser;
import oogbox.api.odoo.client.helper.OdooErrorException;
import oogbox.api.odoo.client.helper.data.OdooRecord;
import oogbox.api.odoo.client.helper.data.OdooResult;
import oogbox.api.odoo.client.helper.utils.OArguments;
import oogbox.api.odoo.client.helper.utils.ODomain;
import oogbox.api.odoo.client.helper.utils.OdooFields;
import oogbox.api.odoo.client.helper.utils.OdooValues;
import oogbox.api.odoo.client.listeners.AuthenticateListener;
import oogbox.api.odoo.client.listeners.IOdooResponse;

public class OdooConnection {
    private OdooClient client;
    private OdooUser odooUser;
    private Context context;
    private OdooValues values;
    private String uuid = "";
    private String expense_ids = "";

    public OdooConnection(Context context) {
        client = new OdooClient.Builder(context)
                .setHost(JConst.HOST_SERVER)
                .build();
    }

    public OdooConnection(Context context, String session) {
        client = new OdooClient.Builder(context)
                .setHost(JConst.HOST_SERVER)
                .setSession(session)
                .build();
    }

    public OdooClient getClient() {
        return client;
    }

    public void setClient(OdooClient client) {
        this.client = client;
    }

    public OdooUser getOdooUser() {
        return odooUser;
    }

    public void setOdooUser(OdooUser odooUser) {
        this.odooUser = odooUser;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void login(String email, String password, AuthenticateListener loginCallback){
        if (client.isConnected()) {
            client.authenticate(email, password, JConst.DB_SERVER_NAME, loginCallback);
        }
    }



    public void download(Context appContext){
    }

    public void loadLoginInformation(Context appContext, Activity activity, OdooUser user, String userid, Runnable runnableSuccess){
        SharedPreferences sharedpreferences;
        sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("uid", user.uid);
        editor.putInt("companyId", user.companyId);
        editor.putString("username", user.username);
        editor.putString("database", user.database);
        editor.putString("tz", user.tz);
        editor.putString("host", user.host);
        editor.putString("name", user.name);
        editor.putString("sessionId", user.sessionId);
//      data yang masih difix
//        editor.putString("archivedUntil", "2021-01-01");
//        editor.putString("uuidLocation", "");
        editor.putBoolean("is_subuser", user.isSuperuser);

        editor.commit();
        editor.apply();

        //masukin ke logininformation
        LoginInformationModel loginInformationModel = new LoginInformationModel();

        JApplication.getInstance().setLoginInformationBySharedPreferences();
    }

    public void loadLoginInformation(Context appContext, Activity activity, JSONObject obj, Runnable runnableSuccess, String password){

        try {
            SharedPreferences sharedpreferences;
            sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("uid", obj.getInt("uid"));
            editor.putInt("companyId", obj.getInt("company_id"));
            editor.putString("username", obj.getString("username"));
            editor.putString("database", obj.getString("db"));
            editor.putString("tz", "");
            editor.putString("host", obj.getString("web.base.url"));
            editor.putString("name", obj.getString("partner_display_name"));
            editor.putString("sessionId", obj.getString("session_id"));
    //      data yang masih difix
//            editor.putString("archivedUntil", "2021-01-01");
//            editor.putString("uuidLocation", "");
            editor.putString("password", password);
            editor.putBoolean("is_subuser", obj.getBoolean("odoobot_initialized"));

            editor.commit();
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //masukin ke logininformation
        LoginInformationModel loginInformationModel = new LoginInformationModel();

        JApplication.getInstance().setLoginInformationBySharedPreferences();
        GlobalOdoo.get_company(activity, appContext, runnableSuccess);
    }

    public void upload(SyncInfoModel syncInfoModel, Context appContext){
        if (syncInfoModel.getCommand().equals("create")){
            this.create(syncInfoModel, appContext);
        }
        if (syncInfoModel.getCommand().equals("write")){
            this.write(syncInfoModel, appContext);
        }
    }

    private OdooValues getOdooValues(SyncInfoModel syncInfoModel){
        OdooValues values = new OdooValues();
        byte[] blob = syncInfoModel.getContent();
        String value = new String(blob);

        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray keys = jsonObject.names();
            for (int i = 0; i < keys.length (); i++) {
                String key = keys.getString (i);
                Object object = jsonObject.get(key);
                values.put(key, object);
                if (key.equals("uuid")){
                    uuid = jsonObject.getString(key);
                }
            }
        } catch (JSONException e) {
            Log.d("gagal", e.getMessage().toString());
        } catch (Exception e) {
            Log.d("gagal", e.getMessage().toString());
        }
        return values;
    }

    public void create(SyncInfoModel syncInfoModel, Context appContext){
        this.expense_ids = "";
        this.uuid = "";
        OdooValues values = getOdooValues(syncInfoModel);
        final String expense_ids_local = this.expense_ids;
        final String uuidLocal = this.uuid;

        client.create(syncInfoModel.getModel(), values, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                Log.d("Hasil", result.toString());
                ((JApplication)appContext).syncInfoTable.delete(syncInfoModel.getId());
            }
        });
    }

    public void write(SyncInfoModel syncInfoModel, Context appContext){
        this.expense_ids = "";
        this.uuid = "";
        OdooValues values = getOdooValues(syncInfoModel);
        final String expense_ids_local = this.expense_ids;
        final String uuidLocal = this.uuid;

        ODomain domain = new ODomain();
        domain.add("uuid","=", uuid);
        OdooFields fields = new OdooFields();
        fields.addAll("id");
        int offset = 0;
        int limit = 1;
        String sorting = "id";
        String model = syncInfoModel.getModel();
        client.searchRead(model, domain, fields, offset, limit, sorting, new IOdooResponse() {
            @Override
            public void onResult(OdooResult result) {
                OdooRecord[] records = result.getRecords();
                int id = 0;
                for (OdooRecord record : records) {
                    id = record.getInt("id");
                }

                client.write(syncInfoModel.getModel(), new Integer[]{id}, values, new IOdooResponse() {
                    @Override
                    public void onResult(OdooResult result) {
                        Log.d("Hasil", result.toString());
                        ((JApplication)appContext).syncInfoTable.delete(syncInfoModel.getId());
                    }

                    @Override
                    public boolean onError(OdooErrorException error) {
                        Log.d("Hasil", error.toString());
                        return false;
                    }
                });
            }
        });
    }

    private int get_id_by_object_odoo(ArrayList<Object> arrayList){
        String tempp = arrayList.get(0).toString();
        double temp = Double.valueOf(tempp);
        return ((int) temp);
    }

    private String record_to_string(String string){
        if (string.equals("false")){
            return "";
        }else{
            return string;
        }
    }

    public void loadNotesTemplate(Context appContext, Activity activity, Runnable runnableSuccess){
        return;
    }
}
*/
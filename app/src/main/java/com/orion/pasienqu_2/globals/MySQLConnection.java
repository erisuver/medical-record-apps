package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.models.LoginInformationModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MySQLConnection {
    private Context context;
    private String uuid = "";
    private String expense_ids = "";

    public MySQLConnection(Context context) {
    }

    public MySQLConnection(Context context, String session) {
    }

    public Context getContext() {
        return context;
    }

    /**erik tutup odoo 040325
    public void loadLoginInformation(Context appContext, Activity activity, OdooUser user, String userid, Runnable runnableSuccess){
        SharedPreferences sharedpreferences;
        sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("uid", user.uid);
        editor.putInt("companyId", user.companyId);
        editor.putString("username", user.username);
        editor.putString("database", user.database);
        editor.putString("tz", "");
        editor.putString("host", "");
        editor.putString("name", user.name);
        editor.putString("sessionId", "");
        editor.putBoolean("is_subuser", false);

        editor.commit();
        editor.apply();

        //masukin ke logininformation
        LoginInformationModel loginInformationModel = new LoginInformationModel();

        JApplication.getInstance().setLoginInformationBySharedPreferences();
    }
*/

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
            editor.putString("host", "");
            editor.putString("name", obj.getString("name"));
            editor.putString("sessionId", "");
            editor.putString("password", password);
            editor.putBoolean("is_subuser", false);

            editor.commit();
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //masukin ke logininformation
        LoginInformationModel loginInformationModel = new LoginInformationModel();

        JApplication.getInstance().setLoginInformationBySharedPreferences();
        GlobalMySQL.get_company(activity, appContext, runnableSuccess);
    }

}

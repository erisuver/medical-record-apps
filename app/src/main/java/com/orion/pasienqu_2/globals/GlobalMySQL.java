package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Patterns;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.data_table.PatientTable;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.PatientModel;
import com.orion.pasienqu_2.models.v1.DataPasienModel;
import com.orion.pasienqu_2.utility.GetIPAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalMySQL {
    public static void newLogin(Activity activity, Context appContext, String user, String pass, Runnable runnable) {
        String url = Routes.url_login2();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    if (jsonObjectData.getString("message").equals("Access denied")){
                        Snackbar.make(activity.findViewById(android.R.id.content), R.string.access_denied_error, Snackbar.LENGTH_LONG).show();
                    }else {
                        Snackbar.make(activity.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                    }
                }else {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
                    JApplication.getInstance().mySQLConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, pass);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", user);
                    body.put("password", pass);
                    body.put("db", JConst.DB_SERVER_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public static void changePassword(Activity activity, Context appContext, String oldPass, String newPass, String confirmPass, Runnable runnable, ProgressDialog loading) {
        //validation
        if (oldPass.equals("") || newPass.equals("") || confirmPass.equals("") || oldPass.length() < 6 || newPass.length() < 6 || confirmPass.length() < 6) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            loading.dismiss();
            return;
        }else if(!newPass.equals(confirmPass)){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.change_pwd_error, Snackbar.LENGTH_LONG).show();
            loading.dismiss();
            return;
        }else if(!oldPass.equals(JApplication.getInstance().loginInformationModel.getPassword())){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.change_pwd_old_pass_error, Snackbar.LENGTH_LONG).show();
            loading.dismiss();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            loading.dismiss();
            return;
        }

        //fff
        if (Global.CheckConnectionInternet(activity) && JApplication.getInstance().real_url.isEmpty()) {
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    changePassword(activity, appContext, oldPass, newPass, confirmPass, runnable, loading);
                }
            };
            GetIPAddress.get_ip_address(activity, runSuccess);
            return;
        }

        loading.show();
        String url = Routes.url_change_password();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else{
                    GlobalMySQL.newLogin(activity, appContext, JApplication.getInstance().loginInformationModel.getUsername(), newPass, runnable);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("old_password", oldPass);
                    body.put("new_password", newPass);
                    body.put("id", JApplication.getInstance().loginInformationModel.getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void forgetPassword(Activity activity, Context appContext, String email, Runnable runnable, Runnable loading, Runnable dismiss) {
        //validation
        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }

        //fff
        if (Global.CheckConnectionInternet(activity) && JApplication.getInstance().real_url.isEmpty()) {
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    forgetPassword(activity, appContext, email, runnable, loading, dismiss);
                }
            };
            GetIPAddress.get_ip_address(activity, runSuccess);
            return;
        }

        loading.run();
        String url = Routes.url_forget_password();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")) {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    String erMessage = "Reset password: invalid username or email";
                    if (!erMessage.equals(jsonObjectData.getString("message"))) {
                        Snackbar.make(activity.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                    }else{
                        Snackbar.make(activity.findViewById(android.R.id.content), R.string.error_invalid_username, Snackbar.LENGTH_SHORT).show();
                    }
                    dismiss.run();
                } else {
                    dismiss.run();
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(activity.findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                dismiss.run();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            dismiss.run();
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": " +
                        body.toString() + "}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                    headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);

    }


    public static void exportExternal(Activity activity, Context appContext, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/object/pasienqu.export.excel/action_export_external";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }else{
                    GlobalMySQL.newLogin(activity, appContext, JApplication.getInstance().loginInformationModel.getUsername(), JApplication.getInstance().loginInformationModel.getPassword(), runnable);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    JSONArray itemArray=new JSONArray("\"\"");

                    body.put("args", itemArray);
                    final JSONObject jsonData = new JSONObject();
                    jsonData.put("id", JApplication.getInstance().loginInformationModel.getUid());
                    String stringJson = jsonData.toString();
                    body.put("data", stringJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+
                        body.toString()+"}";
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }



    public static void get_company(Activity activity, Context appContext, Runnable runnable) {
        String url = Routes.url_get_company() + "/" + JApplication.getInstance().loginInformationModel.getCompanyId()
                                              + "?email=" + JApplication.getInstance().loginInformationModel.getUsername(); //tambah email

        StringRequest jArr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response); // Ubah response menjadi JSON array

                    if (jsonArray.length() > 0) {
                        JSONObject json = jsonArray.getJSONObject(0); // Ambil elemen pertama dari JSON array

                        SharedPreferences sharedPreferences = Global.getEncryptSharedPreference(JApplication.getInstance());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("id", json.getInt("id"));
                        editor.putInt("country_id", json.getInt("country_id"));
                        editor.putString("name", json.getString("name"));
                        editor.putString("product", json.getString("product"));
                        editor.putString("zip", json.getString("zip"));
                        editor.putString("member_type", json.getString("member_type"));
                        editor.putString("expired_date", json.getString("expired_date"));
                        editor.putString("register_date", json.getString("register_date"));
                        editor.putString("billing_period", json.getString("billing_period"));
                        editor.putString("street", json.getString("street"));
                        editor.putString("street2", json.getString("street2"));
                        editor.putString("phone", json.getString("phone"));
                        editor.putString("latest_activation_date", json.getString("latest_activation_date"));
                        editor.putString("grace_period_date", json.getString("grace_period_date"));
                        editor.putString("autogenerate_patient_id", json.getString("autogenerate_patient_id"));
                        editor.putString("email", json.getString("email"));
                        editor.commit();
                        editor.apply();
                        SharedPrefsUtils.setBooleanPreference(appContext, "is_mysql", true);
                        SharedPrefsUtils.setStringPreference(appContext, "versi", json.getString("versi"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!e.getMessage().contains("cannot") && !e.getMessage().contains("JSONObject")) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                JApplication.getInstance().setEncryptLoginCompanyBySharedPreferences();
                runnable.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jArr, Global.tag_json_obj);
    }

    public static void get_company(Activity activity, Context appContext, Runnable runnable, ProgressDialog loading) {
        String url = Routes.url_get_company() + "/" + JApplication.getInstance().loginInformationModel.getCompanyId()
                + "?email=" + JApplication.getInstance().loginInformationModel.getUsername(); //tambah email

        StringRequest jArr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response); // Ubah response menjadi JSON array

                    if (jsonArray.length() > 0) {
                        JSONObject json = jsonArray.getJSONObject(0); // Ambil elemen pertama dari JSON array

                        SharedPreferences sharedPreferences = Global.getEncryptSharedPreference(JApplication.getInstance());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("id", json.getInt("id"));
                        editor.putInt("country_id", json.getInt("country_id"));
                        editor.putString("name", json.getString("name"));
                        editor.putString("product", json.getString("product"));
                        editor.putString("zip", json.getString("zip"));
                        editor.putString("member_type", json.getString("member_type"));
                        editor.putString("expired_date", json.getString("expired_date"));
                        editor.putString("register_date", json.getString("register_date"));
                        editor.putString("billing_period", json.getString("billing_period"));
                        editor.putString("street", json.getString("street"));
                        editor.putString("street2", json.getString("street2"));
                        editor.putString("phone", json.getString("phone"));
                        editor.putString("latest_activation_date", json.getString("latest_activation_date"));
                        editor.putString("grace_period_date", json.getString("grace_period_date"));
                        editor.putString("autogenerate_patient_id", json.getString("autogenerate_patient_id"));
                        editor.putString("email", json.getString("email"));
                        editor.commit();
                        editor.apply();
                        SharedPrefsUtils.setBooleanPreference(appContext, "is_mysql", true);
                        SharedPrefsUtils.setStringPreference(appContext, "versi", json.getString("versi"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!e.getMessage().contains("cannot") && !e.getMessage().contains("JSONObject")) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }

                JApplication.getInstance().setEncryptLoginCompanyBySharedPreferences();
                runnable.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jArr, Global.tag_json_obj);
    }


    public static void editProfile(Activity activity, Context appContext, LoginCompanyModel loginCompanyModel, Runnable runnable, ProgressDialog loading) {
        //fff
        if (Global.CheckConnectionInternet(activity) && JApplication.getInstance().real_url.isEmpty()) {
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    editProfile(activity, appContext, loginCompanyModel, runnable, loading);
                }
            };
            GetIPAddress.get_ip_address(activity, runSuccess);
            return;
        }

        String url = Routes.url_update_company();
        loading.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {

                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else{
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", loginCompanyModel.getEmail());
                    body.put("name", loginCompanyModel.getName());
                    body.put("street", loginCompanyModel.getStreet());
                    body.put("street2", loginCompanyModel.getStreet2());
                    body.put("phone", loginCompanyModel.getPhone());
                    body.put("zip", loginCompanyModel.getZip());
                    body.put("id", JApplication.getInstance().loginInformationModel.getCompanyId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
//                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void SignUp(Activity activity, Context appContext, String userName, String email, String password, Runnable runnable, Runnable loading, Runnable dismiss) {
        //validation
        if (userName.equals("") || email.equals("") || password.equals("") ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.length() < 6){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }
        //sign up
        loading.run();
        String url = Routes.url_signup_new_member();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    String erMessage = "Invalid e-mail address. Please type in valid and active address.\nNone";
                    if (!erMessage.equals(jsonObjectData.getString("message"))) {
                        Snackbar.make(activity.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(activity.findViewById(android.R.id.content), R.string.error_invalid_email_type, Snackbar.LENGTH_LONG).show();
                    }
                    dismiss.run();
                }else{
                    GlobalMySQL.newLogin(activity, appContext, email, password, runnable);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Snackbar.make(activity.findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG).show();
                dismiss.run();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            dismiss.run();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("name", userName);
                    body.put("email", email);
                    body.put("password", password);

                    //sekarang untuk subsription, metodenya pake trial pada new member
                    body.put("latest_activation_date", Global.serverNowFormated());
                    body.put("is_trial", JConst.TRUE_STRING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+
                        body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void renewSubscription(Activity activity, Context appContext, LoginCompanyModel loginCompanyModel, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/api/res.company";

        ProgressDialog loading = new ProgressDialog(activity);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {

                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else{
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    JSONArray itemArray=new JSONArray("[[\"id\",\"=\",\""+loginCompanyModel.getId()+"\"]]");

                    body.put("filter", itemArray);
                    final JSONObject jsonData = new JSONObject();
                    jsonData.put("is_trial", loginCompanyModel.getIs_trial());
                    jsonData.put("billing_period", loginCompanyModel.getBilling_period());
                    jsonData.put("latest_activation_date", loginCompanyModel.getLatest_activation_date());

//                    String stringJson = jsonData.toString();
                    body.put("data", jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = body.toString();
                strbody = strbody.replace("\"{", "{");
                strbody = strbody.replace("}\"", "}");
                strbody = "{\"params\": "+
                        strbody+"}";
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public static void newRenewSubscription(Activity activity, LoginCompanyModel loginCompanyModel, Runnable runnable) {
        String url = Routes.url_renew_membership();
        ProgressDialog loading = new ProgressDialog(activity);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setMessage("Loading...");
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    //simpan untuk di kirim ulang
                    JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url, body) values ('"+url+"', '"+ JApplication.renewSubsBody +"')");

                }else{
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
            Global.showErrorMessage(activity, error.toString());
            loading.dismiss();
            //simpan untuk di kirim ulang
            JApplication.getInstance().db.execSQL("INSERT INTO kirim_ulang (url, body) values ('"+url+"', '"+ JApplication.renewSubsBody +"')");
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("id", loginCompanyModel.getId());
                    body.put("subscription_date", loginCompanyModel.getSubsription_date());
                    body.put("billing_period", loginCompanyModel.getBilling_period());
                    body.put("email", loginCompanyModel.getEmail());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+
                        body.toString()+"}";
                JApplication.renewSubsBody = strbody;
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void newRenewSubscriptionNoLoading(Activity activity, LoginCompanyModel loginCompanyModel, Runnable runnable) {
        String url = Routes.url_renew_membership();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }else{
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Global.showErrorMessage(activity, error.toString());
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("id", loginCompanyModel.getId());
                    body.put("subscription_date", loginCompanyModel.getSubsription_date());
                    body.put("billing_period", loginCompanyModel.getBilling_period());
                    body.put("email", loginCompanyModel.getEmail());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+
                        body.toString()+"}";
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void newLogin2(Activity activity, Context appContext, String user, String pass, Runnable runnable, Runnable loading, Runnable dismiss) {
        //validation
        if (user.equals("") || pass.equals("") || !Patterns.EMAIL_ADDRESS.matcher(user).matches() || pass.length() < 6){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }

        //fff
        if (Global.CheckConnectionInternet(activity) && JApplication.getInstance().real_url.isEmpty()) {
            Runnable runSuccess = new Runnable() {
                @Override
                public void run() {
                    newLogin2(activity, appContext, user, pass, runnable, loading, dismiss);
                }
            };
            GetIPAddress.get_ip_address(activity, runSuccess);
            return;
        }

        //fungsi
        loading.run();
        String url = Routes.url_login2();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){

                    JSONObject jsonObject = new JSONObject(response);
                    String errorMessage = jsonObject.getString("error");
                    if (errorMessage.contains("invalid user")){
                        //erik tutup odoo 040325
//                        GlobalOdoo.newLogin2(activity, appContext, user, pass, runnable, loading, dismiss);
                        Snackbar.make(activity.findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
                        dismiss.run();
                    }else {
                        Snackbar.make(activity.findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
                        dismiss.run();
                    }
                }else {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
                    JApplication.getInstance().mySQLConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, pass);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismiss.run();
            }
        }, error -> {
            Global.showErrorMessage(appContext, error.toString());
            dismiss.run();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", user);
                    body.put("password", pass);
                    body.put("db", JConst.DB_SERVER_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void resetPin(Activity activity, int newPin, Runnable runnable) {
        if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }

        String url = Routes.url_forget_pin();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }else{
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Global.showErrorMessage(activity, error.toString());
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                    body.put("new_pin", newPin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": " +
                        body.toString() + "}";
                return strbody.getBytes();
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
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void getStatusSubsription(Activity activity, Context context, boolean isRefresh){
        /**Random random = new Random();  ditutup karena meresahkan,, jadikan ngecek cuman sehari sekali
        int r = random.nextInt(10);
        int x = 7;

        if (r > x){
        */
            if (Global.CheckConnectionInternet(activity)){
                ProgressDialog loading = Global.createProgresSpinner(activity, context.getString(R.string.sync_progress));
                loading.show();
//                Runnable runCompany = new Runnable() {
//                    @Override
//                    public void run() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Runnable runnable2 = new Runnable() {
                            @Override
                            public void run() {
                                /**jalankan kalau udh selesai proses */
                                loading.dismiss();
                                if (isRefresh) {
                                    activity.startActivity(new Intent(activity, home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                                /**simpen ke pref bahwa tanggal sekarang udah di cek */
//                                        SharedPrefsUtils.setLongPreference(context, "last_cek_subscription", Global.serverNowWithoutTimeLong());
                            }
                        };
                        UpdateLatestActivity(activity, context, Global.serverNowFormatedWithTime(), runnable2, loading);
                    }
                };
                get_company(activity, context, runnable, loading);   //ambil semua data akun
//                    }
//                };
//                auth(activity, context, runCompany, loading);   //bypass login
            }
//        }
    }

    private static String object_to_string(String string){
        if (string.equals("false")){
            return "";
        }else{
            return string;
        }
    }

    public static void auth(Activity activity, Context appContext, Runnable runnable, ProgressDialog loading) {
//        ProgressDialog loading = Global.createProgresSpinner(activity);
        loading.show();

        String url = Routes.url_login2();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    if (jsonObjectData.getString("message").equals("Access denied") || jsonObjectData.getString("message").equals("Session expired")){
                        //reroute ke login aktivity
                        loading.dismiss();
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                JApplication.getInstance().clearSharedPreperence();  //clear session
                                SharedPrefsUtils.setBooleanPreference(appContext, "force_logout", true);
                                SharedPrefsUtils.setStringPreference(appContext, "last_user", JApplication.getInstance().loginInformationModel.getUsername());

                                appContext.startActivity(new Intent(appContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        };
                        ShowDialog.infoDialogWithRunnable(activity, appContext.getString(R.string.app_name), activity.getString(R.string.inform_session_expired), run);
                    }else {
                        Snackbar.make(activity.findViewById(android.R.id.content), jsonObjectData.getString("message"), Snackbar.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                }else {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
                    JApplication.getInstance().mySQLConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, JApplication.getInstance().loginInformationModel.getPassword());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
//            Global.showErrorMessage(appContext, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", JApplication.getInstance().loginInformationModel.getUsername());
                    body.put("password", JApplication.getInstance().loginInformationModel.getPassword());
                    body.put("db", JConst.DB_SERVER_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void UpdateLatestActivity(Activity activity, Context appContext, String LatestActivity ,Runnable runnable, ProgressDialog loading) {
        String url = Routes.url_update_company();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {

                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();

                    loading.dismiss();
                }else{
                    JSONObject jsonObject = new JSONObject(response);
                    // Mendapatkan nilai "success" dari objek JSONObject
                    String successValue = jsonObject.getString("success");
                    // Konversi nilai "success" ke dalam tipe data Long
                    long successLongValue = Long.parseLong(successValue);
                    SharedPrefsUtils.setLongPreference(appContext, "last_cek_subscription", successLongValue);
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();

            }
        }, error -> {
//            Global.showErrorMessage(appContext, error.toString());
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("x_latest_activity",LatestActivity);
                    body.put("versi",JApplication.getInstance().getVersionName());
                    body.put("id", JApplication.getInstance().loginInformationModel.getCompanyId());
                    body.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
//                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public static void check_login_odoo(Context appContext, Runnable runnable, ProgressDialog loading) {
        loading.show();

        String url = Routes.url_login2();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    loading.dismiss();
                }else {
                    check_login_mysql(appContext, runnable, loading);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("login", JApplication.getInstance().loginInformationModel.getUsername());
                    body.put("password", JApplication.getInstance().loginInformationModel.getPassword());
                    body.put("db", JConst.DB_SERVER_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void check_login_mysql(Context appContext, Runnable runnable, ProgressDialog loading) {
        loading.show();

        String url = Routes.url_login2();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    loading.dismiss();
                }else {
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", JApplication.getInstance().loginInformationModel.getUsername());
                    body.put("password", JApplication.getInstance().loginInformationModel.getPassword());
                    body.put("db", JConst.DB_SERVER_NAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


    public static void migrasi_odoo_to_mysql(Activity context, String newPassword, ProgressDialog loading) {
        String url = Routes.url_migrasi_data_odoo();

        loading.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    loading.dismiss();
                }else{
                    SharedPrefsUtils.setBooleanPreference(context, "is_mysql", true);
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("result");

                    Runnable runDismiss = new Runnable() {
                        @Override
                        public void run() {
                            loading.dismiss();
                        }
                    };
                    Runnable runCompany = new Runnable() {
                        @Override
                        public void run() {
                            JApplication.getInstance().mySQLConnection.loadLoginInformation(context.getApplicationContext(), context, jsonObject, runDismiss, newPassword);
                        }
                    };

                    Global.infoDialogRun(context,"Migration was successful", "", runCompany);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            Global.showErrorMessage(context, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("id", JApplication.getInstance().loginInformationModel.getUid());
                    body.put("password", newPassword);
                    body.put("country_id", JApplication.getInstance().loginCompanyModel.getCountry_id());
                    body.put("name", JApplication.getInstance().loginCompanyModel.getName());
                    body.put("product", JApplication.getInstance().loginCompanyModel.getProduct());
                    body.put("zip", JApplication.getInstance().loginCompanyModel.getZip());
                    body.put("member_type", JApplication.getInstance().loginCompanyModel.getMember_type());
                    body.put("expired_date", JApplication.getInstance().loginCompanyModel.getExpired_date());
                    body.put("register_date", JApplication.getInstance().loginCompanyModel.getRegister_date());
                    body.put("billing_period", JApplication.getInstance().loginCompanyModel.getBilling_period());
                    body.put("street", JApplication.getInstance().loginCompanyModel.getStreet());
                    body.put("street2", JApplication.getInstance().loginCompanyModel.getStreet2());
                    body.put("phone", JApplication.getInstance().loginCompanyModel.getPhone());
                    body.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                    body.put("latest_activation_date", JApplication.getInstance().loginCompanyModel.getLatest_activation_date());
                    body.put("grace_period_date", JApplication.getInstance().loginCompanyModel.getGrace_period_date());
                    body.put("autogenerate_patient_id", JApplication.getInstance().loginCompanyModel.getAutogenerate_patient_id());
                    body.put("is_trial", JApplication.getInstance().loginCompanyModel.getIs_trial());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
//                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void check_email_mysql(Activity activity, String email, Runnable runnable) {
        ProgressDialog loading = Global.createProgresSpinner(activity, activity.getString(R.string.loading));
        loading.show();

        String url = Routes.url_cek_email();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    SharedPrefsUtils.setBooleanPreference(activity.getApplicationContext(), "is_mysql", false);
                    loading.dismiss();
                    runnable.run();
                }else {
                    SharedPrefsUtils.setBooleanPreference(activity.getApplicationContext(), "is_mysql", true);
                    loading.dismiss();
                    runnable.run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("email", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strbody = "{\"params\": "+body.toString()+"}";
                return strbody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }


}

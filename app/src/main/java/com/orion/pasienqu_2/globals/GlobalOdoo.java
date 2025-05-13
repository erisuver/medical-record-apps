package com.orion.pasienqu_2.globals;
//erik tutup odoo 040325
/*
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.activities.login.LoginActivity;
import com.orion.pasienqu_2.activities.login.SignUpActivity;
import com.orion.pasienqu_2.activities.more.change_password.ChangePasswordActivity;
import com.orion.pasienqu_2.activities.more.pin_protection.PinProtectScreenActivity;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;

public class GlobalOdoo {
    public static void newLogin(Activity activity, Context appContext, String user, String pass, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/auth";
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
                    JApplication.getInstance().odooConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, pass);
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
                    body.put("login", user);
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

        loading.show();
        String url = JConst.HOST_SERVER+"/object/res.users/change_password";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }else{
                    GlobalOdoo.newLogin(activity, appContext, JApplication.getInstance().loginInformationModel.getUsername(), newPass, runnable);
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
                    JSONArray itemArray=new JSONArray("[\""+oldPass+"\",\""+newPass+"\"]");

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

    public static void forgetPassword(Activity activity, Context appContext, String email, Runnable runnable, Runnable loading, Runnable dismiss) {
        //validation
        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }

        loading.run();
        String url = JConst.HOST_SERVER + "/web/forget_password";
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
            if (error instanceof TimeoutError) {
                // Handle timeout error here
                Snackbar.make(activity.findViewById(android.R.id.content), R.string.error_invalid_username, Snackbar.LENGTH_SHORT).show();
                dismiss.run();
            } else {
                // Handle other network-related errors here
                Global.showErrorMessage(appContext, error.toString());
                dismiss.run();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("login", email);
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
                    GlobalOdoo.newLogin(activity, appContext, JApplication.getInstance().loginInformationModel.getUsername(), JApplication.getInstance().loginInformationModel.getPassword(), runnable);
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
        String url = JConst.HOST_SERVER+"/api/res.company/"+JApplication.getInstance().loginInformationModel.getCompanyId();

        StringRequest jArr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (new JSONObject(response).has("name")){
                        try {
                            SharedPreferences sharedPreferences = Global.getEncryptSharedPreference(JApplication.getInstance());
//                            SharedPreferences sharedpreferences;
//                            sharedpreferences = JApplication.getInstance().getSharedPreferences("login_company", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            JSONObject json = new JSONObject(response);
//                            json.append("asas", null);
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
//                            editor.putString("trial_end_date", json.getString("trial_end_date"));
//                            editor.putString("purchase_token", object_to_string(json.getString("purchase_token")));
                            editor.putString("zip", object_to_string(json.getString("zip")));
                            editor.putString("street", object_to_string(json.getString("street")));
                            editor.putString("street2", object_to_string(json.getString("street2")));
                            editor.putString("phone", object_to_string(json.getString("phone")));

                            editor.commit();
                            editor.apply();
                            SharedPrefsUtils.setBooleanPreference(appContext, "is_mysql", false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (!e.getMessage().contains("cannot") && !e.getMessage().contains("JSONObject")){
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        JApplication.getInstance().setEncryptLoginCompanyBySharedPreferences();
                        runnable.run();
                    }else{


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String ErrorJava = "<DOCTYPE of java.lang.string ";
                    if (!e.getMessage().contains(ErrorJava)){
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    runnable.run();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
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
        String url = JConst.HOST_SERVER+"/api/res.company";

        loading.show();
        StringRequest strReq = new StringRequest(Request.Method.PUT, url, response -> {
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
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    JSONArray itemArray=new JSONArray("[[\"id\",\"=\",\""+loginCompanyModel.getId()+"\"]]");

                    body.put("filter", itemArray);
                    final JSONObject jsonData = new JSONObject();
                    jsonData.put("email", loginCompanyModel.getEmail());
                    jsonData.put("name", loginCompanyModel.getName());
                    jsonData.put("street", loginCompanyModel.getStreet());
                    jsonData.put("street2", loginCompanyModel.getStreet2());
                    jsonData.put("phone", loginCompanyModel.getPhone());
                    jsonData.put("zip", loginCompanyModel.getZip());

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
        String url = JConst.HOST_SERVER+"/web/signup_new_member";
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
                    GlobalOdoo.newLogin(activity, appContext, email, password, runnable);
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
//                    body.put("billing_period", "day30");
                    body.put("street", "");
                    body.put("street2", "");
                    body.put("phone", "");
                    body.put("zip", "");

                    //sekarang untuk subsription, metodenya pake trial pada new member
                    body.put("latest_activation_date", Global.serverNowFormated());
                    body.put("is_trial", true);

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
//                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
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

        StringRequest strReq = new StringRequest(Request.Method.PUT, url, response -> {
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
        String url = JConst.HOST_SERVER+"/object/res.company/"+loginCompanyModel.getId()+"/renew_membership";

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
            Global.showErrorMessage(activity, error.toString());
            loading.dismiss();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    final JSONObject jsonData = new JSONObject();
                    jsonData.put("subscription_date", loginCompanyModel.getSubsription_date());
                    jsonData.put("billing_period", loginCompanyModel.getBilling_period());
                    body.put("context", jsonData);

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

    public static void newRenewSubscriptionNoLoading(Activity activity, LoginCompanyModel loginCompanyModel, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/object/res.company/"+loginCompanyModel.getId()+"/renew_membership";

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
                    final JSONObject jsonData = new JSONObject();
                    jsonData.put("subscription_date", loginCompanyModel.getSubsription_date());
                    jsonData.put("billing_period", loginCompanyModel.getBilling_period());
                    body.put("context", jsonData);

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

    public static void newLogin2(Activity activity, Context appContext, String user, String pass, Runnable runnable, Runnable loading, Runnable dismiss) {
        //validation
        if (user.equals("") || pass.equals("") || !Patterns.EMAIL_ADDRESS.matcher(user).matches() || pass.length() < 6){
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.field_must_be_fill, Snackbar.LENGTH_LONG).show();
            return;
        }else if (!Global.CheckConnectionInternet(activity)) {
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
            return;
        }

        //fungsi
//        loading.run();  tutup karena dipanggil sesudah login mysql
        String url = JConst.HOST_SERVER+"/auth";
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
                    dismiss.run();
                }else {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
                    JApplication.getInstance().odooConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, pass);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismiss.run();
            }
        }, error -> {
            if (error instanceof TimeoutError) {
                // Handle timeout error here
                Snackbar.make(activity.findViewById(android.R.id.content), R.string.error_invalid_username, Snackbar.LENGTH_SHORT).show();
                dismiss.run();
            } else {
                // Handle other network-related errors here
                Global.showErrorMessage(appContext, error.toString());
                dismiss.run();
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("login", user);
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

        String url = JConst.HOST_SERVER+"/object/res.users/reset_pin";
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
                    JSONArray itemArray=new JSONArray("["+newPin+"]");
                    body.put("args", itemArray);
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

    public static void getStatusSubsription(Activity activity, Context context){
//        Random random = new Random();  ditutup karena meresahkan,, jadikan ngecek cuman sehari sekali
//        int r = random.nextInt(10);
//        int x = 7;
//
//        if (r > x){
//
            if (Global.CheckConnectionInternet(activity)){
//                ProgressDialog loading = new ProgressDialog(activity, R.style.MyGravity);
//                loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                loading.setCancelable(false);
                ProgressDialog loading = Global.createProgresSpinner(activity, context.getString(R.string.sync_progress));
                loading.show();
                Runnable runCompany = new Runnable() {
                    @Override
                    public void run() {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Runnable runnable2 = new Runnable() {
                                    @Override
                                    public void run() {
                                        //jalankan kalau udh selesai proses
                                        loading.dismiss();
                                        //simpen ke pref bahwa tanggal sekarang udah di cek
                                        SharedPrefsUtils.setLongPreference(context, "last_cek_subscription", Global.serverNowWithoutTimeLong());
                                    }
                                };
                                UpdateLatestActivity(activity, context, Global.serverNowFormatedWithTime(), runnable2, loading);
                            }
                        };
                        get_company(activity, context, runnable);   //ambil semua data akun
                    }
                };
                auth(activity, context, runCompany, loading);   //bypass login
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

        String url = JConst.HOST_SERVER+"/auth";
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
                    JApplication.getInstance().odooConnection.loadLoginInformation(appContext, activity, jsonObject, runnable, JApplication.getInstance().loginInformationModel.getPassword());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }, error -> {
            if (error instanceof TimeoutError) {
                // Handle timeout error here, odoo sudah di nonaktifkan
                SharedPrefsUtils.setBooleanPreference(appContext, "is_mysql", true);
                loading.dismiss();
            } else {
                // Handle other network-related errors here
                Global.showErrorMessage(appContext, error.toString());
                loading.dismiss();
            }
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

    public static void UpdateLatestActivity(Activity activity, Context appContext, String LatestActivity ,Runnable runnable, ProgressDialog loading) {
        String url = JConst.HOST_SERVER+"/api/res.company";
        StringRequest strReq = new StringRequest(Request.Method.PUT, url, response -> {
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
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    JSONArray itemArray=new JSONArray("[[\"id\",\"=\",\""+JApplication.getInstance().loginCompanyModel.getId()+"\"]]");

                    body.put("filter", itemArray);
                    final JSONObject jsonData = new JSONObject();

                    jsonData.put("x_latest_activity",LatestActivity);

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

    public static void get_session(String email, Activity activity, Context appContext, Runnable runnable) {
        ProgressDialog loading = Global.createProgresSpinner(activity, "");
        loading.show();

        String url = JConst.HOST_SERVER+"/auth";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    loading.dismiss();
                }else {
                    JSONObject obj = new JSONObject(response).getJSONObject("result");
//                    JApplication.getInstance().loginInformationModel.setCookie(obj.getString("session_id"));
                    check_email_odoo(email, activity, appContext, runnable, loading);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            if (error instanceof TimeoutError) {
                // Handle timeout error here
                loading.dismiss();
                //karena timeout/server sudah mati maka lanjut signup via mysql.
                runnable.run();
            } else {
                // Handle other network-related errors here
                Global.showErrorMessage(appContext, error.toString());
                loading.dismiss();
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    body.put("login", "pasienqu");
                    body.put("password", "admin54321*");
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

    public static void check_email_odoo(String email, Activity activity, Context appContext, Runnable runnable, ProgressDialog loading) {
        String url = JConst.HOST_SERVER+"/object/res.users/search_read";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                // Cek apakah properti "result" memiliki nilai null
                JSONObject responseObject = new JSONObject(response);
                String hasil = responseObject.getString("result");
                if (responseObject.isNull("result") || hasil.equals("[]")) {
                    loading.dismiss();
                    runnable.run();
                } else if (responseObject.has("error")){
                    loading.dismiss();
                }else{
                    // Lakukan sesuatu jika "result" tidak null
                    loading.dismiss();
                    Snackbar.make(activity.findViewById(android.R.id.content), "Email is already registered. please use another email.", Snackbar.LENGTH_SHORT).show();
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
                // Membuat JSON Object
                JSONObject params = new JSONObject();
                JSONObject kwargs = new JSONObject();

                // Membuat domain array dengan login
                JSONArray domain = new JSONArray();
                JSONArray loginCondition = new JSONArray();
                loginCondition.put("login");
                loginCondition.put("=");
                loginCondition.put(email);
                domain.put(loginCondition);

                // Menambahkan nilai domain dan fields ke dalam kwargs
                try {
                    kwargs.put("domain", domain);
                    kwargs.put("fields", new JSONArray().put("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Menambahkan kwargs ke dalam params
                try {
                    params.put("kwargs", kwargs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String strbody = "{\"params\": "+
                        params.toString()+"}";
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

}
*/

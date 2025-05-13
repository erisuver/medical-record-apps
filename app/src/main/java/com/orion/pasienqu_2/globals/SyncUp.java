package com.orion.pasienqu_2.globals;

import android.app.Activity;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.data_table.GenderTable;
import com.orion.pasienqu_2.data_table.SyncInfoTable;
import com.orion.pasienqu_2.data_table.WorkLocationTable;
import com.orion.pasienqu_2.data_table.more.NoteTemplateTable;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.SyncInfoModel;
import com.orion.pasienqu_2.models.WorkLocationModel;
import com.orion.pasienqu_2.models.more.NoteTemplateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

public class SyncUp {
    public static void sync_all(Activity activity, Context appContext, Runnable runnable){
        SyncInfoTable syncInfoTable = JApplication.getInstance().syncInfoTable;
        ArrayList<SyncInfoModel> arrayList = syncInfoTable.getRecords();
        if (arrayList.size() > 0){
            if (arrayList.get(0).getCommand().equals("create")) {
                post(activity, appContext, arrayList.get(0), runnable);
            }else if (arrayList.get(0).getCommand().equals("write")) {
                put(activity, appContext, arrayList.get(0), runnable);
            }else {
                delete(activity, appContext, arrayList.get(0), runnable);
            }
        }else{
            runnable.run();
        }
    }

    public static void post(Activity activity, Context appContext, SyncInfoModel syncInfoModel, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/api/"+syncInfoModel.getModel();
        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    if (activity != null) {
                        Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    SyncInfoTable syncInfoTable = JApplication.getInstance().syncInfoTable;
                    syncInfoTable.delete(syncInfoModel.getId());
                    ArrayList<SyncInfoModel> syncInfoModelArrayList = syncInfoTable.getRecords();
                    if (syncInfoModelArrayList.size() == 0){
                        runnable.run();
                    }else {
                        sync_all(activity, appContext, runnable);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    String value = new String(syncInfoModel.getContent());
                    JSONObject jsonData = new JSONObject(value);
                    if (jsonData.has("date_of_birth")){
                        Long date = jsonData.getLong("date_of_birth");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("date_of_birth", dateString);
                    }
                    if (jsonData.has("register_date")){
                        Long date = jsonData.getLong("register_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("register_date", dateString);
                    }
                    if (jsonData.has("record_date")){
                        Long date = jsonData.getLong("record_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("record_date", dateString);
                    }


                    //untuk subacount mode
                    if (syncInfoModel.getModel().equals("res.users")){
                        JSONObject jsonContext = new JSONObject();
                        jsonContext.put("mode", "subuser");
                        body.put("context", jsonContext);
                    }
                    if (jsonData.has("appointment_date")){
                        Long date = jsonData.getLong("appointment_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("appointment_date", dateString);
                    }
                    if (jsonData.has("billing_date")){
                        Long date = jsonData.getLong("billing_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("billing_date", dateString);
                    }
//                  nanti di hapusssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
                    if (jsonData.has("billing_ids")){
                        jsonData.put("billing_ids", "[]");
                    }
                    if (jsonData.has("file_ids")){
                        jsonData.put("file_ids", "[]");
                    }

                    body.put("data", jsonData);
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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void put(Activity activity, Context appContext, SyncInfoModel syncInfoModel, Runnable runnable) {
        String url = JConst.HOST_SERVER+"/api/"+syncInfoModel.getModel();
        StringRequest strReq = new StringRequest(Request.Method.PUT, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
//                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    if (activity != null) {
                        Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    SyncInfoTable syncInfoTable = JApplication.getInstance().syncInfoTable;
                    syncInfoTable.delete(syncInfoModel.getId());
                    ArrayList<SyncInfoModel> syncInfoModelArrayList = syncInfoTable.getRecords();
                    if (syncInfoModelArrayList.size() == 0){
                        runnable.run();
                    }else {
                        sync_all(activity, appContext, runnable);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                final JSONObject body = new JSONObject();
                try {
                    String value = new String(syncInfoModel.getContent());
                    JSONObject jsonData = new JSONObject(value);
                    if (jsonData.has("date_of_birth")){
                        Long date = jsonData.getLong("date_of_birth");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("date_of_birth", dateString);
                    }
                    if (jsonData.has("register_date")){
                        Long date = jsonData.getLong("register_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("register_date", dateString);
                    }
                    if (jsonData.has("record_date")){
                        Long date = jsonData.getLong("record_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("record_date", dateString);
                    }
                    //untuk subacount mode
                    if (syncInfoModel.getModel().equals("res.users")){
                        JSONObject jsonContext = new JSONObject();
                        jsonContext.put("mode", "subuser");
                        body.put("context", jsonContext);
                    }
                    if (jsonData.has("appointment_date")){
                        Long date = jsonData.getLong("appointment_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("appointment_date", dateString);
                    }
                    if (jsonData.has("billing_date")){
                        Long date = jsonData.getLong("billing_date");
                        String dateString = Global.getDateFormatedOdoo(date);
                        jsonData.put("billing_date", dateString);
                    }
                    JSONArray itemArray=new JSONArray("[[\"uuid\", \"=\", \""+syncInfoModel.getUuidModel()+"\"]]");
//                    String filter = "[[\"uuid\",\"=\","+syncInfoModel.getUuidModel()+"]]";
                    body.put("filter", itemArray);
                    body.put("data", jsonData);
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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                JApplication.getInstance().checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        JApplication.getInstance().addToRequestQueue(strReq, Global.tag_json_obj);
    }

    public static void delete(Activity activity, Context appContext, SyncInfoModel syncInfoModel, Runnable runnable) {
        String filter = "?filter=[[\"uuid\",\"=\", \""+syncInfoModel.getUuidModel()+"\"]]";
        String url = JConst.HOST_SERVER+"/api/"+syncInfoModel.getModel()+filter;
        StringRequest strReq = new StringRequest(Request.Method.DELETE, url, response -> {
            try {
                if (new JSONObject(response).has("error")){
                    JSONObject jsonObject = new JSONObject(response).getJSONObject("error");
                    JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                    Toast.makeText(activity, jsonObjectData.getString("message"), Toast.LENGTH_SHORT).show();
                }else {
                    SyncInfoTable syncInfoTable = JApplication.getInstance().syncInfoTable;
                    syncInfoTable.delete(syncInfoModel.getId());
                    ArrayList<SyncInfoModel> syncInfoModelArrayList = syncInfoTable.getRecords();
                    if (syncInfoModelArrayList.size() == 0){
                        runnable.run();
                    }else {
                        sync_all(activity, appContext, runnable);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
        })
        {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type","application/json");
                headers.put("Cookie", JApplication.getInstance().loginInformationModel.getCookie());
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

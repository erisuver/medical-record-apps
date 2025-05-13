package com.orion.pasienqu_2.activities.more.kirim_database;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.activities.more.data_migration.DataMigrationActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.globals.VolleyMultipartRequest;
import com.orion.pasienqu_2.globals.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KirimDatabaseActivity extends AppCompatActivity {
    private AppCompatActivity thisActivity;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_database);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Kirim Data");
        thisActivity = this;
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        btnUpload = findViewById(R.id.btnUpload);
        
    }

    private void InitClass() {

    }

    private void EventClass() {
        btnUpload.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(thisActivity)) {
                ShowDialog.infoDialog(thisActivity, getString(R.string.information), getString(R.string.must_be_online));
                return;
            }

            Runnable runConfirm = new Runnable() {
                @Override
                public void run() {
                    kirimDatabase();
                }
            };
            ShowDialog.confirmDialog(thisActivity, String.valueOf(getTitle()), "Apakah Anda yakin ingin melakukan kirim data?", runConfirm);
        });
    }

    public void kirimDatabase(){
        ProgressDialog pDialog = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
        pDialog.setMessage("Uploading...");
        pDialog.show();
        String url = Routes.url_kirim_database();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    if (status.equals(JConst.STATUS_API_SUCCESS)) {
                        pDialog.dismiss();
                        ShowDialog.infoDialog(thisActivity, getString(R.string.information), "Kirim Data Sukses");
                    }else{
                        Toast.makeText(thisActivity, status, Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                    pDialog.dismiss();
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //masukan ke param
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", JApplication.getInstance().loginCompanyModel.getEmail());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                //upload DML
                String fieldName = "db";
                DataPart dataPart = new DataPart("pasienqu.db", getFileDataFromPath(JApplication.getInstance().lokasi_db), "application/x-sqlite3");
                params.put(fieldName, dataPart);

                return params;
            }
        };
        int timeout = 5*60*1000; // 5 menit
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private byte[] getFileDataFromPath(String path) {
        File file = new File(path);
        byte[] data = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
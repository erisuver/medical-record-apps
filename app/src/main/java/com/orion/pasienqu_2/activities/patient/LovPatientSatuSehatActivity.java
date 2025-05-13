package com.orion.pasienqu_2.activities.patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.RoutesSatuSehat;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalSatuSehat;
import com.orion.pasienqu_2.models.GenderModel;
import com.orion.pasienqu_2.models.LovPatientSatuSehatModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LovPatientSatuSehatActivity extends CustomAppCompatActivity {
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    private TextInputEditText txtName, txtDateBirth;
    private TextInputLayout errName, errDateBirth, errGender;
    private AutoCompleteTextView spinGender;
    private MaterialButton btnSearch;
    private AppCompatButton btnPilih;

    public LovPatientSatuSehatAdapter mAdapter;
    public List<LovPatientSatuSehatModel> ListItems = new ArrayList<>();
    private List<String>listStringGender;
    private ArrayList<GenderModel> listGender;
    private int genderId;
    private String strName, strDateBirth, strGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lov_patient_satu_sehat);
        JApplication.currentActivity = this; //set awal currenactivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cari Pasien Satu Sehat");
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        thisActivity = LovPatientSatuSehatActivity.this;
        rcvLoad = findViewById(R.id.rcvLoad);
        txtName = findViewById(R.id.txtName);
        txtDateBirth = findViewById(R.id.txtDateBirth);
        errName = findViewById(R.id.layoutName);
        errDateBirth = findViewById(R.id.layoutDateBirth);
        errGender = findViewById(R.id.layoutGender);
        spinGender = findViewById(R.id.spinGender);
        btnSearch = findViewById(R.id.btnSearch);
        btnPilih = findViewById(R.id.btnPilih);

        this.mAdapter = new LovPatientSatuSehatAdapter(this, ListItems, R.layout.lov_patient_satu_sehat_list_item);
        rcvLoad.setLayoutManager(new GridLayoutManager(thisActivity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);

    }

    private void InitClass(){
        genderId = 0;
        isiSpinner();

        Bundle extra = this.getIntent().getExtras();
        if (extra != null) {
            strName = extra.getString("name");
            strDateBirth = extra.getString("date_birth");
            genderId = extra.getInt("gender");

            txtName.setText(strName);
            txtDateBirth.setText(strDateBirth);

            if (genderId > 0) {
                for (int i = 0; i < listGender.size(); i++) {
                    if (listGender.get(i).getId() == genderId) {
                        spinGender.setText(listStringGender.get(i), false);
                    }
                }
            }
        }
    }

    private void EventClass(){
        txtDateBirth.setOnClickListener(view -> {
                Global.datePickerClick(thisActivity, txtDateBirth, view);
        });

        spinGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinGender.showDropDown();
            }
        });

        spinGender.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);

                for (int i = 0; i < listStringGender.toArray().length; i++) {
                    if (listStringGender.get(i).equals(selection)) {
                        genderId = i+1;
                        break;
                    }
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    GlobalSatuSehat.checkTokenSatuSehat(thisActivity, () -> loadData());
                }
            }
        });

        btnPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.Datas.size() == 0 || mAdapter.selectedPosition == RecyclerView.NO_POSITION){
                    Global.infoDialog(thisActivity, "", "Tidak ada data yang dipilih.");
                    return;
                }
                LovPatientSatuSehatModel itemSelected = mAdapter.Datas.get(mAdapter.selectedPosition);
                Intent intent = LovPatientSatuSehatActivity.this.getIntent();
                intent.putExtra("patient_ihs_number", itemSelected.getPatient_ihs_number());
                intent.putExtra("name", itemSelected.getName());
                intent.putExtra("birth_date", txtDateBirth.getText().toString());
                intent.putExtra("gender", genderId);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void loadData(){
        ProgressDialog loading = Global.createProgresSpinner(thisActivity, getString(R.string.loading));
        loading.show();

        strName = txtName.getText().toString().trim();
        strDateBirth = Global.getDateFormated(Global.getMillisDate(txtDateBirth.getText().toString()), "yyyy-MM-dd");
        strGender = spinGender.getText().toString();

        String params = "?name="+strName+
                "&birthdate="+strDateBirth+
                "&gender="+strGender;
        String url = RoutesSatuSehat.url_patient + params;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                mAdapter.removeAllModel();
                ArrayList<LovPatientSatuSehatModel> itemDatas = new ArrayList<>();

                JSONArray entryArray = response.getJSONArray("entry");
                for (int i = 0; i < entryArray.length(); i++) {
                    JSONObject resourceObject = entryArray.getJSONObject(i).getJSONObject("resource");

                    // Mendapatkan nilai 'name', 'birthDate', 'city', 'id' dari resourceObject
                    String name = resourceObject.getJSONArray("name").getJSONObject(0).getString("text");
                    String birthDate = resourceObject.getString("birthDate");
                    String city = "";
                    if (resourceObject.has("address")) {
                        JSONObject addressObject = resourceObject.getJSONArray("address").getJSONObject(0);
                        if (addressObject.has("city")) {
                            city = addressObject.getString("city");
                        }
                    }
                    String id = resourceObject.getString("id");
                    /*if (resourceObject.has("telecom")) {
                        String telp = resourceObject.getJSONArray("telecom").getJSONObject(0).getString("value");
                    }*/

                    LovPatientSatuSehatModel data = new LovPatientSatuSehatModel(name, birthDate, city, id);
                    itemDatas.add(data);
                }

                //cek jika tidak ditemukan
                if (itemDatas.size() == 0){
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Data tidak ditemukan.", Snackbar.LENGTH_LONG).show();
                }else {
                    mAdapter.addModels(itemDatas);
                    mAdapter.notifyDataSetChanged();
                }
                //dismiss progress dialog
                loading.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }, error -> {
            // Tangani kesalahan jaringan atau kegagalan permintaan
            loading.dismiss();
            if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")){
                GlobalSatuSehat.showMessage("Tidak bisa terhubung ke Satu Sehat, silahkan coba beberapa saat lagi.");
                return;
            }
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                GlobalSatuSehat.handleErrorResponse(networkResponse.statusCode);
            } else {
                GlobalSatuSehat.showMessage(GlobalSatuSehat.INTERNAL_SERVER_ERROR);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + JApplication.getInstance().globalTable.getTokenSatuSehat());
                return headers;
            }
        };
        JApplication.getInstance().addToRequestQueue(jsonObjectRequest, Global.tag_json_obj);
    }

    private void isiSpinner(){
        //isi gender
        listGender = JApplication.getInstance().genderTable.getRecords();
        listStringGender = new ArrayList<>();
        for (int i = 0; i < listGender.size(); i++){
            listStringGender.add(listGender.get(i).getName());
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listStringGender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGender.setAdapter(genderAdapter);
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(txtName.getText().toString().trim())) {
            errName.setError(getString(R.string.error_first_name));
            isValid = false;
        }else{
            errName.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(spinGender.getText().toString().trim())) {
            errGender.setError(getString(R.string.error_gender));
            isValid = false;
        }else{
            errGender.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtDateBirth.getText())) {
            errDateBirth.setError(getString(R.string.error_date_birth));
            isValid = false;
        }else{
            errDateBirth.setErrorEnabled(false);
        }
        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

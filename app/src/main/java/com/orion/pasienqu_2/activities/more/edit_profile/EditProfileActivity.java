package com.orion.pasienqu_2.activities.more.edit_profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.models.LoginCompanyModel;
import com.orion.pasienqu_2.models.LoginInformationModel;

public class EditProfileActivity extends CustomAppCompatActivity {
    private TextInputEditText txtEmail, txtName, txtAddressLine1, txtAddressLine2, txtContactNo, txtZip;
    private TextInputLayout errName;
    private Button btnSave, btnCancel;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.edit_profile));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        txtAddressLine1 = (TextInputEditText) findViewById(R.id.txtAddressLine1);
        txtAddressLine2 = (TextInputEditText) findViewById(R.id.txtAddressLine2);
        txtContactNo = (TextInputEditText) findViewById(R.id.txtContact);
        txtZip = (TextInputEditText) findViewById(R.id.txtZip);

        errName = (TextInputLayout) findViewById(R.id.layoutName);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    private void InitClass(){
        Global.setEnabledClickText(txtEmail, false);
        LoginCompanyModel loginCompanyModel = JApplication.getInstance().loginCompanyModel;

        id = loginCompanyModel.getId();
        txtEmail.setText(loginCompanyModel.getEmail());
        txtName.setText(loginCompanyModel.getName());
        txtAddressLine1.setText(loginCompanyModel.getStreet());
        txtAddressLine2.setText(loginCompanyModel.getStreet2());
        txtContactNo.setText(loginCompanyModel.getPhone());
        txtZip.setText(loginCompanyModel.getZip());
    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SaveForm()) {
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private boolean IsValid(){
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
            return false;
        }else{
            errName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean SaveForm(){
        if (this.IsValid()) {
            LoginCompanyModel loginCompanyModel = new LoginCompanyModel();
            LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
            loginCompanyModel.setId(id);
            loginCompanyModel.setEmail(txtEmail.getText().toString());
            loginCompanyModel.setName(txtName.getText().toString().trim());
            loginInformationModel.setName(txtName.getText().toString().trim());
            loginCompanyModel.setPhone(txtContactNo.getText().toString());
            loginCompanyModel.setStreet(txtAddressLine1.getText().toString().trim());
            loginCompanyModel.setStreet2(txtAddressLine2.getText().toString().trim());
            loginCompanyModel.setZip(txtZip.getText().toString());

            ProgressDialog loading = Global.createProgresSpinner(EditProfileActivity.this, getString(R.string.loading));

//            Runnable runAuth = new Runnable() {
//                @Override
//                public void run() {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Runnable runnable1 = new Runnable() {
                                @Override
                                public void run() {
                                    setResult(RESULT_OK);
//                                    finish();
                                    loading.dismiss();
                                    onBackPressed();
                                }
                            };
//                            GlobalOdoo.get_company(EditProfileActivity.this, getApplicationContext(), runnable1);
                            GlobalMySQL.get_company(EditProfileActivity.this, getApplicationContext(), runnable1, loading);
                        }
                    };
//                    GlobalOdoo.editProfile(EditProfileActivity.this, getApplicationContext(), loginCompanyModel, runnable, loading);
                    GlobalMySQL.editProfile(EditProfileActivity.this, getApplicationContext(), loginCompanyModel, runnable, loading);
//                }
//            };
//            GlobalOdoo.auth(EditProfileActivity.this, getApplicationContext(), runAuth, loading);
//            GlobalMySQL.auth(EditProfileActivity.this, getApplicationContext(), runAuth, loading);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
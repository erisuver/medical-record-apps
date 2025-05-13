package com.orion.pasienqu_2.activities.more.change_password;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;

public class ChangePasswordActivity extends CustomAppCompatActivity {
    private TextInputEditText txtOldPassword, txtNewPassword, txtConfirmPassword;
    private TextInputLayout errOldPassword, errNewPassword, errConfirmPassword;
    private Button btnSubmit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.change_password));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        txtOldPassword = (TextInputEditText) findViewById(R.id.txtOldPassword);
        txtNewPassword = (TextInputEditText) findViewById(R.id.txtNewPassword);
        txtConfirmPassword = (TextInputEditText) findViewById(R.id.txtConfirmPassword);
        errOldPassword = (TextInputLayout) findViewById(R.id.layoutOldPassword);
        errNewPassword = (TextInputLayout) findViewById(R.id.layoutNewPassword);
        errConfirmPassword = (TextInputLayout) findViewById(R.id.layoutConfirmPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    private void InitClass(){

    }

    private void EventClass(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
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

    private void LoadData(){

    }

    private boolean IsValid(){
        if (TextUtils.isEmpty(txtOldPassword.getText())) {
            errOldPassword.setError(getString(R.string.error_old_password));
        }else if (txtOldPassword.getText().toString().length() < 6 ) {
            errOldPassword.setError(String.format(getString(R.string.min_length), getString(R.string.old_password)));
        }else{
            errOldPassword.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(txtNewPassword.getText())) {
            errNewPassword.setError(getString(R.string.error_new_password));
        }else if (txtNewPassword.getText().toString().length() < 6 ) {
            errNewPassword.setError(String.format(getString(R.string.min_length), getString(R.string.new_password)));
        }else{
            errNewPassword.setErrorEnabled(false);
        }

        //kasih return false karena saat saveform ga ada parameter confirm pass
        if (TextUtils.isEmpty(txtConfirmPassword.getText())) {
            errConfirmPassword.setError(getString(R.string.error_confirm_password));
        }else if (txtConfirmPassword.getText().toString().length() < 6 ) {
            errConfirmPassword.setError(String.format(getString(R.string.min_length), getString(R.string.confirm_pass)));
        }else{
            errConfirmPassword.setErrorEnabled(false);
        }

//        if (!txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())){
//            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.change_pwd_error, Snackbar.LENGTH_LONG).show();
//        }
//
//        if (!txtOldPassword.getText().toString().equals(JApplication.getInstance().loginInformationModel.getPassword())){
//            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.change_pwd_old_pass_error, Snackbar.LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }

    private boolean SaveForm(){
        if (this.IsValid()) {
            ProgressDialog loading = Global.createProgresSpinner(ChangePasswordActivity.this, getString(R.string.loading));

//            Runnable runAuth = new Runnable() {
//                @Override
//                public void run() {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                            loading.dismiss();
                        }
                    };
//                    GlobalOdoo.changePassword(ChangePasswordActivity.this, getApplicationContext(), txtOldPassword.getText().toString(), txtNewPassword.getText().toString(), txtConfirmPassword.getText().toString(), runnable, loading);
                    GlobalMySQL.changePassword(ChangePasswordActivity.this, getApplicationContext(), txtOldPassword.getText().toString(), txtNewPassword.getText().toString(), txtConfirmPassword.getText().toString(), runnable, loading);
//                }
//            };
//            GlobalOdoo.auth(ChangePasswordActivity.this, getApplicationContext(), runAuth, loading);
//            GlobalMySQL.auth(ChangePasswordActivity.this, getApplicationContext(), runAuth, loading);
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
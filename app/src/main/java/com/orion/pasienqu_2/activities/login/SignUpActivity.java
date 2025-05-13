package com.orion.pasienqu_2.activities.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.api.services.drive.DriveScopes;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.utility.GetIPAddress;


public class SignUpActivity extends CustomAppCompatActivity {
    private TextInputEditText txtName, txtEmail, txtPassword, txtConfirmPass;
    private TextInputLayout errName, errEmail, errPassword, errConfirmPass;
    private Button btnSignUp, btnCancel;
    private CheckBox chbAgree;
    private ProgressBar pgLoading;
    private View mainLayout;
    private ImageButton btnGoogle;
    private int REQUEST_CODE_SIGN_IN = 111;
    private GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        CreateView();
        EventClass();
        InitClass();
    }

    private void CreateView() {
        txtName = (TextInputEditText) findViewById(R.id.txtName);
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        txtConfirmPass = (TextInputEditText) findViewById(R.id.txtConfirm);
        errName = (TextInputLayout) findViewById(R.id.layoutName);
        errEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        errPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        errConfirmPass = (TextInputLayout) findViewById(R.id.layoutConfirm);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        chbAgree = (CheckBox) findViewById(R.id.chbAgreement);

        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        mainLayout = (View) findViewById(R.id.main_layout);
        btnGoogle = (ImageButton) findViewById(R.id.btnGoogle);
    }


    private void InitClass() {
        Global.setEnabledClickText(txtEmail, false);
    }

    private void EventClass() {
        chbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!chbAgree.isChecked()){
                    btnSignUp.setEnabled(false);
                    btnSignUp.setBackgroundColor(Color.parseColor("#E0E0E0"));
                }else{
                    btnSignUp.setEnabled(true);
                    btnSignUp.setBackgroundColor(Color.parseColor("#FF005EB8"));
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.hideSoftKeyboard(SignUpActivity.this);
                if (isValid()) {

                    //fff
                    if (Global.CheckConnectionInternet(SignUpActivity.this) && JApplication.getInstance().real_url.isEmpty()) {
                        Runnable runSuccess = new Runnable() {
                            @Override
                            public void run() {
                                signUp();
                            }
                        };
                        GetIPAddress.get_ip_address(SignUpActivity.this, runSuccess);
                        return;
                    }
                    signUp();
                }
            }
        });

        btnGoogle.setOnClickListener(view -> {
            signInGoogle();
        });

        txtEmail.setOnClickListener(view -> {
            signInGoogle();
        });
    }

    private boolean isValid(){
        //name
        if (TextUtils.isEmpty(txtName.getText())) {
            errName.setError(getString(R.string.error_name));
        }else{
            errName.setErrorEnabled(false);
        }

        //email
        if (TextUtils.isEmpty(txtEmail.getText())) {
            errEmail.setError(getString(R.string.error_email));
        }else if(!TextUtils.isEmpty(txtEmail.getText()) && !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
            errEmail.setError(getString(R.string.error_invalid_email));
        }else{
            errEmail.setErrorEnabled(false);
        }

        //password
        if (TextUtils.isEmpty(txtPassword.getText())) {
            errPassword.setError(getString(R.string.error_password_req));
        }else if (txtPassword.getText().toString().length() < 6 ) {
            errPassword.setError(getString(R.string.error_password));
        }else{
            errPassword.setErrorEnabled(false);
        }

        //confirm pas
        if (TextUtils.isEmpty(txtConfirmPass.getText())) {
            errConfirmPass.setError(getString(R.string.error_confirm_password));
        }else if (txtConfirmPass.getText().toString().length() < 6 ) {
            errConfirmPass.setError(getString(R.string.error_confirm_password_length));
            return false;
        }else if (!txtConfirmPass.getText().toString().equals(txtPassword.getText().toString())){
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.sign_up_pwd_error, Snackbar.LENGTH_LONG).show();
            return false;
        }else{
            errConfirmPass.setErrorEnabled(false);
        }

        return true;
    }

    private void signUp(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                JApplication.getInstance().isLogOn = true;
                JApplication.getInstance().isAfterSignup = true;
                startActivity(new Intent(SignUpActivity.this, home.class));
                Runnable runDismis =  new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                };
            }
        };
        Runnable loading = new Runnable() {
            @Override
            public void run() {
                showLoading();
            }
        };
        Runnable dismiss = new Runnable() {
            @Override
            public void run() {
                dismissLoading();
            }
        };
//        GlobalOdoo.SignUp(SignUpActivity.this, getApplicationContext(), txtName.getText().toString().trim(), txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim(), runnable, loading, dismiss);
        Runnable runSignUp = new Runnable() {
            @Override
            public void run() {
                GlobalMySQL.SignUp(SignUpActivity.this, getApplicationContext(), txtName.getText().toString().trim(), txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim(), runnable, loading, dismiss);
            }
        };
        //erik tutup odoo 040325
//        GlobalOdoo.get_session(txtEmail.getText().toString().trim(), SignUpActivity.this, getApplicationContext(), runSignUp);
        runSignUp.run();
    }

    private void showLoading(){
        pgLoading.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    private void dismissLoading(){
        pgLoading.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void signInGoogle(){
        signOutGoogle();
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();

        client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void signOutGoogle(){
        try {
            client.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateUI(String name, String email){
        txtName.setText(name);
        txtEmail.setText(email);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN){
            if(resultCode == RESULT_OK){
                GoogleSignInAccount Account = GoogleSignIn.getLastSignedInAccount(this);
                String AccountName = Account.getDisplayName();
                String AccountEmail = Account.getEmail();
                updateUI(AccountName, AccountEmail);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }


}
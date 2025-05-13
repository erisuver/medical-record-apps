package com.orion.pasienqu_2.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.orion.pasienqu_2.globals.SharedPrefsUtils;


public class LoginActivity extends CustomAppCompatActivity {
    private TextInputEditText txtEmail, txtPassword;
    private TextInputLayout errEmail, errPassword;
    private TextView tvForgetPass;
    private Button btnSignIn, btnSignUp;
    private boolean isClose;
    private ProgressBar pgLoading;
    private ConstraintLayout mainLayout;
    private int REQUEST_CODE = 111;
    private int REQUEST_CODE_SIGN_IN = 222;
    private GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CreateView();
        EventClass();
    }

    private void CreateView() {
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        txtPassword = (TextInputEditText) findViewById(R.id.txtPassword);
        errEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        errPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        tvForgetPass = (TextView) findViewById(R.id.tvForgetPass);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        mainLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        isClose = true;
        Global.setEnabledClickText(txtEmail, false);
    }

    private void EventClass() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.hideSoftKeyboard(LoginActivity.this);
                if (isValid()) {
                    login();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), REQUEST_CODE);
            }
        });

        tvForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        txtEmail.setOnClickListener(view -> {
            if (!Global.CheckConnectionInternet(this)){
                Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
                return;
            }

            signInGoogle();
        });
    }

    private boolean isValid(){
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

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isClose){
            finishAffinity();
        }else {
            super.onBackPressed();
        }
    }


//    public void login(String email, String password){
//
//        isClose = false;
//        onBackPressed();
//        ((JApplication)getApplicationContext()).odooConnection.login(email, password, loginCallback);
//    }

//    AuthenticateListener loginCallback = new AuthenticateListener() {
//        @Override
//        public void onLoginSuccess(OdooUser user) {
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    isClose = false;
//                    JApplication.getInstance().isLogOn = true;
//                    onBackPressed();
//                }
//            };
//            ((JApplication)getApplicationContext()).odooConnection.setOdooUser(user);
//            ((JApplication)getApplicationContext()).isLogOn = true;
//            ((JApplication)getApplicationContext()).odooConnection.loadLoginInformation(getApplicationContext(), LoginActivity.this, user, txtEmail.getText().toString(), runnable);
//
//        }
//
//        @Override
//        public void onLoginFail(AuthError error) {
//            Toast.makeText(LoginActivity.this, getString(R.string.login_access_denied_error), Toast.LENGTH_SHORT).show();
//        }
//    };

    private void login(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isClose = true;
                JApplication.getInstance().isLogOn = true;
                startActivity(new Intent(getApplicationContext(), home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

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
        //cek jika login gara2 sesion expired yg (force logout)
        boolean isForceLogout = SharedPrefsUtils.getBooleanPreference(this, "force_logout", false);
        //erik tutup odoo 040325
//        if(isForceLogout && !txtEmail.getText().toString().equals(SharedPrefsUtils.getStringPreference(this, "last_user"))){
//            Runnable runContinue = new Runnable() {
//                @Override
//                public void run() {
//                    GlobalOdoo.newLogin2(LoginActivity.this, getApplicationContext(), txtEmail.getText().toString(), txtPassword.getText().toString(), runnable, loading, dismiss);
//                }
//            };
//            ShowDialog.confirmDialog(LoginActivity.this, getString(R.string.app_name), getString(R.string.inform_diferent_user), runContinue);
//        }else {
////            GlobalOdoo.newLogin2(LoginActivity.this, getApplicationContext(), txtEmail.getText().toString(), txtPassword.getText().toString(), runnable, loading, dismiss);
//            GlobalMySQL.newLogin2(LoginActivity.this, getApplicationContext(), txtEmail.getText().toString(), txtPassword.getText().toString(), runnable, loading, dismiss);
//        }

        GlobalMySQL.newLogin2(LoginActivity.this, getApplicationContext(), txtEmail.getText().toString(), txtPassword.getText().toString(), runnable, loading, dismiss);
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

    private void updateUI(String email){
        if (!email.equals("")) {
            txtEmail.setText(email);
        }else{
            Snackbar.make(findViewById(android.R.id.content), R.string.must_be_online, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN){
            if(resultCode == RESULT_OK){
                GoogleSignInAccount Account = GoogleSignIn.getLastSignedInAccount(this);
                String AccountEmail = "";
                if (Account != null) {
                    AccountEmail = Account.getEmail();
                    updateUI(AccountEmail);
                }
            }
        }
    }

}
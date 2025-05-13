package com.orion.pasienqu_2.activities.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.utility.GetIPAddress;

public class ForgetPasswordActivity extends CustomAppCompatActivity {

    private TextInputEditText txtEmail;
    private TextInputLayout errEmail;
    private Button btnSendMail, btnBack;
    private ProgressBar pgLoading;
    private View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setTitle(getString(R.string.forget_password_title));
        CreateView();
        EventClass();
    }



    private void CreateView() {
        txtEmail = (TextInputEditText) findViewById(R.id.txtEmail);
        errEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        btnSendMail = (Button) findViewById(R.id.btnSendMail);
        btnBack = (Button) findViewById(R.id.btnBack);
        pgLoading = (ProgressBar) findViewById(R.id.pg_loading);
        mainLayout = (View) findViewById(R.id.main_layout);
    }

    private void EventClass() {
       btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               onBackPressed();
           }
       });

       btnSendMail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!Global.CheckConnectionInternet(ForgetPasswordActivity.this)) {
                   ShowDialog.infoDialog(ForgetPasswordActivity.this, getString(R.string.information), getString(R.string.must_be_online));
                   return;
               }
               if (JApplication.getInstance().real_url.isEmpty()) {
                   Runnable runSuccess = new Runnable() {
                       @Override
                       public void run() {
                           sendEmail();
                       }
                   };
                   GetIPAddress.get_ip_address(ForgetPasswordActivity.this, runSuccess);
                   return;
               }
               sendEmail();
           }
       });

    }

    private boolean IsValid(){
        if (TextUtils.isEmpty(txtEmail.getText())) {
            errEmail.setError(getString(R.string.error_email));
        }else if(!TextUtils.isEmpty(txtEmail.getText()) && !Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches()){
            errEmail.setError(getString(R.string.error_invalid_email));
        }else{
            errEmail.setErrorEnabled(false);
        }

        return true;
    }

    private void sendEmail(){
        Global.hideSoftKeyboard(ForgetPasswordActivity.this);

        if (IsValid()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mainLayout, R.string.notif_forget_password, Snackbar.LENGTH_LONG).show();
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
            Runnable runForgetPassword = new Runnable() {
                @Override
                public void run() {
                    if (!SharedPrefsUtils.getBooleanPreference(ForgetPasswordActivity.this, "is_mysql", false)) {
                        //erik tutup odoo 040325
//                        GlobalOdoo.forgetPassword(ForgetPasswordActivity.this, getApplicationContext(), txtEmail.getText().toString(), runnable, loading, dismiss);
                    } else {
                        GlobalMySQL.forgetPassword(ForgetPasswordActivity.this, getApplicationContext(), txtEmail.getText().toString(), runnable, loading, dismiss);
                    }
                }
            };
            GlobalMySQL.check_email_mysql(ForgetPasswordActivity.this, txtEmail.getText().toString(), runForgetPassword);
        }
    }

    private void showLoading(){
        pgLoading.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
    }

    private void dismissLoading(){
        pgLoading.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
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
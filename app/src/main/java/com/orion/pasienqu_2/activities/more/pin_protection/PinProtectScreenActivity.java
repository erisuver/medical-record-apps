package com.orion.pasienqu_2.activities.more.pin_protection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.GlobalMySQL;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.LoginInformationModel;

public class PinProtectScreenActivity extends CustomAppCompatActivity {
    private TextInputEditText txtPin;
    private TextInputLayout errPin;
    private Button btnForget;
    private String pinProtection;
    private boolean isFirstOpenApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_protect_screen);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        txtPin = (TextInputEditText) findViewById(R.id.txtPin);
        errPin = (TextInputLayout) findViewById(R.id.layoutPin);
        btnForget = (Button) findViewById(R.id.btnForget);
    }

    private void InitClass(){
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        pinProtection = loginInformationModel.getPinProtection();

        Bundle extra = this.getIntent().getExtras();
        if (extra != null){
            isFirstOpenApp = extra.getBoolean("first_open_app");
        }
    }

    private void EventClass(){
        txtPin.addTextChangedListener(new TextWatcher() {
            int pinDigit = pinProtection.length();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i < 5){
                    errPin.setError(getString(R.string.error_pin));
                }else{
                    errPin.setErrorEnabled(false);
                    errPin.setErrorEnabled(true);
                }

                verifyPin();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnForget.setOnClickListener(view -> {
            int newPin = Global.generateNewPin();

            Runnable runResetPin = new Runnable() {
                @Override
                public void run() {
                    Runnable runDialog = new Runnable() {
                        @Override
                        public void run() {
                            pinProtection = String.valueOf(newPin);
                            SharedPreferences sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putBoolean("usePin", true);
                            editor.putString("pinProtection", pinProtection);
                            editor.commit();
                            editor.apply();
                            JApplication.getInstance().setLoginInformationBySharedPreferences();

                            ShowDialog.infoDialog(PinProtectScreenActivity.this, getString(R.string.reset_pin), getString(R.string.pin_reset_sucess));
                        }
                    };
//                    GlobalOdoo.resetPin(PinProtectScreenActivity.this, newPin, runDialog);
                    GlobalMySQL.resetPin(PinProtectScreenActivity.this, newPin, runDialog);
                }
            };
            ShowDialog.confirmDialog(PinProtectScreenActivity.this, getString(R.string.reset_pin), getString(R.string.reset_pin_intro), runResetPin);
        });

    }

    private void verifyPin(){
        String pinInput = txtPin.getText().toString().trim();
        int pinDigit = txtPin.getText().toString().trim().length();

        if ((pinInput.equals(pinProtection)) && (pinDigit == 6)){
            Intent intent = PinProtectScreenActivity.this.getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }else if((!pinInput.equals(pinProtection)) && (pinDigit == 6)){
//            Toast.makeText(PinProtectScreenActivity.this, getString(R.string.error_invalid_pin), Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_invalid_pin), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFirstOpenApp){
            finishAffinity();
        }else {
            super.onBackPressed();
        }
    }
}
package com.orion.pasienqu_2.activities.more.pin_protection;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.globals.Global;
import com.orion.pasienqu_2.globals.ShowDialog;
import com.orion.pasienqu_2.models.LoginInformationModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class PinProtectionActivity extends CustomAppCompatActivity {
    private TextInputEditText txtPin, txtConfirmPin;
    private TextInputLayout errPin, errConfirmPin;
    private Button btnSave;
    private Switch swcPin;
    private View inputView;
    private String pinProtection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_protection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.pin_protection));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        txtPin = (TextInputEditText) findViewById(R.id.txtPin);
        txtConfirmPin = (TextInputEditText) findViewById(R.id.txtConfirmPin);
        errPin = (TextInputLayout) findViewById(R.id.layoutPin);
        errConfirmPin = (TextInputLayout) findViewById(R.id.layoutConfirmPin);
        swcPin = (Switch) findViewById(R.id.swcPin);
        btnSave = (Button) findViewById(R.id.btnSave);
        inputView = (View) findViewById(R.id.layoutInputPin);
    }

    private void InitClass(){
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        pinProtection = loginInformationModel.getPinProtection();

        if (!pinProtection.equals("")) {
            Intent i = new Intent(PinProtectionActivity.this, PinProtectScreenActivity.class);
            startActivityForResult(i, 1);
        }

        LoadData();

    }

    private void EventClass(){
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(SavePin()) {
                    Global.hideSoftKeyboard(PinProtectionActivity.this, view);
                    Snackbar.make(findViewById(android.R.id.content), R.string.pin_update_status, Snackbar.LENGTH_SHORT).show();

                    //set delay agar menampilkan pin status is update dulu baru close
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2000); //2 detik
                }
            }
        });

        swcPin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSwitch();
            }
        });

    }

    private void LoadData(){
        if (!pinProtection.equals("")) {
            swcPin.setChecked(true);
            inputView.setVisibility(View.VISIBLE);
//            txtPin.setText(pinProtection);
//            txtConfirmPin.setText(pinProtection);
        }else {
            swcPin.setChecked(false);
            inputView.setVisibility(View.GONE);
        }
    }

    private boolean IsValid(){
        String pin = txtPin.getText().toString().trim();
        String confirmPin = txtConfirmPin.getText().toString().trim();

        if (pin.length() < 6) {
            errPin.setError(getString(R.string.error_pin));
        }else{
            errPin.setErrorEnabled(false);
            errPin.setErrorEnabled(true);
        }

        if (confirmPin.length() < 6) {
            errConfirmPin.setError(getString(R.string.error_pin));
        }else{
            errConfirmPin.setErrorEnabled(false);
            errConfirmPin.setErrorEnabled(true);
        }

        if(!pin.equals(confirmPin)){
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_pin_confirm_not_match), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean SavePin(){
        if (this.IsValid()) {
            String pin = txtPin.getText().toString().trim();
            if (pin.length() < 6){
                return false;
            }

            SharedPreferences sharedpreferences;
            sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            if (swcPin.isChecked()) {
                editor.putBoolean("usePin", true);
                editor.putString("pinProtection", pin);
                editor.commit();
                editor.apply();
                JApplication.getInstance().setLoginInformationBySharedPreferences();
            }
            return true;
        }
        return false;
    }

    private void ResetPin(){
        SharedPreferences sharedpreferences;
        sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean("usePin", false);
        editor.putString("pinProtection", "");
        editor.commit();
        editor.apply();
        JApplication.getInstance().setLoginInformationBySharedPreferences();


        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.pin_update_status, Snackbar.LENGTH_SHORT).show();
    }

    private void setSwitch(){
        if (swcPin.isChecked()) {
            inputView.setVisibility(View.VISIBLE);
            errPin.setErrorEnabled(true);
            errConfirmPin.setErrorEnabled(true);
        }else{
            Global.hideSoftKeyboard(PinProtectionActivity.this);
            resetText();
            inputView.setVisibility(View.GONE);
            ResetPin();
        }
    }


    private void resetText(){
        txtPin.setText("");
        txtConfirmPin.setText("");
        errPin.setErrorEnabled(false);
        errConfirmPin.setErrorEnabled(false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    LoadData();
                }else{
                    finish();
                }
            break;
        }

    }


}
package com.orion.pasienqu_2.activities.more.generate_patient_id;

import androidx.appcompat.app.AppCompatActivity;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.models.LoginInformationModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

public class GeneratePatientIDActivity extends CustomAppCompatActivity {
    private Switch swcGenerateID;
    private ProgressBar pg_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_patient_idactivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.generate_patient_id_title));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        swcGenerateID = (Switch) findViewById(R.id.swcGenerateID);
        pg_loading = (ProgressBar) findViewById(R.id.pg_loading);
    }

    private void InitClass(){
        LoginInformationModel loginInformationModel = JApplication.getInstance().loginInformationModel;
        swcGenerateID.setChecked(loginInformationModel.isAutoGeneratePatientId());
    }

    private void EventClass(){
        swcGenerateID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setSwitch();
            }
        });
    }

    private void setSwitch(){
        SharedPreferences sharedpreferences;
        sharedpreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (swcGenerateID.isChecked()) {
            editor.putBoolean("autogenerate_patient_id", true);
            editor.commit();
            editor.apply();
            JApplication.getInstance().setLoginInformationBySharedPreferences();
        }else{
            editor.putBoolean("autogenerate_patient_id", false);
            editor.commit();
            editor.apply();
            JApplication.getInstance().setLoginInformationBySharedPreferences();
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
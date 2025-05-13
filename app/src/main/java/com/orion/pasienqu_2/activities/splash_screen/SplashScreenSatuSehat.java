package com.orion.pasienqu_2.activities.splash_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.home.home;
import com.orion.pasienqu_2.globals.SharedPrefsUtils;

public class SplashScreenSatuSehat extends AppCompatActivity {
    private Button btnContinue;
    private CheckBox chbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_satu_sehat);
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView() {
        btnContinue = findViewById(R.id.btnContinue);
        chbRemember = findViewById(R.id.chbRemember);
    }

    private void InitClass() {
        boolean isMunculkanSplash = SharedPrefsUtils.getBooleanPreference(getApplicationContext(), "isMunculkanSplash", true);
        if (!isMunculkanSplash){
            openMainClass();
        }
    }

    private void EventClass() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainClass();
            }
        });

        chbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPrefsUtils.setBooleanPreference(getApplicationContext(), "isMunculkanSplash", !isChecked);
            }
        });
    }

    private void openMainClass(){
        // Navigasi ke aktivitas berikutnya
        startActivity(new Intent(SplashScreenSatuSehat.this, home.class));
        finish(); // Tutup splash screen
    }

}
package com.orion.pasienqu_2.globals;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;

import java.util.Locale;

public class CustomAppCompatActivity extends AppCompatActivity {
    private boolean isSpecialLanguageSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
    }


    private void setLanguage() {
        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if(!language.equals("")) {
            if (language.equals("in") || isSpecialLanguageSelected) {
                Locale locale = new Locale("in");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }else{
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        }else{
            if (isSpecialLanguageSelected){
                Locale locale = new Locale("in");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }else {
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = getResources().getConfiguration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }
        }
    }

    // Metode untuk mengembalikan bahasa ke default setelah keluar dari menu khusus bahasa Indonesia
    public void resetLanguage() {
        isSpecialLanguageSelected = false; // Mengatur flag kembali ke false
        setLanguage(); // Mengatur bahasa kembali ke default
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLanguage();
    }

    // Metode untuk menandai bahwa bahasa spesial (ID) telah dipilih
    public void setSpecialLanguageSelected() {
        this.isSpecialLanguageSelected = true;
        setLanguage(); // Mengatur bahasa kembali ke default
    }
}

package com.orion.pasienqu_2.activities.more.switch_language;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.adapter.more.CountryAdapter;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;
import com.orion.pasienqu_2.models.CountryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SwitchLanguageActivity extends CustomAppCompatActivity {
    public CountryAdapter mAdapter;
    public List<CountryModel> ListItems = new ArrayList<>();
    private Activity thisActivity;
    private RecyclerView rcvLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_language);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.set_language));
        CreateView();
        InitClass();
        EventClass();
    }

    private void CreateView(){
        rcvLoad = (RecyclerView) findViewById(R.id.rcvLoad);
        thisActivity = SwitchLanguageActivity.this;
        mAdapter = new CountryAdapter(thisActivity, ListItems, R.layout.switch_language_list_item);
    }

    private void InitClass(){
        rcvLoad.setLayoutManager(new GridLayoutManager(thisActivity, 1, GridLayoutManager.VERTICAL, false));
        rcvLoad.setAdapter(mAdapter);
        loadData();
    }

    private void EventClass(){

    }

    private void loadData(){
        mAdapter.removeAllModel();
        CountryModel countryModel;
        String uuid = UUID.randomUUID().toString();

        SharedPreferences sharedPreferences = JApplication.getInstance().getSharedPreferences("login_information", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if(!language.equals("")) {
            if (language.equals("in")) {
                countryModel = new CountryModel(1, "", getString(R.string.english), R.drawable.en, 0);
                mAdapter.addModel(countryModel);

                countryModel = new CountryModel(2, "", getString(R.string.bahasa_indonesia), R.drawable.id, R.drawable.ic_baseline_check_24);
                mAdapter.addModel(countryModel);
            }else{
                countryModel = new CountryModel(1, "", getString(R.string.english), R.drawable.en, R.drawable.ic_baseline_check_24);
                mAdapter.addModel(countryModel);

                countryModel = new CountryModel(2, "", getString(R.string.bahasa_indonesia), R.drawable.id, 0);
                mAdapter.addModel(countryModel);
            }
        }else{
            language = Locale.getDefault().getCountry().toLowerCase(Locale.ROOT);
            if (language.equals("in")) {
                countryModel = new CountryModel(1, "", getString(R.string.english), R.drawable.en, 0);
                mAdapter.addModel(countryModel);

                countryModel = new CountryModel(2, "", getString(R.string.bahasa_indonesia), R.drawable.id, R.drawable.ic_baseline_check_24);
                mAdapter.addModel(countryModel);
            }else{
                countryModel = new CountryModel(1, "", getString(R.string.english), R.drawable.en, R.drawable.ic_baseline_check_24);
                mAdapter.addModel(countryModel);

                countryModel = new CountryModel(2, "", getString(R.string.bahasa_indonesia), R.drawable.id, 0);
                mAdapter.addModel(countryModel);
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
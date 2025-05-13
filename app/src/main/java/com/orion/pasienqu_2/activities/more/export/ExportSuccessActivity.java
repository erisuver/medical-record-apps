package com.orion.pasienqu_2.activities.more.export;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;

import java.io.File;

public class ExportSuccessActivity extends CustomAppCompatActivity {
    private Button btnBack;
    private TextView tvNoticeExport;
    private String locationExport;
    private String location, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_success);
        setTitle(getString(R.string.export));
        CreateView();
        initClass();
        EventClass();
    }

    private void CreateView(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnBack = (Button) findViewById(R.id.btnBack);
        tvNoticeExport = (TextView) findViewById(R.id.tvNoticeExport);
    }

    private void initClass(){
        Bundle extra = this.getIntent().getExtras();
        locationExport = extra.getString("file_name");
        location = extra.getString("location");
        name = extra.getString("name");
        
        tvNoticeExport.setText(getString(R.string.export_success_offline)+locationExport);
        btnBack.setText(R.string.open_excel);
    }

    private void EventClass(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onBackPressed();
                openExcel();
            }
        });
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

    private void openExcel(){
        try {
            File dst = new File(location, name);

            // Get URI and MIME type of file
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", dst);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String mimeType = map.getMimeTypeFromExtension("xls");

            // Open file with user selected app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            //cek kompatibel
            if (Build.VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, mimeType);
            } else {
                intent.setDataAndType(uri, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivity(intent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.orion.pasienqu_2.activities.ZoomImage;
//
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.globals.CustomAppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;


public class ZoomImageActivity extends CustomAppCompatActivity {
    private PhotoView photo;
    private ProgressDialog Loading;
    private int ResultGambar;
    private boolean IsAdaGambar;
    private String gambar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CreateView();
        InitView();
        EventClass();
        LoadData();
    }

    private void InitView() {
        Bundle extra = this.getIntent().getExtras();
        gambar = extra.getString("gambar");
        this.Loading = new ProgressDialog(ZoomImageActivity.this);
    }

    private void CreateView(){
        photo = (PhotoView) findViewById(R.id.imgGambar);
    }

    private void EventClass() {
    }

    private void LoadData() {
//        Loading.setMessage("Loading...");
//        Loading.setCancelable(false);
//        Loading.show();
        IsAdaGambar = false;

        byte[] imageByte = Base64.decode(gambar, Base64.DEFAULT);
        Drawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length));
        photo.setImageDrawable(drawable);
    }
}


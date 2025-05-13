package com.orion.pasienqu_2.utility;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.orion.pasienqu_2.BuildConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadHelper {
    private static final int PROGRESS_BAR_TYPE = 0;

    private ProgressDialog pDialog;
    private String destination;

    private Context mContext;

    public FileDownloadHelper(Context context) {
        mContext = context;
    }

    public void downloadFile(String url, String fileName) {
        url = url.replace("\\", "/");
//        destination = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
        destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ "/" + fileName;

        new DownloadFileFromURL().execute(url);
    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(PROGRESS_BAR_TYPE);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream(destination);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {
            openFile();
            dismissDialog(PROGRESS_BAR_TYPE);
        }
    }

    private void openFile() {
        String PATH = destination;
        File file = new File(PATH);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = uriFromFile(mContext, new File(PATH));
            String mime = mContext.getContentResolver().getType(uri);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error in opening the file!");
                Toast.makeText(mContext, "Tidak ditemukan aplikasi untuk membuka file ini.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Download gagal.", Toast.LENGTH_LONG).show();
        }
    }

    private Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    private void showDialog(int id) {
        switch (id) {
            case PROGRESS_BAR_TYPE:
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                break;
            default:
                break;
        }
    }

    private void dismissDialog(int id) {
        if (id == PROGRESS_BAR_TYPE && pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}


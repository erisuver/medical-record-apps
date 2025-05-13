package com.orion.pasienqu_2.utility;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModernDownloadHelper {

    private final Context mContext;
    private ProgressDialog progressDialog;

    public ModernDownloadHelper(Context context) {
        this.mContext = context;
    }

    public void downloadToPublicDownloads(String fileUrl, String fileName, String mimeType) {
        showProgressDialog();

        new Thread(() -> {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(connection.getInputStream());

                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = mContext.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    OutputStream output = mContext.getContentResolver().openOutputStream(uri);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    int total = 0;

                    while ((bytesRead = input.read(buffer)) != -1) {
                        total += bytesRead;
                        output.write(buffer, 0, bytesRead);

                        int progress = (fileLength > 0) ? (total * 100 / fileLength) : -1;
                        updateProgress(progress);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    ((android.app.Activity) mContext).runOnUiThread(() -> {
                        dismissProgressDialog();
                        Toast.makeText(mContext, "File berhasil disimpan di folder Download", Toast.LENGTH_SHORT).show();
                        openDownloadedFile(uri, mimeType);
                    });
                } else {
                    throw new Exception("Gagal insert ke MediaStore");
                }

            } catch (Exception e) {
                e.printStackTrace();
                ((android.app.Activity) mContext).runOnUiThread(() -> {
                    dismissProgressDialog();
                    Toast.makeText(mContext, "Download gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void openDownloadedFile(Uri uri, String mimeType) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Tidak ditemukan aplikasi untuk membuka file ini", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog() {
        ((android.app.Activity) mContext).runOnUiThread(() -> {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Downloading file...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        });
    }

    private void updateProgress(int progress) {
        ((android.app.Activity) mContext).runOnUiThread(() -> {
            if (progressDialog != null && progress >= 0) {
                progressDialog.setProgress(progress);
            }
        });
    }

    private void dismissProgressDialog() {
        ((android.app.Activity) mContext).runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }
}

package com.orion.pasienqu_2.globals.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.R;
import com.orion.pasienqu_2.activities.more.data_migration.DataMigrationActivity;
import com.orion.pasienqu_2.globals.JConst;
import com.orion.pasienqu_2.globals.ShowDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MigrationMedia {
        public static void migrationMedia(Activity activity, Runnable run, Runnable runFailed, ProgressDialog pDialog) {
        // ...
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        //migrasi file
        List<MultipartBody.Part> mediaParts = new ArrayList<>();
        File folder = new File(JConst.mediaLocationPath);
        File[] files = folder.listFiles();

        // Mengambil konten file DMP
        File pathDML = new File(JConst.DMPLocationPath);
        File fileDmp = new File(pathDML, "dmpTemp.txt");
        RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), fileDmp);
        MultipartBody.Part dmpPart = MultipartBody.Part.createFormData("dmp", fileDmp.getName(), requestFile);
        int jml = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    RequestBody requestFileMedia = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part mediaPart = mediaPart = MultipartBody.Part.createFormData("mediaFiles", file.getName(), requestFileMedia);
                    mediaParts.add(mediaPart);

                    jml = jml + 1;
                }
            }
        }


        // Mengambil nilai lain dari param
        String folderString = "company_" + JApplication.getInstance().loginCompanyModel.getId();
        RequestBody folderBody = RequestBody.create(MediaType.parse("text/plain"), folderString);
        RequestBody companyIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(JApplication.getInstance().loginCompanyModel.getId()));
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), JApplication.getInstance().loginCompanyModel.getEmail());

//        Call<MigrationResponse> call = apiService.uploadData(dmpPart, folderBody, companyIdBody, emailBody, mediaParts);
//        Call<MigrationResponse> call = apiService.uploadData(folderBody, companyIdBody, emailBody, mediaParts);
        final int[] jmlSukses = {0};
        pDialog.setProgress(0);

        final int jmlAkhir = jml;

        if (jmlAkhir > 0) {
            for (int i = 0; i < jmlAkhir; i++) {
                Call<MigrationResponse> call = apiService.uploadData(mediaParts.get(i), folderBody, companyIdBody, emailBody);
                call.enqueue(new Callback<MigrationResponse>() {
                    @Override
                    public void onResponse(Call<MigrationResponse> call, Response<MigrationResponse> response) {
                        // Tanggapi respons
                        if (response.isSuccessful()) {
                            MigrationResponse data = response.body();
                            //                    ShowDialog.infoDialog(activity, data.toString(), activity.getString(R.string.migration_success));
                            // Lakukan sesuatu dengan data
                        } else {
                            // Tangani respon error
                            // Misalnya, dapat Anda tampilkan pesan kesalahan ke pengguna
                            //                    Toast.makeText(activity, response.body().toString(), Toast.LENGTH_SHORT).show();
                        }
                        jmlSukses[0]++;
                        int progress = (int) (((float) jmlSukses[0] / jmlAkhir) * 100);
                        pDialog.setProgress(progress);

                        // Check if all responses have been received
                        if (jmlSukses[0] == jmlAkhir) {
                            run.run();
                        }
                    }

                    @Override
                    public void onFailure(Call<MigrationResponse> call, Throwable t) {
                        //                Toast.makeText(activity, activity.getString(R.string.error_connection_timeout), Toast.LENGTH_SHORT).show();
                        runFailed.run();
                    }
                });
            }
        }else{
            run.run();
        }
    }
}

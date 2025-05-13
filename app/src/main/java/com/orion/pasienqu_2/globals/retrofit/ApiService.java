package com.orion.pasienqu_2.globals.retrofit;
import com.orion.pasienqu_2.Routes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("media/migrasi")
    Call<MigrationResponse> uploadData(
            @Part MultipartBody.Part dmp,
            @Part("folder") RequestBody folder,
            @Part("company_id") RequestBody companyId,
            @Part("email") RequestBody email
//            ,
//            @Part List<MultipartBody.Part> mediaParts
    );
}
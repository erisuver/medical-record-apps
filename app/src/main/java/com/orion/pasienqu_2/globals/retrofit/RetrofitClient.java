package com.orion.pasienqu_2.globals.retrofit;

import com.orion.pasienqu_2.JApplication;
import com.orion.pasienqu_2.Routes;
import com.orion.pasienqu_2.globals.retrofit.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(JApplication.getInstance().real_url.replace(Routes.NAMA_API, Routes.NAMA_API_PASIENQU_ONLINE))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getClientReconnect() {
        retrofit = new Retrofit.Builder()
                .baseUrl(JApplication.getInstance().real_url.replace(Routes.NAMA_API, Routes.NAMA_API_PASIENQU_ONLINE))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public static Retrofit getClientTestConnect(String ip) {
        retrofit = new Retrofit.Builder()
                .baseUrl(JApplication.getInstance().real_url.replace(Routes.NAMA_API, Routes.NAMA_API_PASIENQU_ONLINE))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}

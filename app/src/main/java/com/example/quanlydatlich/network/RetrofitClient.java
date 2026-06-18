package com.example.quanlydatlich.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.90.101:5000/";

    //private static final String BASE_URL = "http://192.168.90.101:5000/";

    private static Retrofit retrofit = null;
    public static ApiService getApiService() {
        if (retrofit == null) {
            // Nếu chưa có thì mới tạo mới
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // Nếu tạo rồi thì dùng lại
        return retrofit.create(ApiService.class);
    }
}
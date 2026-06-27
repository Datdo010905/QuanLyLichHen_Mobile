package com.example.quanlydatlich.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://192.168.90.100:5000/";

    //public static final String BASE_URL = "http://10.19.202.204:5000/";
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
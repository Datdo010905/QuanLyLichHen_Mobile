package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.AuthModel;
import com.example.quanlydatlich.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.quanlydatlich.network.RetrofitClient;
public class AuthRepository {

    public interface AuthCallback {
        void onSuccess(String message, String tenKhach, String token);
        void onError(String errorMessage);
    }

    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void login(String username, String password, AuthCallback callback) {
        AuthModel.LoginRequest request = new AuthModel.LoginRequest(username, password);

        apiService.loginUser(request).enqueue(new Callback<AuthModel.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthModel.LoginResponse> call, Response<AuthModel.LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();
                    String matk = response.body().getData().getMatk();
                    String token = response.body().getToken();
                    callback.onSuccess(msg, matk, token);
                }
                else {
                    try {
                        //lấy lỗi từ máy chủ
                        String errorString = response.errorBody().string();
                        org.json.JSONObject errorJson = new org.json.JSONObject(errorString);
                        callback.onError(errorJson.getString("message"));
                    } catch (Exception e) {
                        callback.onError("Lỗi không xác định từ máy chủ!");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthModel.LoginResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
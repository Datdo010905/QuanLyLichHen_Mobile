package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.AuthModel;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    // Interface trả kết quả đăng nhập về Activity
    public interface AuthCallback {
        void onSuccess(String message, String tenKhach, String token);
        void onError(String errorMessage);
    }

    // Đối tượng gọi API
    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void login(String username, String password, AuthCallback callback) {

        // Tạo dữ liệu gửi lên máy chủ
        AuthModel.LoginRequest request =
                new AuthModel.LoginRequest(username, password);

        // Gọi API đăng nhập
        apiService.loginUser(request).enqueue(
                new Callback<AuthModel.LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<AuthModel.LoginResponse> call,
                            Response<AuthModel.LoginResponse> response) {

                        // Đăng nhập thành công
                        if (response.isSuccessful()
                                && response.body() != null) {

                            String msg = response.body().getMessage();
                            String matk = response.body().getData().getMatk();
                            String token = response.body().getToken();

                            // Trả kết quả về Activity
                            callback.onSuccess(msg, matk, token);
                        }
                        else {
                            try {
                                // lấy lỗi từ máy chủ
                                String errorString = response.errorBody().string();

                                org.json.JSONObject errorJson =
                                        new org.json.JSONObject(errorString);

                                callback.onError(
                                        errorJson.getString("message")
                                );

                            } catch (Exception e) {
                                callback.onError(
                                        "Lỗi không xác định từ máy chủ!"
                                );
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<AuthModel.LoginResponse> call, Throwable t) {
                        // Lỗi mạng hoặc không kết nối được máy chủ
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}
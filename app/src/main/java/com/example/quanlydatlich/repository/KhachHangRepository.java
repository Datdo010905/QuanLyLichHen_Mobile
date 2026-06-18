package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KhachHangRepository {
    private ApiService apiService;
    public KhachHangRepository() {
        // Lấy kết nối mạng
        apiService = RetrofitClient.getApiService();
    }
    public interface KhachHangCallback {
        void onSuccess(KhachHangResponse.KhachHangDetail khachHang);
        void onError(String errorMessage);
    }

    public void fetchThongTinKhachHang(String maTK, KhachHangCallback callback) {
        apiService.getThongTinKhachHang(maTK).enqueue(new Callback<KhachHangResponse>() {
            @Override
            public void onResponse(Call<KhachHangResponse> call, Response<KhachHangResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KhachHangResponse res = response.body();

                    if (res.isSuccess() && res.getData() != null) {
                        callback.onSuccess(res.getData());
                    } else {
                        callback.onError("Dữ liệu rỗng hoặc lỗi từ Web!");
                    }
                } else {
                    callback.onError("Không lấy được thông tin! Lỗi HTTP: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<KhachHangResponse> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
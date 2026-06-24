package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.UpdateProfileRequest;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private ApiService apiService;

    public ProfileRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public interface ProfileCallback<T> {
        void onSuccess(T data);

        void onError(String msg);
    }

    // Fetch data
    public void getProfile(String maTK, ProfileCallback<KhachHangResponse.KhachHangDetail> callback) {
        apiService.getKhachHangById(maTK).enqueue(new Callback<KhachHangResponse>() {
            @Override
            public void onResponse(Call<KhachHangResponse> call, Response<KhachHangResponse> response) {
                if (response.isSuccessful() && response.body() != null)
                    callback.onSuccess(response.body().getData());
                else callback.onError("Không kéo được dữ liệu!");
            }

            @Override
            public void onFailure(Call<KhachHangResponse> call, Throwable t) {
                callback.onError("Lỗi mạng!");
            }
        });
    }

    // Update data
    public void updateProfile(String maTK, UpdateProfileRequest request, ProfileCallback<String> callback) {
        apiService.updateKhachHangProfile(maTK, request).enqueue(new Callback<KhachHangResponse>() {
            @Override
            public void onResponse(Call<KhachHangResponse> call, Response<KhachHangResponse> response) {
                if (response.isSuccessful()) callback.onSuccess("Cập nhật thành công!");
                else callback.onError("Cập nhật thất bại!");
            }

            @Override
            public void onFailure(Call<KhachHangResponse> call, Throwable t) {
                callback.onError("Lỗi đường truyền!");
            }
        });
    }
}
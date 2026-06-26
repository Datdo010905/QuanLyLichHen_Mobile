package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.RegisterRequest;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
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
    public interface RegisterCallback {
        void onSuccess(String message);
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


    public void registerKhachHang(String hoTen, String phone, String email, String password, RegisterCallback callback) {
        // Gom dữ liệu từ UI thành Object
        RegisterRequest request = new RegisterRequest(hoTen, phone, email, password);

        apiService.registerUser(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() || response.code() == 201) {
                        callback.onSuccess("Đăng ký thành công!");
                    } else {
                        // báo lỗi từ Node.js (ví dụ: "Số điện thoại đã tồn tại!")
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        JSONObject jsonObject = new JSONObject(errorBody);
                        String serverMessage = jsonObject.optString("message", "Đăng ký thất bại!");

                        callback.onError(serverMessage);
                    }
                } catch (Exception e) {
                    callback.onError("Lỗi phản hồi từ máy chủ!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ. Vui lòng thử lại!");
            }
        });
    }
}
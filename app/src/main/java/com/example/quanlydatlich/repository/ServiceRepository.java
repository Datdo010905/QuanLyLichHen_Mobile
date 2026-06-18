package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.ServiceResponse.ServiceModel;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Kho chứa các phương thức truy xuất API để lấy dữ liệu về
// xong thì mới gửi
public class ServiceRepository {

    public interface ServiceCallback {
        void onSuccess(List<ServiceModel> serviceList);
        void onError(String errorMessage);
    }

    // API dịch vụ
    private final ApiService apiService;

    // Hàm khởi tạo
    public ServiceRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // Hàm lấy tất cả danh sách dịch vụ
    public void fetchAllServices(ServiceCallback callback) {

        apiService.getServicesAll().enqueue(new Callback<ServiceResponse>() {

            @Override
            public void onResponse(Call<ServiceResponse> call,
                                   Response<ServiceResponse> response) {

                // Check có kết quả trả về
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    callback.onSuccess(response.body().getData());

                } else {
                    callback.onError("Lỗi trả về từ server!" + response.code());
                }
            }

            // Nếu không có kết quả trả về
            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                callback.onError("Mất kết nối: " + t.getMessage());
            }
        });
    }

    // Hàm lấy danh sách dịch vụ
    public void fetchServices(ServiceCallback callback) {

        apiService.getServices().enqueue(new Callback<ServiceResponse>() {

            @Override
            public void onResponse(Call<ServiceResponse> call,
                                   Response<ServiceResponse> response) {

                // Check có kết quả trả về
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    callback.onSuccess(response.body().getData());

                } else {
                    callback.onError("Lỗi trả về từ server!");
                }
            }

            // Nếu không có kết quả trả về
            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                callback.onError("Mất kết nối: " + t.getMessage());
            }
        });
    }

    // Hàm lấy danh sách dịch vụ chăm sóc da
    public void fetchServices2(ServiceCallback callback) {

        apiService.getServices2().enqueue(new Callback<ServiceResponse>() {

            @Override
            public void onResponse(Call<ServiceResponse> call,
                                   Response<ServiceResponse> response) {

                // Check có kết quả trả về
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    callback.onSuccess(response.body().getData());

                } else {
                    callback.onError("Lỗi trả về từ server!");
                }
            }

            // Nếu không có kết quả trả về
            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                callback.onError("Mất kết nối: " + t.getMessage());
            }
        });
    }
}
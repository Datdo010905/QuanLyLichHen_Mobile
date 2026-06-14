package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.ServiceResponse.ServiceModel;
import com.example.quanlydatlich.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.quanlydatlich.network.RetrofitClient; // Nhớ import vào nhé

//kho chứa các phương thức truy xuất API để lấy dữ liệu về
//xong thì mới gửi
public class ServiceRepository {
    public interface ServiceCallback {
        void onSuccess(List<ServiceModel> serviceList);
        void onError(String errorMessage);
    }
    //api dịch vụ
    private ApiService apiService;

    //hàm khởi tạo
    public ServiceRepository() {
        apiService = RetrofitClient.getApiService();
    }

    //hàm lấy danh sách dịch vụ
    public void fetchServices(ServiceCallback callback) {
        apiService.getServices().enqueue(new Callback<com.example.quanlydatlich.model.ServiceResponse>() {
            @Override
            public void onResponse(Call<com.example.quanlydatlich.model.ServiceResponse> call,
                                   Response<com.example.quanlydatlich.model.ServiceResponse> response) {

                //check có kết quả trả về
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Lỗi trả về từ server!");
                }
            }

            //nếu không có kết quả trả về
            @Override
            public void onFailure(Call<com.example.quanlydatlich.model.ServiceResponse> call, Throwable t) {
                callback.onError("Mất kết nối: " + t.getMessage());
            }
        });
    }

    //hàm lấy danh sách dịch vụ csd
    public void fetchServices2(ServiceCallback callback) {
        apiService.getServices2().enqueue(new Callback<com.example.quanlydatlich.model.ServiceResponse>() {
            @Override
            public void onResponse(Call<com.example.quanlydatlich.model.ServiceResponse> call,
                                   Response<com.example.quanlydatlich.model.ServiceResponse> response) {

                //check có kết quả trả về
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Lỗi trả về từ server!");
                }
            }

            //nếu không có kết quả trả về
            @Override
            public void onFailure(Call<com.example.quanlydatlich.model.ServiceResponse> call, Throwable t) {
                callback.onError("Mất kết nối: " + t.getMessage());
            }
        });
    }
}
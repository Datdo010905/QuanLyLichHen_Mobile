package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.StaffBookingResponse;
import com.example.quanlydatlich.model.UpdateStatusRequest;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffRepository {
    private ApiService apiService;

    public StaffRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public interface StaffDataCallback {
        void onSuccess(List<StaffBookingResponse.StaffBooking> listLich, List<ServiceResponse.ServiceModel> listDV);

        void onError(String msg);
    }

    //GỘP 2 API VÀO 1 ĐỂ ACTIVITY KHÔNG BỊ LỒNG CALLBACK
    public void fetchStaffData(String maTK, StaffDataCallback callback) {
        apiService.getServicesAll().enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ServiceResponse.ServiceModel> listDV = response.body().getData();

                    // Lấy xong Dịch vụ mới đi kéo Lịch
                    apiService.getLichHenCuaNhanVien(maTK).enqueue(new Callback<StaffBookingResponse>() {
                        @Override
                        public void onResponse(Call<StaffBookingResponse> call, Response<StaffBookingResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body().data, listDV);
                            } else callback.onError("Lỗi tải lịch!");
                        }

                        @Override
                        public void onFailure(Call<StaffBookingResponse> call, Throwable t) {
                            callback.onError("Lỗi mạng!");
                        }
                    });
                } else callback.onError("Lỗi tải dịch vụ!");
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                callback.onError("Lỗi mạng!");
            }
        });
    }

    public void updateStatus(String maLich, String trangThai, Callback<BookingResponse> callback) {
        apiService.updateBookingStatus(maLich, new UpdateStatusRequest(trangThai)).enqueue(callback);
    }
}
package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.BookingRequest;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private ApiService apiService;

    public BookingRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public interface BookingCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public void submitBooking(BookingRequest request, BookingCallback callback) {
        apiService.createBookingTransaction(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse res = response.body();
                    if (res.isSuccess()) {
                        callback.onSuccess(res.getMessage());
                    } else {
                        callback.onError(res.getMessage());
                    }
                } else {
                    //lỗi 400 (Trùng mã) hoặc 500
                    callback.onError("Lỗi máy chủ: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                callback.onError("Đứt cáp mạng: " + t.getMessage());
            }
        });
    }
}
package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.BookingRequest;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

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

    //Báo khi lấy xong đủ 4 món
    public interface SetupDataCallback {
        void onSuccess(List<NhanVien> listNV,
                       List<LichHen> listLH,
                       List<ChiTietLichHen> listCT,
                       List<ServiceResponse.ServiceModel> listDV);

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

        apiService.createBookingTransaction(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse res = response.body();
                    if (res.isSuccess()) callback.onSuccess(res.getMessage());
                    else callback.onError(res.getMessage());
                } else {
                    callback.onError("Lỗi máy chủ: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                callback.onError("Đứt cáp mạng: " + t.getMessage());
            }
        });
    }

    //Gọi 4 API song song
    public void fetchSetupData(SetupDataCallback callback) {
        final int[] successCount = {0};
        final boolean[] isFailed = {false};

        //chứa tạm thời
        final List<NhanVien>[] nvList = new List[]{new ArrayList<>()};
        final List<LichHen>[] lhList = new List[]{new ArrayList<>()};
        final List<ChiTietLichHen>[] ctList = new List[]{new ArrayList<>()};
        final List<ServiceResponse.ServiceModel>[] dvList = new List[]{new ArrayList<>()};

        //Đủ 4 thì thành công
        Runnable checkDone = () -> {
            if (isFailed[0]) return;
            successCount[0]++;
            if (successCount[0] == 4) {
                callback.onSuccess(nvList[0], lhList[0], ctList[0], dvList[0]);
            }
        };

        //Có 1 -> báo lỗi
        Runnable throwError = () -> {
            if (!isFailed[0]) {
                isFailed[0] = true;
                callback.onError("Không thể tải dữ liệu máy chủ!");
            }
        };

        // 1. Lấy Nhân Viên
        apiService.getAllNhanVien().enqueue(new Callback<NhanVien.NhanVienRes>() {
            @Override
            public void onResponse(Call<NhanVien.NhanVienRes> call, Response<NhanVien.NhanVienRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    nvList[0] = response.body().data;
                    checkDone.run();
                } else throwError.run();
            }

            @Override
            public void onFailure(Call<NhanVien.NhanVienRes> call, Throwable t) {
                throwError.run();
            }
        });

        // 2. Lấy Lịch Hẹn
        apiService.getAllLichHen().enqueue(new Callback<LichHen.LichHenRes>() {
            @Override
            public void onResponse(Call<LichHen.LichHenRes> call, Response<LichHen.LichHenRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lhList[0] = response.body().data;
                    checkDone.run();
                } else throwError.run();
            }

            @Override
            public void onFailure(Call<LichHen.LichHenRes> call, Throwable t) {
                throwError.run();
            }
        });

        // 3. Lấy Chi Tiết
        apiService.getAllChiTietLichHen().enqueue(new Callback<ChiTietLichHen.ChiTietRes>() {
            @Override
            public void onResponse(Call<ChiTietLichHen.ChiTietRes> call, Response<ChiTietLichHen.ChiTietRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ctList[0] = response.body().data;
                    checkDone.run();
                } else throwError.run();
            }

            @Override
            public void onFailure(Call<ChiTietLichHen.ChiTietRes> call, Throwable t) {
                throwError.run();
            }
        });

        // 4. Lấy Dịch Vụ
        apiService.getServicesAll().enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dvList[0] = response.body().getData();
                    checkDone.run();
                } else throwError.run();
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                throwError.run();
            }
        });
    }
}
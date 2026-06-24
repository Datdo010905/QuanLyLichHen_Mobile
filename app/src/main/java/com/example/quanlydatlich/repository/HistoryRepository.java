package com.example.quanlydatlich.repository;

import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.UpdateStatusRequest;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepository {
    private ApiService apiService;

    public HistoryRepository() {
        apiService = RetrofitClient.getApiService();
    }

    // gom đủ 4 data lịch sử
    public interface HistoryDataCallback {
        void onSuccess(List<LichHen> listLH,
                       List<ChiTietLichHen> listCT,
                       List<NhanVien> listNV,
                       List<ServiceResponse.ServiceModel> listDV);

        void onError(String errorMessage);
    }

    // Kéo 4 API cùng lúc
    public void fetchHistoryData(String maKH, HistoryDataCallback callback) {
        final int[] successCount = {0};
        final boolean[] isFailed = {false};

        final List<LichHen>[] lhList = new List[]{new ArrayList<>()};
        final List<ChiTietLichHen>[] ctList = new List[]{new ArrayList<>()};
        final List<NhanVien>[] nvList = new List[]{new ArrayList<>()};
        final List<ServiceResponse.ServiceModel>[] dvList = new List[]{new ArrayList<>()};

        Runnable checkDone = () -> {
            if (isFailed[0]) return;
            successCount[0]++;
            if (successCount[0] == 4) {
                callback.onSuccess(lhList[0], ctList[0], nvList[0], dvList[0]);
            }
        };

        Runnable throwError = () -> {
            if (!isFailed[0]) {
                isFailed[0] = true;
                callback.onError("Lỗi tải dữ liệu lịch sử từ máy chủ!");
            }
        };

        // 1. Kéo Lịch Hẹn Của Khách Này
        apiService.getLichHenByKhachHang(maKH).enqueue(new Callback<LichHen.LichHenRes>() {
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

        // 2. Kéo Chi Tiết
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

        // 3. Kéo Thợ
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

        // 4. Kéo Dịch Vụ
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

    // Báo cho UI khi Hủy lịch xong
    public interface CancelCallback {
        void onSuccess();

        void onError(String msg);
    }

    // logic Hủy lịch
    public void cancelBooking(String maLich, CancelCallback callback) {
        UpdateStatusRequest request = new UpdateStatusRequest("Đã huỷ");
        apiService.updateBookingStatus(maLich, request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                } else {
                    callback.onError("Hủy thất bại, vui lòng thử lại!");
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                callback.onError("Lỗi mạng!");
            }
        });
    }
}
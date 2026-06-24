package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.HistoryAdapter;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.MasterDataResponse;
import com.example.quanlydatlich.model.UpdateStatusRequest;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ImageView btnBackHistory;
    private HistoryAdapter adapter;

    private List<MasterDataResponse.LichHen> listLichHen = new ArrayList<>();
    private List<MasterDataResponse.ChiTiet> listChiTiet = new ArrayList<>();
    private List<MasterDataResponse.NhanVien> listNhanVien = new ArrayList<>();
    private com.example.quanlydatlich.model.ServiceResponse listDichVuResponse; // Hứng full cục dịch vụ
    private List<com.example.quanlydatlich.model.ServiceResponse.ServiceModel> listDichVu = new ArrayList<>();
    private String currentMaTK = ""; // Chứa mã tài khoản/khách hàng đang đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_history); // Bro nhớ tạo file XML layout này nhé

        // Lò xo chống tràn viền
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Mở két sắt lấy CMND của người dùng hiện tại
        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        currentMaTK = prefs.getString("MATK", "");

        initViews();

        // Có chìa khóa thì bắt đầu gọi API kéo Data về
        if (!currentMaTK.isEmpty()) {
            fetchHistoryData();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        btnBackHistory = findViewById(R.id.btnBackHistory);

        // Setup RecyclerView dạng danh sách dọc
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        btnBackHistory.setOnClickListener(v -> finish());
    }

    // ================= BỘ NÃO XỬ LÝ GỌI DATA =================
    private void fetchHistoryData() {
        ApiService api = RetrofitClient.getApiService();

        // 1. Kéo toàn bộ Lịch Hẹn của CÁ NHÂN ông khách này
        api.getLichHenByKhachHang(currentMaTK).enqueue(new retrofit2.Callback<MasterDataResponse.LichHenRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.LichHenRes> call, retrofit2.Response<MasterDataResponse.LichHenRes> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listLichHen = response.body().data;

                    // Lấy xong Lịch Hẹn thì gọi tiếp API kéo Chi Tiết (để tính tiền)
                    fetchChiTietLichHen(api);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<MasterDataResponse.LichHenRes> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChiTietLichHen(ApiService api) {
        // 2. Kéo toàn bộ Chi tiết
        api.getAllChiTietLichHen().enqueue(new retrofit2.Callback<MasterDataResponse.ChiTietRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.ChiTietRes> call, retrofit2.Response<MasterDataResponse.ChiTietRes> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listChiTiet = response.body().data;
                    fetchNhanVien(api); // 💡 Kéo xong chi tiết thì kéo tiếp Nhân viên
                }
            }
            @Override public void onFailure(retrofit2.Call<MasterDataResponse.ChiTietRes> call, Throwable t) { }
        });
    }

    private void fetchNhanVien(ApiService api) {
        // 3. Kéo toàn bộ Nhân viên (để lấy Tên)
        api.getAllNhanVien().enqueue(new retrofit2.Callback<MasterDataResponse.NhanVienRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.NhanVienRes> call, retrofit2.Response<MasterDataResponse.NhanVienRes> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listNhanVien = response.body().data;
                    fetchDichVu(api); // 💡 Kéo xong Thợ thì kéo nốt Dịch vụ
                }
            }
            @Override public void onFailure(retrofit2.Call<MasterDataResponse.NhanVienRes> call, Throwable t) { }
        });
    }

    private void fetchDichVu(ApiService api) {
        // 4. Kéo toàn bộ Dịch vụ (để lấy Tên dịch vụ)
        api.getServicesAll().enqueue(new retrofit2.Callback<com.example.quanlydatlich.model.ServiceResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.quanlydatlich.model.ServiceResponse> call, retrofit2.Response<com.example.quanlydatlich.model.ServiceResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listDichVu = response.body().getData();

                    // 💡 ĐỦ 4 MÓN ĂN CHƠI RỒI -> DỌN MÂM LÊN GIAO DIỆN!
                    setupAdapter();
                }
            }
            @Override public void onFailure(retrofit2.Call<com.example.quanlydatlich.model.ServiceResponse> call, Throwable t) { }
        });
    }

    private void setupAdapter() {
        // Truyền thêm listNhanVien và listDichVu sang Adapter để tra cứu Tên
        adapter = new HistoryAdapter(listLichHen, listChiTiet, listNhanVien, listDichVu, new HistoryAdapter.OnItemCancelListener() {
            @Override
            public void onCancelClick(String maLich) {
                callApiHuyLich(maLich);
            }
        });
        rvHistory.setAdapter(adapter);
    }

    // ================= SỰ KIỆN HỦY LỊCH CỦA BRO =================
    private void callApiHuyLich(String maLich) {
        // Đúc khuôn request trạng thái
        UpdateStatusRequest request = new UpdateStatusRequest("Đã huỷ");

        ApiService api = RetrofitClient.getApiService();
        api.updateBookingStatus(maLich, request).enqueue(new retrofit2.Callback<BookingResponse>() {
            @Override
            public void onResponse(retrofit2.Call<BookingResponse> call, retrofit2.Response<BookingResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Toast.makeText(HistoryActivity.this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                    // 💡 Load lại danh sách để nó cập nhật màu ĐỎ và ẩn nút Hủy đi
                    fetchHistoryData();
                } else {
                    Toast.makeText(HistoryActivity.this, "Hủy thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<BookingResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
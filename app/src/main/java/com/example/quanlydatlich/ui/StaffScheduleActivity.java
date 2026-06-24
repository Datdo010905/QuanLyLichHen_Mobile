package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.StaffScheduleAdapter;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.StaffBookingResponse;
import com.example.quanlydatlich.model.UpdateStatusRequest;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffScheduleActivity extends AppCompatActivity {

    private RecyclerView rvStaffSchedule;
    private StaffScheduleAdapter adapter;

    private List<StaffBookingResponse.StaffBooking> listLichLamViec = new ArrayList<>();
    private List<ServiceResponse.ServiceModel> listDichVu = new ArrayList<>(); // Cuốn từ điển dịch vụ
    private String currentMaTK = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_schedule);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        rvStaffSchedule = findViewById(R.id.rvStaffSchedule);
        rvStaffSchedule.setLayoutManager(new LinearLayoutManager(this));

        // Mở két lấy mã Thợ
        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        currentMaTK = prefs.getString("MATK", "");

        if (!currentMaTK.isEmpty()) {
            // Quy trình: Kéo Dịch Vụ trước (để dịch tên) -> Xong mới kéo Lịch
            fetchDichVuData();
        } else {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 1. Kéo từ điển Dịch vụ
    private void fetchDichVuData() {
        ApiService api = RetrofitClient.getApiService();
        api.getServicesAll().enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listDichVu = response.body().getData();
                    fetchLichLamViec(currentMaTK); // Có từ điển rồi thì kéo lịch về
                }
            }
            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {}
        });
    }

    // 2. Kéo lịch của ông Thợ này
    private void fetchLichLamViec(String maTK) {
        ApiService api = RetrofitClient.getApiService();
        api.getLichHenCuaNhanVien(maTK).enqueue(new Callback<StaffBookingResponse>() {
            @Override
            public void onResponse(Call<StaffBookingResponse> call, Response<StaffBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listLichLamViec = response.body().data;

                    if (listLichLamViec.isEmpty()) {
                        Toast.makeText(StaffScheduleActivity.this, "Hôm nay không có lịch!", Toast.LENGTH_SHORT).show();
                    }

                    // 💡 Đủ đồ chơi rồi, lên mâm!
                    setupAdapter();
                }
            }
            @Override
            public void onFailure(Call<StaffBookingResponse> call, Throwable t) {
                Toast.makeText(StaffScheduleActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter() {
        adapter = new StaffScheduleAdapter(listLichLamViec, listDichVu, new StaffScheduleAdapter.OnActionClickListener() {
            @Override
            public void onUpdateStatus(String maLich, String trangThaiMoi) {
                // Thợ bấm nút là nổ hàm này!
                capNhatTrangThaiLich(maLich, trangThaiMoi);
            }
        });
        rvStaffSchedule.setAdapter(adapter);
    }

    // 3. Bắn lệnh lên CSDL (Chung API update với nút Hủy của luồng Khách hàng)
    private void capNhatTrangThaiLich(String maLich, String trangThaiMoi) {
        UpdateStatusRequest request = new UpdateStatusRequest(trangThaiMoi);
        ApiService api = RetrofitClient.getApiService();

        api.updateBookingStatus(maLich, request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(StaffScheduleActivity.this, "Đã đổi trạng thái thành " + trangThaiMoi, Toast.LENGTH_SHORT).show();
                    // Load lại Data để thẻ nó ẩn nút đi và đổi màu
                    fetchLichLamViec(currentMaTK);
                } else {
                    Toast.makeText(StaffScheduleActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(StaffScheduleActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
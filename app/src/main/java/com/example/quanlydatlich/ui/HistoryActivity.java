package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.repository.HistoryRepository;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private ImageView btnBackHistory;
    private HistoryAdapter adapter;
    private String currentMaTK = "";

    //Gọi Kho
    private HistoryRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new HistoryRepository();

        // Mở két sắt
        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        currentMaTK = prefs.getString("MATK", "");

        initViews();

        if (!currentMaTK.isEmpty()) {
            fetchHistoryData();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        btnBackHistory = findViewById(R.id.btnBackHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        btnBackHistory.setOnClickListener(v -> finish());
    }

    // ================= KÉO DATA TỪ KHO =================
    private void fetchHistoryData() {
        repository.fetchHistoryData(currentMaTK, new HistoryRepository.HistoryDataCallback() {
            @Override
            public void onSuccess(List<LichHen> lh, List<ChiTietLichHen> ct, List<NhanVien> nv, List<ServiceResponse.ServiceModel> dv) {
                // Có đồ rồi thì cho Adapter lắp ráp
                setupAdapter(lh, ct, nv, dv);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HistoryActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<LichHen> lh, List<ChiTietLichHen> ct, List<NhanVien> nv, List<ServiceResponse.ServiceModel> dv) {
        adapter = new HistoryAdapter(lh, ct, nv, dv, maLich -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận hủy lịch")
                    .setMessage("Có chắc chắn muốn hủy lịch hẹn [" + maLich.trim() + "] này không?")
                    .setPositiveButton("Hủy lịch", (dialog, which) -> {
                        // Gọi API Hủy qua Repository
                        callApiHuyLich(maLich);
                    })
                    .setNegativeButton("Giữ lại", null)
                    .show();
        });
        rvHistory.setAdapter(adapter);
    }

    // ================= XỬ LÝ HỦY LỊCH =================
    private void callApiHuyLich(String maLich) {
        repository.cancelBooking(maLich, new HistoryRepository.CancelCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(HistoryActivity.this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
                // Load lại danh sách để cập nhật màu ĐỎ
                fetchHistoryData();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(HistoryActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
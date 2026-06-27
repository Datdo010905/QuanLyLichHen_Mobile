package com.example.quanlydatlich.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvStartDate, tvEndDate;
    private ImageButton btnFilterDate;
    private ImageView btnBackHistory;
    private HistoryAdapter adapter;
    private String currentMaTK = "";

    //Gọi Kho
    private HistoryRepository repository;
    // CACHE: lọc mà không cần gọi lại API
    private List<LichHen> originalLhList = new ArrayList<>();
    private List<ChiTietLichHen> originalCtList = new ArrayList<>();
    private List<NhanVien> originalNvList = new ArrayList<>();
    private List<ServiceResponse.ServiceModel> originalDvList = new ArrayList<>();

    // Format ngày chuẩn để so sánh (Phù hợp với DatePicker)
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        // Gắn sự kiện chọn ngày
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));

        // Gắn sự kiện lọc
        btnFilterDate.setOnClickListener(v -> filterHistoryByDate());
    }
    // ================= HIỂN THỊ LỊCH ĐỂ CHỌN NGÀY =================
    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Android đếm tháng từ 0 nên phải +1
            calendar.set(year, month, dayOfMonth);
            targetTextView.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void filterHistoryByDate() {
        String startStr = tvStartDate.getText().toString();
        String endStr = tvEndDate.getText().toString();

        // 1. Kiểm tra rỗng
        if (startStr.isEmpty() || endStr.isEmpty() || startStr.equals("Từ ngày") || endStr.equals("Đến ngày")) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc!", Toast.LENGTH_SHORT).show();
            // Nếu bỏ trống thì hiển thị lại toàn bộ danh sách gốc
            setupAdapter(originalLhList, originalCtList, originalNvList, originalDvList);
            return;
        }

        try {
            Date startDate = dateFormat.parse(startStr);
            Date endDate = dateFormat.parse(endStr);

            // Xử lý nới rộng endDate đến cuối ngày (23:59:59) để bao trọn các lịch hẹn trong ngày đó
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endDate = endCal.getTime();

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Tiến hành lọc danh sách
            List<LichHen> filteredList = new ArrayList<>();

            //Tạo format chuẩn để hứng 10 ký tự đầu tiên của chuỗi (yyyy-MM-dd)
            SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (LichHen lh : originalLhList) {
                try {
                    if (lh.ngayHen == null || lh.ngayHen.length() < 10) continue;

                    // Cắt đúng 10 ký tự đầu tiên của chuỗi NGAYHEN (VD: từ "2026-06-27T09:00:00Z" -> "2026-06-27")
                    String dateOnly = lh.ngayHen.substring(0, 10);
                    Date apiDate = apiDateFormat.parse(dateOnly);

                    // Nếu ngày hẹn nằm trong khoảng từ Start đến End
                    if (apiDate != null && !apiDate.before(startDate) && !apiDate.after(endDate)) {
                        filteredList.add(lh);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Bỏ qua lịch bị lỗi định dạng ngày
                }
            }

            // 3. Đổ danh sách đã lọc vào Adapter
            setupAdapter(filteredList, originalCtList, originalNvList, originalDvList);

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Không có lịch hẹn nào trong khoảng thời gian này!", Toast.LENGTH_SHORT).show();
            }else {
                // Báo số lượng lịch tìm thấy
                Toast.makeText(this, "Tìm thấy " + filteredList.size() + " lịch hẹn", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show();
        }
    }
    // ================= KÉO DATA TỪ KHO =================
    private void fetchHistoryData() {
        repository.fetchHistoryData(currentMaTK, new HistoryRepository.HistoryDataCallback() {
            @Override
            public void onSuccess(List<LichHen> lh, List<ChiTietLichHen> ct, List<NhanVien> nv, List<ServiceResponse.ServiceModel> dv) {
                //lưu lại danh sách để lọc
                originalLhList = lh;
                originalCtList = ct;
                originalNvList = nv;
                originalDvList = dv;
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
                        tvStartDate.setText("Từ ngày");
                        tvEndDate.setText("Đến ngày");

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
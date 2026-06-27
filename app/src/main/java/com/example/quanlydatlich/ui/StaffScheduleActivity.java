package com.example.quanlydatlich.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.StaffScheduleAdapter;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.StaffBookingResponse;
import com.example.quanlydatlich.repository.StaffRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffScheduleActivity extends AppCompatActivity {
    private RecyclerView rvStaffSchedule;
    private StaffRepository repository;
    private TextView tvStartDate, tvEndDate;
    private ImageButton btnFilterDate;
    private String currentMaTK;
    private ImageView btnLogout;
    // Lưu dữ liệu gốc để lọc
    private List<StaffBookingResponse.StaffBooking> originalLichList = new ArrayList<>();
    private List<ServiceResponse.ServiceModel> originalDvList = new ArrayList<>();

    // Format ngày chuẩn
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule);
        EdgeToEdge.enable(this);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        rvStaffSchedule = findViewById(R.id.rvStaffSchedule);
        rvStaffSchedule.setLayoutManager(new LinearLayoutManager(this));

        repository = new StaffRepository();
        currentMaTK = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE)
                .getString("MATK", "");

        if (!currentMaTK.isEmpty()) {
            loadData();
        } else {
            finish();
        }

        // 2. Gắn sự kiện Bộ lọc
        tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));
        btnFilterDate.setOnClickListener(v -> filterScheduleByDate());


        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StaffScheduleActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Ở lại", null)
                    .show();
        });

    }


    // ================= HIỂN THỊ LỊCH ĐỂ CHỌN NGÀY =================
    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            //đưa ngày đã chọn vào TextView
            targetTextView.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ================= LỌC NGÀY =================
    private void filterScheduleByDate() {
        String startStr = tvStartDate.getText().toString();
        String endStr = tvEndDate.getText().toString();

        if (startStr.isEmpty() || endStr.isEmpty() || startStr.equals("Từ ngày") || endStr.equals("Đến ngày")) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc!", Toast.LENGTH_SHORT).show();
            setupAdapter(originalLichList, originalDvList); // Reset về danh sách gốc
            return;
        }

        try {
            Date startDate = dateFormat.parse(startStr);
            Date endDate = dateFormat.parse(endStr);

            // Kéo dài endDate đến 23:59:59 để bao trọn ngày
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endDate = endCal.getTime();

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Ngày bắt đầu không lớn hơn ngày kết thúc!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<StaffBookingResponse.StaffBooking> filteredList = new ArrayList<>();

            for (StaffBookingResponse.StaffBooking booking : originalLichList) {
                try {
                    if (booking.ngayHen == null || booking.ngayHen.length() < 10) continue;

                    // lấy 10 ký tự đầu tiên (yyyy-MM-dd)
                    String dateOnly = booking.ngayHen.substring(0, 10);
                    Date apiDate = apiDateFormat.parse(dateOnly);

                    if (apiDate != null && !apiDate.before(startDate) && !apiDate.after(endDate)) {
                        filteredList.add(booking);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            setupAdapter(filteredList, originalDvList);

            //Thông báo số lượng lịch tìm thấy
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Không có lịch làm việc nào trong khoảng thời gian này!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tìm thấy " + filteredList.size() + " lịch làm việc", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadData() {
        repository.fetchStaffData(currentMaTK, new StaffRepository.StaffDataCallback() {
            @Override
            public void onSuccess(List<StaffBookingResponse.StaffBooking> listLich, List<ServiceResponse.ServiceModel> listDV) {
                //lưu ds gốc
                originalLichList = listLich;
                originalDvList = listDV;

                setupAdapter(listLich, listDV);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(StaffScheduleActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<StaffBookingResponse.StaffBooking> listLich, List<ServiceResponse.ServiceModel> listDV) {
        StaffScheduleAdapter adapter = new StaffScheduleAdapter(listLich, listDV, (maLich, trangThai) -> {
            repository.updateStatus(maLich, trangThai, new Callback<BookingResponse>() {
                @Override
                public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                    Toast.makeText(StaffScheduleActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    tvStartDate.setText("Từ ngày");
                    tvEndDate.setText("Đến ngày");

                    loadData(); // Refresh lại danh sách
                }

                @Override
                public void onFailure(Call<BookingResponse> call, Throwable t) {
                    Toast.makeText(StaffScheduleActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvStaffSchedule.setAdapter(adapter);
    }
}
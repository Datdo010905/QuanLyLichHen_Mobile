package com.example.quanlydatlich.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.MasterDataResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private Spinner spnChiNhanh, spnThoCat, spnDichVu, spnGioHen;
    private TextView tvChonNgay;
    private EditText edtGhiChu;
    private Button btnDatLichNgay;
    private ImageView btnBackBooking;

    // Lưu trữ lựa chọn của khách
    private String selectedChiNhanhId = "";
    private String selectedThoCatId = "";
    private String selectedDichVuId = "";
    private String selectedNgay = ""; // Format: yyyy-MM-dd
    private String selectedGio = "";

    // Chứa Data thật từ API
    private List<MasterDataResponse.NhanVien> listNhanVien = new ArrayList<>();
    private List<MasterDataResponse.LichHen> listLichHen = new ArrayList<>();
    private List<MasterDataResponse.ChiTiet> listChiTiet = new ArrayList<>();
    private List<ServiceResponse.ServiceModel> listDichVu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        fetchRealData();
        setupChiNhanhSpinner();
        setupEvents(); // 💡 QUAN TRỌNG: Gọi hàm kích hoạt chọn Ngày/Giờ
    }

    private void initViews() {
        spnChiNhanh = findViewById(R.id.spnChiNhanh);
        spnThoCat = findViewById(R.id.spnThoCat);
        spnDichVu = findViewById(R.id.spnDichVu);
        spnGioHen = findViewById(R.id.spnGioHen);
        tvChonNgay = findViewById(R.id.tvChonNgay);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        btnDatLichNgay = findViewById(R.id.btnDatLichNgay);
        btnBackBooking = findViewById(R.id.btnBackBooking);

        btnBackBooking.setOnClickListener(v -> finish());

        // 💡 SỰ KIỆN CHỐT ĐƠN: BÓC DATA -> ĐÚC KHUÔN -> GỌI API
        btnDatLichNgay.setOnClickListener(v -> {

            // 1. Kiểm tra xem khách đã chọn đủ các bước chưa (Validation)
            if (selectedChiNhanhId.isEmpty() || selectedThoCatId.isEmpty() ||
                    selectedDichVuId.isEmpty() || selectedNgay.isEmpty() || selectedGio.isEmpty()) {
                Toast.makeText(BookingActivity.this, "Vui lòng chọn đầy đủ thông tin các bước!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Mở két sắt lấy thông tin người đặt (Mã Khách Hàng / Mã Tài Khoản)
            android.content.SharedPreferences prefs = getSharedPreferences("ThongTinKhach", android.content.Context.MODE_PRIVATE);
            String maKH = prefs.getString("MATK", "");

            if (maKH.isEmpty()) {
                Toast.makeText(BookingActivity.this, "Lỗi: Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Khởi tạo dữ liệu tự động
            // Tạo mã lịch duy nhất (VD: LH17032482394)
            String maLich = "LH" + System.currentTimeMillis();
            String ghiChu = edtGhiChu.getText().toString().trim();

            // Tìm giá tiền dự kiến của dịch vụ khách vừa chọn
            double giaDuKien = 0;
            for (ServiceResponse.ServiceModel dv : listDichVu) {
                if (dv.getMaDV().equals(selectedDichVuId)) {
                    giaDuKien = dv.getGia();
                    break;
                }
            }

            // 4. Đúc Khuôn gửi đi (Khớp 100% với Backend Node.js)
            com.example.quanlydatlich.model.BookingRequest.BookingInfo info =
                    new com.example.quanlydatlich.model.BookingRequest.BookingInfo(maLich, selectedNgay, selectedGio, selectedChiNhanhId, maKH);

            com.example.quanlydatlich.model.BookingRequest.BookingDetails details =
                    new com.example.quanlydatlich.model.BookingRequest.BookingDetails(maLich, selectedDichVuId, selectedThoCatId, giaDuKien, ghiChu);

            com.example.quanlydatlich.model.BookingRequest requestPayload =
                    new com.example.quanlydatlich.model.BookingRequest(info, details);

            // 5. Khóa nút lại để tránh khách spam click (bấm nhiều lần sinh ra 2-3 bill)
            btnDatLichNgay.setEnabled(false);
            btnDatLichNgay.setText("ĐANG XỬ LÝ...");

            // 6. Gọi Thợ săn đi giao hàng!
            com.example.quanlydatlich.repository.BookingRepository repo = new com.example.quanlydatlich.repository.BookingRepository();
            repo.submitBooking(requestPayload, new com.example.quanlydatlich.repository.BookingRepository.BookingCallback() {

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(BookingActivity.this, "🎉 " + message, Toast.LENGTH_LONG).show();

                    // Đóng màn hình Đặt Lịch, khách sẽ tự động rớt về màn Home hoặc Lịch sử
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(BookingActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    // Mở khóa nút để khách thử lại
                    btnDatLichNgay.setEnabled(true);
                    btnDatLichNgay.setText("ĐẶT LỊCH NGAY");
                }
            });
        });
    }

    // ================= KÉO DATA TỪ API =================
    private void fetchRealData() {
        ApiService api = RetrofitClient.getApiService();

        api.getAllNhanVien().enqueue(new retrofit2.Callback<MasterDataResponse.NhanVienRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.NhanVienRes> call, retrofit2.Response<MasterDataResponse.NhanVienRes> response) {
                if(response.isSuccessful() && response.body() != null) listNhanVien = response.body().data;
            }
            @Override public void onFailure(retrofit2.Call<MasterDataResponse.NhanVienRes> call, Throwable t) {}
        });

        api.getAllLichHen().enqueue(new retrofit2.Callback<MasterDataResponse.LichHenRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.LichHenRes> call, retrofit2.Response<MasterDataResponse.LichHenRes> response) {
                if(response.isSuccessful() && response.body() != null) listLichHen = response.body().data;
            }
            @Override public void onFailure(retrofit2.Call<MasterDataResponse.LichHenRes> call, Throwable t) {}
        });

        api.getAllChiTietLichHen().enqueue(new retrofit2.Callback<MasterDataResponse.ChiTietRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.ChiTietRes> call, retrofit2.Response<MasterDataResponse.ChiTietRes> response) {
                if(response.isSuccessful() && response.body() != null) listChiTiet = response.body().data;
            }
            @Override public void onFailure(retrofit2.Call<MasterDataResponse.ChiTietRes> call, Throwable t) {}
        });

        api.getServicesAll().enqueue(new retrofit2.Callback<ServiceResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ServiceResponse> call, retrofit2.Response<ServiceResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listDichVu = response.body().getData();
                    setupDichVuSpinner();
                }
            }
            @Override public void onFailure(retrofit2.Call<ServiceResponse> call, Throwable t) {}
        });
    }

    // ================= BƯỚC 1: LOAD CHI NHÁNH CỨNG =================
    private void setupChiNhanhSpinner() {
        String[] arrChiNhanh = {"-- Chọn chi nhánh --", "30Shine - Nguyễn Trãi (CN001)", "30Shine - Cầu Giấy (CN002)", "30Shine - Tân Bình (CN003)", "30Shine - Đà Nẵng (CN004)"};
        String[] arrChiNhanhIds = {"", "CN001", "CN002", "CN003", "CN004"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrChiNhanh);
        spnChiNhanh.setAdapter(adapter);

        spnChiNhanh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChiNhanhId = arrChiNhanhIds[position];
                selectedThoCatId = "";
                updateThoCatSpinner();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ================= BƯỚC 2: LỌC THỢ CẮT THEO DATA THẬT =================
    private void updateThoCatSpinner() {
        List<String> displayNames = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        displayNames.add("-- Chọn thợ cắt tóc --");
        ids.add("");

        if (listNhanVien != null) {
            for (MasterDataResponse.NhanVien nv : listNhanVien) {
                // 💡 Bọc check null kỹ càng đề phòng API lỗi
                if (nv.maChiNhanh != null && nv.maChiNhanh.trim().equals(selectedChiNhanhId)
                        && nv.chucVu != null && nv.chucVu.trim().equals("Stylist")) {
                    displayNames.add(nv.maNV + " - " + nv.hoTen + " (" + nv.sdt + ")");
                    ids.add(nv.maNV);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayNames);
        spnThoCat.setAdapter(adapter);
        spnThoCat.setEnabled(!selectedChiNhanhId.isEmpty());

        spnThoCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedThoCatId = ids.get(position);
                calculateAvailableHours();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ================= BƯỚC 3: ĐỔ DATA DỊCH VỤ =================
    private void setupDichVuSpinner() {
        List<String> displayNames = new ArrayList<>();
        displayNames.add("-- Chọn dịch vụ --");

        int viTriCanChon = 0; // Biến đánh dấu vị trí cần tự động cuộn tới

        // 💡 Hứng mã dịch vụ từ màn Chi Tiết ném sang (nếu đi bằng nút bình thường thì nó sẽ = null)
        String maDVCanXem = getIntent().getStringExtra("MADV_CAN_XEM");

        for (int i = 0; i < listDichVu.size(); i++) {
            ServiceResponse.ServiceModel dv = listDichVu.get(i);
            displayNames.add(dv.getName() + " - " + dv.getThoiGian() + " phút - " + (int)dv.getGia() + " VNĐ");

            // Nếu mã dịch vụ trên CSDL khớp với mã ném sang -> Chốt vị trí (Cộng 1 vì dòng đầu là "-- Chọn dịch vụ --")
            if (maDVCanXem != null && dv.getMaDV().trim().equals(maDVCanXem.trim())) {
                viTriCanChon = i + 1;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayNames);
        spnDichVu.setAdapter(adapter);

        // 💡 MA THUẬT: TỰ ĐỘNG CHỌN SẴN DỊCH VỤ TRÊN SPINNER
        if (viTriCanChon > 0) {
            spnDichVu.setSelection(viTriCanChon);
            selectedDichVuId = maDVCanXem; // Cập nhật luôn biến cục bộ để lát chốt đơn không bị báo lỗi thiếu
        }

        spnDichVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) selectedDichVuId = listDichVu.get(position - 1).getMaDV();
                else selectedDichVuId = "";
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ================= BƯỚC 4: CHỌN NGÀY VÀ GIỜ =================
    private void setupEvents() {
        tvChonNgay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(BookingActivity.this, (view, year, month, dayOfMonth) -> {
                selectedNgay = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvChonNgay.setText(selectedNgay);
                calculateAvailableHours();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        spnGioHen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) selectedGio = parent.getItemAtPosition(position).toString();
                else selectedGio = "";
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ================= BƯỚC 5: TÍNH TOÁN GIỜ TRỐNG =================
    private void calculateAvailableHours() {
        List<String> hoursList = new ArrayList<>();
        hoursList.add("-- Chọn giờ hẹn --");

        if (selectedThoCatId.isEmpty() || selectedNgay.isEmpty()) {
            updateGioHenSpinner(hoursList);
            return;
        }

        // Tìm MALICH của thợ
        List<String> bookedIdsForStaff = new ArrayList<>();
        if (listChiTiet != null) {
            for (MasterDataResponse.ChiTiet ct : listChiTiet) {
                if (ct.maNV != null && ct.maNV.trim().equals(selectedThoCatId)) {
                    bookedIdsForStaff.add(ct.maLich != null ? ct.maLich.trim() : "");
                }
            }
        }

        // Lọc giờ đã đặt
        List<String> bookedHours = new ArrayList<>();
        if (listLichHen != null) {
            for (MasterDataResponse.LichHen lh : listLichHen) {
                if (lh.ngayHen == null || lh.trangThai == null || lh.maLich == null) continue;

                boolean isTrungNgay = lh.ngayHen.startsWith(selectedNgay);
                boolean isChuaHuy = !lh.trangThai.trim().equals("Đã huỷ");
                boolean isChuaHoanThanh = !lh.trangThai.trim().equals("Đã hoàn thành");
                boolean isNVDuocChon = bookedIdsForStaff.contains(lh.maLich.trim());

                if (isTrungNgay && isChuaHuy && isChuaHoanThanh && isNVDuocChon) {
                    try {
                        String time = lh.gioHen.split("T")[1].substring(0, 5);
                        bookedHours.add(time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Đục lỗ tạo giờ trống
        for (int h = 8; h <= 22; h++) {
            for (int m : new int[]{0, 30}) {
                String timeSlot = String.format("%02d:%02d", h, m);
                if (!bookedHours.contains(timeSlot)) {
                    hoursList.add(timeSlot);
                }
            }
        }

        updateGioHenSpinner(hoursList);
    }

    private void updateGioHenSpinner(List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spnGioHen.setAdapter(adapter);
        spnGioHen.setEnabled(data.size() > 1);
    }
}
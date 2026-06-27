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
import com.example.quanlydatlich.model.BookingRequest;
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.repository.BookingRepository;
import com.example.quanlydatlich.helper.BookingHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private Spinner spnChiNhanh, spnThoCat, spnDichVu, spnGioHen;
    private TextView tvChonNgay;
    private EditText edtGhiChu;
    private Button btnDatLichNgay;
    private ImageView btnBackBooking;

    private String selectedChiNhanhId = "";
    private String selectedThoCatId = "";
    private String selectedDichVuId = "";
    private String selectedNgay = "";
    private String selectedGio = "";

    // Data hứng từ Kho
    private List<NhanVien> listNhanVien = new ArrayList<>();
    private List<LichHen> listLichHen = new ArrayList<>();
    private List<ChiTietLichHen> listChiTiet = new ArrayList<>();
    private List<ServiceResponse.ServiceModel> listDichVu = new ArrayList<>();

    private BookingRepository repository;

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
        setupChiNhanhSpinner();
        setupEvents();

        //6 gọi kho chứa
        loadDataFromRepository();
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

        repository = new BookingRepository();
        btnBackBooking.setOnClickListener(v -> finish());
        btnDatLichNgay.setOnClickListener(v -> submitBooking());
    }

    // ================= KÉO DATA TỪ KHO =================
    private void loadDataFromRepository() {

        repository.fetchSetupData(new BookingRepository.SetupDataCallback() {
            @Override
            public void onSuccess(List<NhanVien> nv, List<LichHen> lh, List<ChiTietLichHen> ct, List<ServiceResponse.ServiceModel> dv) {
                listNhanVien = nv;
                listLichHen = lh;
                listChiTiet = ct;
                listDichVu = dv;
                setupDichVuSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(BookingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateThoCatSpinner() {
        List<String> displayNames = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        displayNames.add("-- Chọn thợ cắt tóc --");
        ids.add("");

        for (NhanVien nv : listNhanVien) {
            if (nv.maChiNhanh.trim() != null && nv.maChiNhanh.trim().equals(selectedChiNhanhId)
                    && nv.chucVu.trim() != null && nv.chucVu.trim().equals("Stylist")) {
                displayNames.add(nv.maNV.trim() + " - " + nv.hoTen + " (" + nv.sdt + ")");
                ids.add(nv.maNV);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayNames);
        spnThoCat.setAdapter(adapter);
        spnThoCat.setEnabled(!selectedChiNhanhId.isEmpty());

        spnThoCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedThoCatId = ids.get(position);
                refreshGioHen();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDichVuSpinner() {
        List<String> displayNames = new ArrayList<>();
        displayNames.add("-- Chọn dịch vụ --");

        int viTriCanChon = 0;
        String maDVCanXem = getIntent().getStringExtra("MADV_CAN_XEM");

        for (int i = 0; i < listDichVu.size(); i++) {
            ServiceResponse.ServiceModel dv = listDichVu.get(i);
            displayNames.add(dv.getName() + " - " + dv.getThoiGian() + " phút - " + (int) dv.getGia() + " VNĐ");
            if (maDVCanXem != null && dv.getMaDV().trim().equals(maDVCanXem.trim()))
                viTriCanChon = i + 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, displayNames);
        spnDichVu.setAdapter(adapter);

        if (viTriCanChon > 0) {
            spnDichVu.setSelection(viTriCanChon);
            selectedDichVuId = maDVCanXem;
        }

        spnDichVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDichVuId = position > 0 ? listDichVu.get(position - 1).getMaDV() : "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupEvents() {
        tvChonNgay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(BookingActivity.this, (view, year, month, dayOfMonth) -> {
                selectedNgay = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvChonNgay.setText(selectedNgay);
                refreshGioHen();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        spnGioHen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGio = position > 0 ? parent.getItemAtPosition(position).toString() : "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //tính giờ trống
    private void refreshGioHen() {
        //lấy danh sách
        List<String> hoursList = BookingHelper.calculateAvailableHours(selectedThoCatId, selectedNgay, listChiTiet, listLichHen);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hoursList);
        spnGioHen.setAdapter(adapter);

        // Nếu có nhiều hơn 1 phần tử (nghĩa là ngoài chữ "-- Chọn giờ hẹn --" ra thì còn giờ thật)
        boolean hasAvailableHours = hoursList.size() > 1;
        spnGioHen.setEnabled(hasAvailableHours);

        // Chỉ thông báo khi khách ĐÃ chọn thợ và ĐÃ chọn ngày, nhưng danh sách giờ lại trống không
        if (!selectedThoCatId.isEmpty() && !selectedNgay.isEmpty() && !hasAvailableHours) {
            Toast.makeText(this,
                    "⏳ Rất tiếc, thợ cắt tóc này đã kín lịch hoặc đã qua giờ nhận khách trong ngày hôm nay. Bạn vui lòng chọn ngày khác nhé!", Toast.LENGTH_LONG).show();
        }
    }

    //check xác nhận đặt lịch
    private void submitBooking() {
        // 1. Validate dữ liệu
        if (isInvalidInput()) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt lịch")
                .setMessage("Xác nhận đặt lịch với các thông tin đã chọn chứ?")
                .setPositiveButton("Đặt lịch", (dialog, which) -> {
                    // Nếu "Đặt lịch" thì chạy
                    executeBookingProcess();
                })
                .setNegativeButton("Để kiểm tra lại", null)
                .show();
    }
    //đặt lịch
    private void executeBookingProcess() {
        BookingRequest request = createBookingRequest();
        if (request == null) return;

        toggleLoadingState(true);

        repository.submitBooking(request, new BookingRepository.BookingCallback() {
            @Override
            public void onSuccess(String message) {
                // Kiểm tra xem Activity còn sống không trước khi dùng Toast/finish
                if (isFinishing() || isDestroyed()) return;

                Toast.makeText(BookingActivity.this, "🎉 " + message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                // tránh lỗi crash nếu người dùng đã thoát Activity trước khi API trả về
                if (isFinishing() || isDestroyed()) return;

                Toast.makeText(BookingActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                toggleLoadingState(false);
            }
        });
    }

    private boolean isInvalidInput() {
        return selectedChiNhanhId.isEmpty() || selectedThoCatId.isEmpty() ||
                selectedDichVuId.isEmpty() || selectedNgay.isEmpty() || selectedGio.isEmpty();
    }

    private void toggleLoadingState(boolean isLoading) {
        btnDatLichNgay.setEnabled(!isLoading);
        btnDatLichNgay.setText(isLoading ? "ĐANG XỬ LÝ..." : "ĐẶT LỊCH NGAY");
    }

    private BookingRequest createBookingRequest() {
        String maKH = getSharedPreferences("ThongTinKhach", MODE_PRIVATE).getString("MATK", "");
        if (maKH.isEmpty()) return null;

        String maLich = "LH" + System.currentTimeMillis();
        double gia = listDichVu.stream()
                .filter(d -> d.getMaDV().equals(selectedDichVuId))
                .mapToDouble(ServiceResponse.ServiceModel::getGia)
                .findFirst().orElse(0.0);

        BookingRequest.BookingInfo info = new BookingRequest.BookingInfo(maLich, selectedNgay, selectedGio, selectedChiNhanhId, maKH);
        BookingRequest.BookingDetails details = new BookingRequest.BookingDetails(maLich, selectedDichVuId, selectedThoCatId, gia, edtGhiChu.getText().toString().trim());

        return new BookingRequest(info, details);
    }
}
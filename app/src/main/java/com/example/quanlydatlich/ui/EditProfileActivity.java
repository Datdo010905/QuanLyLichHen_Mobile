package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.UpdateProfileRequest;
import com.example.quanlydatlich.repository.ProfileRepository;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtSdtProfile, edtHoTenProfile, edtEmailProfile, edtPassProfile;
    private Button btnLuuProfile;
    private ImageView btnBackProfile;

    private String currentMaTK = "";
    private ProfileRepository repository;
    private String tengoc, emailgoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        repository = new ProfileRepository();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        // 1. Lấy thẻ bài từ két sắt
        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        currentMaTK = prefs.getString("SDT_KHACH", "");

        if (!currentMaTK.isEmpty()) {
            loadDuLieuKhachHang();
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        edtSdtProfile = findViewById(R.id.edtSdtProfile);
        edtHoTenProfile = findViewById(R.id.edtHoTenProfile);
        edtEmailProfile = findViewById(R.id.edtEmailProfile);
        btnLuuProfile = findViewById(R.id.btnLuuProfile);
        btnBackProfile = findViewById(R.id.btnBackProfile);
        edtPassProfile = findViewById(R.id.edtPassProfile);
        btnBackProfile.setOnClickListener(v -> finish());

        // Bắt sự kiện bấm LƯU
        btnLuuProfile.setOnClickListener(v -> updateDuLieuKhachHang());
    }

    // ================= KÉO DATA LÊN GIAO DIỆN =================
    private void loadDuLieuKhachHang() {
        repository.getProfile(currentMaTK, new ProfileRepository.ProfileCallback<KhachHangResponse.KhachHangDetail>() {
            @Override
            public void onSuccess(KhachHangResponse.KhachHangDetail kh) {
                edtSdtProfile.setText(kh.getSdt());
                edtHoTenProfile.setText(kh.getHoTen());
                edtEmailProfile.setText(kh.getEmail() != null ? kh.getEmail() : "");

                tengoc = kh.getHoTen();
                emailgoc = kh.getEmail();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDuLieuKhachHang() {
        //Confirm Dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận lưu thay đổi")
                .setMessage("Bro có muốn lưu thông tin này vào hệ thống không?")
                .setPositiveButton("Lưu ngay", (d, w) -> executeUpdate())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeUpdate() {
        String tenMoi = edtHoTenProfile.getText().toString().trim();
        String emailMoi = edtEmailProfile.getText().toString().trim();
        String passMoi = edtPassProfile.getText().toString().trim();

        //check rỗng
        if (tenMoi.isEmpty() || emailMoi.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        //check chưa thay đổi
        if (tengoc.equals(tenMoi) && emailgoc.equals(emailMoi) && passMoi.isEmpty()) {
            Toast.makeText(this, "Không có thay đổi nào!", Toast.LENGTH_SHORT).show();
            return;
        }


        btnLuuProfile.setEnabled(false);
        btnLuuProfile.setText("ĐANG LƯU...");

        UpdateProfileRequest request = new UpdateProfileRequest(
                new UpdateProfileRequest.CustomerData(tenMoi, emailMoi),
                passMoi.isEmpty() ? null : new UpdateProfileRequest.AccountData(passMoi)
        );

        repository.updateProfile(currentMaTK, request, new ProfileRepository.ProfileCallback<String>() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(EditProfileActivity.this, "🎉 " + msg, Toast.LENGTH_SHORT).show();
                getSharedPreferences("ThongTinKhach", MODE_PRIVATE).edit().putString("TENKHACH", tenMoi).apply();
                tengoc = tenMoi;
                emailgoc = emailMoi;

                finish();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                btnLuuProfile.setEnabled(true);
                btnLuuProfile.setText("LƯU THAY ĐỔI");
            }
        });
    }
}
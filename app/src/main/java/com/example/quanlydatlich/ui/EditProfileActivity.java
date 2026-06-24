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
import com.example.quanlydatlich.model.MasterDataResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtSdtProfile, edtHoTenProfile, edtEmailProfile, edtPassProfile;
    private Button btnLuuProfile;
    private ImageView btnBackProfile;

    private String currentMaTK = "";
    private String currentMaKH = ""; // 💡 Biến quan trọng để biết đang sửa khách hàng nào

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);

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
        ApiService api = RetrofitClient.getApiService();
        api.getKhachHangById(currentMaTK).enqueue(new Callback<MasterDataResponse.KhachHangRes>() {
            @Override
            public void onResponse(Call<MasterDataResponse.KhachHangRes> call, Response<MasterDataResponse.KhachHangRes> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    MasterDataResponse.KhachHang kh = response.body().data;

                    // Ghi nhận Mã Khách Hàng để lát nữa gọi hàm Update
                    currentMaKH = kh.maKH;

                    // Đắp data lên các ô text
                    edtSdtProfile.setText(kh.sdt);
                    edtHoTenProfile.setText(kh.hoTen);
                    edtEmailProfile.setText(kh.email != null ? kh.email : "");

                    // Lưu luôn tên mới vào SharedPreferences lỡ lát khách ra trang chủ nó hiện Tên cập nhật luôn
                    SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
                    prefs.edit().putString("TENKHACH", kh.hoTen).apply();

                } else {
                    Toast.makeText(EditProfileActivity.this, "Không kéo được dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MasterDataResponse.KhachHangRes> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= CHỐT CẬP NHẬT GỬI LÊN NODE.JS =================
    private void updateDuLieuKhachHang() {
        String tenMoi = edtHoTenProfile.getText().toString().trim();
        String emailMoi = edtEmailProfile.getText().toString().trim();
        String passMoi = edtPassProfile.getText().toString().trim(); // Lấy pass mới

        if (tenMoi.isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLuuProfile.setEnabled(false);
        btnLuuProfile.setText("ĐANG LƯU...");

        // 💡 1. Tạo gói Thông tin khách hàng
        com.example.quanlydatlich.model.UpdateProfileRequest.CustomerData customerData =
                new com.example.quanlydatlich.model.UpdateProfileRequest.CustomerData(tenMoi, emailMoi);

        // 💡 2. Tạo gói Tài khoản (Nếu có nhập pass thì tạo, không thì để null cho Backend bỏ qua)
        com.example.quanlydatlich.model.UpdateProfileRequest.AccountData accountData = null;
        if (!passMoi.isEmpty()) {
            accountData = new com.example.quanlydatlich.model.UpdateProfileRequest.AccountData(passMoi);
        }

        // 💡 3. Gộp 2 gói thành Payload tổng
        com.example.quanlydatlich.model.UpdateProfileRequest requestPayload =
                new com.example.quanlydatlich.model.UpdateProfileRequest(customerData, accountData);

        // API Node.js đang nhận req.params.id (Là ID lấy từ Két sắt)
        ApiService api = RetrofitClient.getApiService();
        api.updateKhachHangProfile(currentMaTK, requestPayload).enqueue(new retrofit2.Callback<MasterDataResponse.KhachHangRes>() {
            @Override
            public void onResponse(retrofit2.Call<MasterDataResponse.KhachHangRes> call, retrofit2.Response<MasterDataResponse.KhachHangRes> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "🎉 Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại tên ở Local
                    SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
                    prefs.edit().putString("TENKHACH", tenMoi).apply();

                    // Nếu đổi pass thành công, có thể yêu cầu đăng nhập lại (tùy bro) hoặc cứ đóng trang
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    btnLuuProfile.setEnabled(true);
                    btnLuuProfile.setText("LƯU THAY ĐỔI");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MasterDataResponse.KhachHangRes> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi đường truyền!", Toast.LENGTH_SHORT).show();
                btnLuuProfile.setEnabled(true);
                btnLuuProfile.setText("LƯU THAY ĐỔI");
            }
        });
    }
}
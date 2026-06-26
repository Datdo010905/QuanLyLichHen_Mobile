package com.example.quanlydatlich.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.repository.KhachHangRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginNow = findViewById(R.id.tvLoginNow);

        tvLoginNow.setOnClickListener(v -> {
            finish();
        });

        // Bắt sự kiện click nút đăng ký
        btnRegister.setOnClickListener(v -> executeRegistration());
    }

    private void executeRegistration() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();




        // 1. Kiểm tra đầu vào
        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10 || phone.length() > 11) {
            edtPhone.setError("Số điện thoại không hợp lệ!");
            edtPhone.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Định dạng email không đúng!");
            edtEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
            edtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp!");
            edtConfirmPassword.requestFocus();
            return;
        }

        // 2. Khóa nút bấm, hiện trạng thái loading
        btnRegister.setEnabled(false);
        btnRegister.setText("ĐANG XỬ LÝ...");

        // 3. Gọi API thông qua Repository
        KhachHangRepository repo = new KhachHangRepository();
        repo.registerKhachHang(fullName, phone, email, password, new KhachHangRepository.RegisterCallback() {
            @Override
            public void onSuccess(String message) {
                // Tránh crash nếu Activity đã bị đóng trước khi API phản hồi
                if (isFinishing() || isDestroyed()) return;

                Toast.makeText(RegisterActivity.this, "🎉 " + message, Toast.LENGTH_LONG).show();
                finish(); // Tự động đóng màn hình này để quay lại màn hình Login
            }

            @Override
            public void onError(String errorMessage) {
                if (isFinishing() || isDestroyed()) return;

                Toast.makeText(RegisterActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                // Lỗi thì mở khóa lại nút bấm để khách sửa thông tin và gửi lại
                btnRegister.setEnabled(true);
                btnRegister.setText("ĐĂNG KÝ");
            }
        });
    }
}
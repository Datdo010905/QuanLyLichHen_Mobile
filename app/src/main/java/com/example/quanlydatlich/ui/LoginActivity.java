package com.example.quanlydatlich.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Bạn chưa nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                com.example.quanlydatlich.repository.AuthRepository authRepo = new com.example.quanlydatlich.repository.AuthRepository();

                authRepo.login(username, password, new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message, String tenKhach, String token) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                        //mở két để lưu thông tin đăng nhập tránh mất khi thoát
                        android.content.SharedPreferences prefs = getSharedPreferences("ThongTinKhach", MODE_PRIVATE);
                        android.content.SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("TEN_KHACH", tenKhach);
                        editor.putString("TOKEN", token); // Nhét thẻ JWT vào két
                        editor.apply();

                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }
}
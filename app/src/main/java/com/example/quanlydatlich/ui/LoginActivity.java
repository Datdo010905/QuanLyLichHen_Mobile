package com.example.quanlydatlich.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                        LoginActivity.this,
                        "Bạn chưa nhập đủ thông tin!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            AuthRepository authRepo = new AuthRepository();

            authRepo.login(username, password, new AuthRepository.AuthCallback() {

                @Override
                public void onSuccess(String message, String tenKhach, String token) {

                    Toast.makeText(
                            LoginActivity.this,
                            message,
                            Toast.LENGTH_SHORT
                    ).show();

                    SharedPreferences prefs =
                            getSharedPreferences("ThongTinKhach", MODE_PRIVATE);

                    prefs.edit()
                            .putString("MATK", tenKhach)
                            .putString("TOKEN", token)
                            .apply();

                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(
                            LoginActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });
    }
}
package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quanlydatlich.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnLogin, btnExplore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 1. Ánh xạ View
        btnLogin = findViewById(R.id.btnLogin);
        btnExplore = findViewById(R.id.btnExplore);

        //đã đăng nhập vào thẳng Home
        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        String savedMaTK = prefs.getString("MATK", "");
        if (!savedMaTK.isEmpty()) {
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            finish();
            return; // Dừng hàm onCreate
        }

        //người mới
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnExplore.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
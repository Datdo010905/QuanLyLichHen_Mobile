package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.ServiceAdapter;
import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.network.ApiService;
import com.example.quanlydatlich.network.RetrofitClient;
import com.example.quanlydatlich.repository.KhachHangRepository;;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileNameValue, tvProfilePhoneValue, tvProfileEmailValue;
    private ImageView btnBackProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ View
        tvProfileNameValue = findViewById(R.id.tvProfileNameValue);
        tvProfilePhoneValue = findViewById(R.id.tvProfilePhoneValue);
        tvProfileEmailValue = findViewById(R.id.tvProfileEmailValue);

        btnBackProfile = findViewById(R.id.btnBackProfile);
        btnLogout = findViewById(R.id.btnLogout);


        // Mở Két sắt lấy Data
        SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        String hoten = sharedPreferences.getString("TEN_KHACH", "");
        String sdt = sharedPreferences.getString("SDT_KHACH", "");
        String email = sharedPreferences.getString("EMAIL_KHACH", "");
        if (!hoten.isEmpty() && !sdt.isEmpty() && !email.isEmpty()) {
            tvProfileNameValue.setText(hoten);
            tvProfilePhoneValue.setText(sdt);
            tvProfileEmailValue.setText(email);
        }


        btnBackProfile.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

}
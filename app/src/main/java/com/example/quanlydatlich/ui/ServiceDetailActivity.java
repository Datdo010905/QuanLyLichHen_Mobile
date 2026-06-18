package com.example.quanlydatlich.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.ServiceResponse;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView tvServiceName, tvServicePrice, tvServiceDescription,
                    tvServicePhut, tvServiceMota;
    private ImageView btnBack, imgServiceBanner;
    private Button btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_detail);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Ánh xạ View
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServicePrice = findViewById(R.id.tvServicePrice);
        tvServicePhut = findViewById(R.id.tvServicePhut);
        tvServiceMota = findViewById(R.id.tvServiceMota);
        tvServiceDescription = findViewById(R.id.tvServiceDescription);
        btnBack = findViewById(R.id.btnBack);
        imgServiceBanner = findViewById(R.id.imgServiceBanner);
        btnBookNow = findViewById(R.id.btnBookNow);

        //hứng data từ intent
        ServiceResponse.ServiceModel dichVu =
                (ServiceResponse.ServiceModel) getIntent().getSerializableExtra("SERVICE_DATA");

        //đổ data vào view
        if (dichVu != null) {
            tvServiceName.setText(dichVu.getName());

            // Format giá tiền 349,000 VNĐ
            String giaFormat = String.format("%,.0f VNĐ", dichVu.getGia());
            tvServicePrice.setText(giaFormat);

            //tách chuỗi mô tả thành nhiều dòng
            tvServiceMota.setText("- " + dichVu.getMoTa()
                    .replaceAll("\\s*,\\s*", "\n- "));

            tvServiceDescription.setText("• " + dichVu.getQuyTrinh()
                            .replaceAll("\\s*-\\s*", "\n• "));

            tvServicePhut.setText(dichVu.getThoiGian() + " phút");


            //dùng Glide để tải ảnh
            String hinhAnh = dichVu.getHinh();
            String baseUrl = "http://192.168.90.101:5000";
            String fullImageUrl = baseUrl + hinhAnh;

            // 3. Gọi Glide tải ảnh và đắp vào ImageView
            com.bumptech.glide.Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(dichVu.getDefaultImage()) // Ảnh chờ mạng load
                    .error(dichVu.getDefaultImage()) // Ảnh link lỗi/không tìm thấy
                    .into(imgServiceBanner);
        }

        //Back
        btnBack.setOnClickListener(v -> finish());

        //Đặt Lịch
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Đã chọn: " + dichVu.getName() + " -> Chuyển sang Đặt Lịch!", Toast.LENGTH_SHORT).show();
        });
    }
}
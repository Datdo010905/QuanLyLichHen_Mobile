package com.example.quanlydatlich.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.adapter.BannerAdapter;
import com.example.quanlydatlich.adapter.ServiceAdapter;
import com.example.quanlydatlich.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView tvLoginNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 1. Tìm RecyclerView và setup Layout
        RecyclerView rvServices = findViewById(R.id.rvServices);
        RecyclerView rvServices2 = findViewById(R.id.rvServices2);
        rvServices.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        rvServices2.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));

        // 2. Gọi Kho chứa ra làm việc
        com.example.quanlydatlich.repository.ServiceRepository repository = new com.example.quanlydatlich.repository.ServiceRepository();
        repository.fetchServices(new com.example.quanlydatlich.repository.ServiceRepository.ServiceCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quanlydatlich.model.ServiceResponse.ServiceModel> serviceList) {
                // Có data ghép vào Adapter
                com.example.quanlydatlich.adapter.ServiceAdapter adapter = new com.example.quanlydatlich.adapter.ServiceAdapter(serviceList);
                rvServices.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                // Báo lỗi
                android.widget.Toast.makeText(HomeActivity.this, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Gọi Kho chứa ra làm việc
        com.example.quanlydatlich.repository.ServiceRepository repository2 = new com.example.quanlydatlich.repository.ServiceRepository();
        repository2.fetchServices2(new com.example.quanlydatlich.repository.ServiceRepository.ServiceCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quanlydatlich.model.ServiceResponse.ServiceModel> serviceList) {
                // Có data ghép vào Adapter
                com.example.quanlydatlich.adapter.ServiceAdapter adapter = new com.example.quanlydatlich.adapter.ServiceAdapter(serviceList);
                rvServices2.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                // Báo lỗi
                android.widget.Toast.makeText(HomeActivity.this, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        //Tìm ViewPager2 để lấy banner
        androidx.viewpager2.widget.ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);

        //Tạo list chứa các ảnh banner
        List<Integer> listBanners = new ArrayList<>();
        listBanners.add(R.drawable.slideshow_1);
        listBanners.add(R.drawable.slideshow_2);
        listBanners.add(R.drawable.slideshow_3);
        listBanners.add(R.drawable.slideshow_4);
        listBanners.add(R.drawable.slideshow_5);
        listBanners.add(R.drawable.slideshow_6);
        listBanners.add(R.drawable.slideshow_7);
        listBanners.add(R.drawable.slideshow_8);
        listBanners.add(R.drawable.slideshow_9);

        //Gắn Adapter vào ViewPager2
        viewPagerBanner.setAdapter(new BannerAdapter(listBanners));
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);

        RecyclerView recyclerView = (RecyclerView) viewPagerBanner.getChildAt(0);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);

        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.setPadding(50, 0, 50, 0);

        //Đặt khoảng cách giữa các ảnh với nhau (24 pixel)
        viewPagerBanner.setPageTransformer(new androidx.viewpager2.widget.MarginPageTransformer(24));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi lần màn hình Home hiện lên, check Két sắt
        android.widget.TextView tvUserName = findViewById(R.id.tvUserName);
        //tìm tên đăng nhập trong két
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", MODE_PRIVATE);
        //Tìm tên đăng nhập trong két và gán
        String tenDangNhap = sharedPreferences.getString("TEN_KHACH", "GUEST");
        //show
        tvUserName.setText(tenDangNhap);

        tvLoginNow = findViewById(R.id.tvLoginNow);
       //chưa đăng nhập
        if (tenDangNhap.equals("GUEST")) {
            tvLoginNow.setText("Đăng nhập ngay >");
            tvLoginNow.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            tvLoginNow.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    android.content.Intent intent = new android.content.Intent(HomeActivity.this,
                            com.example.quanlydatlich.ui.LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            tvLoginNow.setText("Đăng xuất");
            tvLoginNow.setTextColor(android.graphics.Color.parseColor("#FF0000"));
            tvLoginNow.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    // Mở két
                    android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    //F5
                    recreate();
                    android.widget.Toast.makeText(HomeActivity.this, "Đã đăng xuất!",
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
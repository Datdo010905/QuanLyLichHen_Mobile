package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.repository.KhachHangRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.BannerAdapter;
import com.example.quanlydatlich.adapter.ServiceAdapter;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.repository.ServiceRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TextView tvLoginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);


        RecyclerView rvServices = findViewById(R.id.rvServices);
        RecyclerView rvServices2 = findViewById(R.id.rvServices2);

        FloatingActionButton fabBooking = findViewById(R.id.fabBooking);
        fabBooking.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("ThongTinKhach",
                    Context.MODE_PRIVATE);
            String maTK = prefs.getString("MATK", ""); // Lấy mã tài khoản

            if (!maTK.isEmpty()) {
                Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(HomeActivity.this, "Vui lòng đăng nhập để đặt lịch!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        rvServices.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvServices2.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loadServices(rvServices);
        loadServices2(rvServices2);

        setupBanner();

        //ánh xạ bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences prefs = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
        String maTK = prefs.getString("MATK", ""); // Lấy mã tài khoản
        //sự kiện khi chọn tab
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                //false để ko sáng nút search
                return false;
            } else if (itemId == R.id.nav_history) {
                if (!maTK.isEmpty()) {
                    // TRƯỜNG HỢP 1: Đã đăng nhập -> Chuyển sang màn lịch sử
                    Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return false;

            } else if (itemId == R.id.nav_profile) {
                if (!maTK.isEmpty()) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "Vui lòng đăng nhập để xem tài khoản!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            }
            return false;
        });

    }

    //tải danh sách dịch vụ tóc
    private void loadServices(RecyclerView recyclerView) {
        ServiceRepository repository = new ServiceRepository();

        repository.fetchServices(new ServiceRepository.ServiceCallback() {
            @Override
            public void onSuccess(List<ServiceResponse.ServiceModel> serviceList) {
                recyclerView.setAdapter(new ServiceAdapter(serviceList));
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(
                        HomeActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    //tải danh dách chăm sóc da
    private void loadServices2(RecyclerView recyclerView) {
        ServiceRepository repository = new ServiceRepository();

        repository.fetchServices2(new ServiceRepository.ServiceCallback() {
            @Override
            public void onSuccess(List<ServiceResponse.ServiceModel> serviceList) {
                recyclerView.setAdapter(new ServiceAdapter(serviceList));
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(
                        HomeActivity.this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    //setup banner
    private void setupBanner() {

        ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);

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

        viewPagerBanner.setAdapter(new BannerAdapter(listBanners));

        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.setPadding(50, 0, 50, 0);
        viewPagerBanner.setPageTransformer(new MarginPageTransformer(24));

        RecyclerView recyclerView =
                (RecyclerView) viewPagerBanner.getChildAt(0);

        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView tvUserName = findViewById(R.id.tvUserName);
        tvLoginNow = findViewById(R.id.tvLoginNow);
        tvLoginNow.setPaintFlags(
                tvLoginNow.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG
        );

        SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", MODE_PRIVATE);

        // Mở Két sắt lấy Data
        String maTK = sharedPreferences.getString("MATK", "GUEST");

        if (!maTK.isEmpty()) {
            KhachHangRepository repo = new KhachHangRepository();
            repo.fetchThongTinKhachHang(maTK, new KhachHangRepository.KhachHangCallback() {
                @Override
                public void onSuccess(KhachHangResponse.KhachHangDetail khachHang) {
                    tvUserName.setText(khachHang.getHoTen());

                    SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TEN_KHACH", khachHang.getHoTen());
                    editor.putString("SDT_KHACH", khachHang.getSdt());
                    editor.putString("EMAIL_KHACH", khachHang.getEmail());

                    editor.apply();
                }
                @Override
                public void onError(String errorMessage) {
                }
            });
        } else {
            Toast.makeText(this, "Lỗi phiên đăng nhập, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
        }

        if ("GUEST".equals(maTK)) {

            tvLoginNow.setVisibility(View.VISIBLE);

            tvLoginNow.setText("Đăng nhập ngay >");
            tvLoginNow.setTextColor(Color.WHITE);

            tvLoginNow.setOnClickListener(v ->
                    startActivity(
                            new Intent(HomeActivity.this, LoginActivity.class)
                    ));

        } else {

            tvLoginNow.setVisibility(View.GONE);
        }
    }
}
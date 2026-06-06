package com.example.quanlydatlich.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.adapter.BannerAdapter;
import com.example.quanlydatlich.adapter.ServiceAdapter;
import com.example.quanlydatlich.model.ServiceModel;
import com.example.quanlydatlich.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        // 1. Tìm cái RecyclerView trên giao diện
        RecyclerView rvServices = findViewById(R.id.rvServices);
        RecyclerView rvServices2 = findViewById(R.id.rvServices2);

         // 2. Tạo một list dữ liệu ảo (Mock data)
        List<ServiceModel> listServices = new ArrayList<>();
        listServices.add(new ServiceModel("Cắt tóc", R.mipmap.ic_launcher));
        listServices.add(new ServiceModel("Uốn định hình", R.mipmap.ic_launcher));
        listServices.add(new ServiceModel("Nhuộm màu", R.mipmap.ic_launcher));
        listServices.add(new ServiceModel("Gội đầu VIP", R.mipmap.ic_launcher));

        // 3. Gắn LayoutManager (vuốt ngang) và Adapter vào RecyclerView
        rvServices.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        rvServices.setAdapter(new ServiceAdapter(listServices));

        rvServices2.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        rvServices2.setAdapter(new ServiceAdapter(listServices));
        // 4. Tìm ViewPager2 trên giao diện
        androidx.viewpager2.widget.ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);

        // 5. Tạo list chứa các ảnh banner (Tạm dùng ảnh mặc định, sau bro tải ảnh thật ném vào drawable nhé)
        List<Integer> listBanners = new ArrayList<>();
        listBanners.add(R.mipmap.ic_launcher);
        listBanners.add(R.mipmap.ic_launcher);
        listBanners.add(R.mipmap.ic_launcher);

        // 6. Gắn Adapter vào ViewPager2
        viewPagerBanner.setAdapter(new BannerAdapter(listBanners));
        // 1. Tắt chế độ cắt mép mặc định của ViewPager2
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);

        // 2. Ép nó thụt lề 2 bên vào trong (mỗi bên hở 60 pixel) để lộ mép ảnh cạnh
        viewPagerBanner.setPadding(60, 0, 60, 0);

        // 3. Đặt khoảng cách giữa các ảnh với nhau (24 pixel)
        viewPagerBanner.setPageTransformer(new androidx.viewpager2.widget.MarginPageTransformer(24));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
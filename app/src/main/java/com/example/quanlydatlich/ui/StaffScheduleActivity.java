package com.example.quanlydatlich.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.StaffScheduleAdapter;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.StaffBookingResponse;
import com.example.quanlydatlich.repository.StaffRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffScheduleActivity extends AppCompatActivity {
    private RecyclerView rvStaffSchedule;
    private StaffRepository repository;
    private String currentMaTK;
    private ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule);

        rvStaffSchedule = findViewById(R.id.rvStaffSchedule);
        rvStaffSchedule.setLayoutManager(new LinearLayoutManager(this));

        repository = new StaffRepository();
        currentMaTK = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE)
                .getString("MATK", "");

        if (!currentMaTK.isEmpty()) {
            loadData();
        } else {
            finish();
        }
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bro có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("ThongTinKhach", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StaffScheduleActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Ở lại", null)
                    .show();
        });

    }

    private void loadData() {
        repository.fetchStaffData(currentMaTK, new StaffRepository.StaffDataCallback() {
            @Override
            public void onSuccess(List<StaffBookingResponse.StaffBooking> listLich, List<ServiceResponse.ServiceModel> listDV) {
                setupAdapter(listLich, listDV);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(StaffScheduleActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<StaffBookingResponse.StaffBooking> listLich, List<ServiceResponse.ServiceModel> listDV) {
        StaffScheduleAdapter adapter = new StaffScheduleAdapter(listLich, listDV, (maLich, trangThai) -> {
            repository.updateStatus(maLich, trangThai, new Callback<BookingResponse>() {
                @Override
                public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                    Toast.makeText(StaffScheduleActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    loadData(); // Refresh lại danh sách
                }

                @Override
                public void onFailure(Call<BookingResponse> call, Throwable t) {
                    Toast.makeText(StaffScheduleActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvStaffSchedule.setAdapter(adapter);
    }
}
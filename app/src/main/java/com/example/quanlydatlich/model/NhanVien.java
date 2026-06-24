package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NhanVien {
    @SerializedName("MANV") public String maNV;
    @SerializedName("HOTEN") public String hoTen;
    @SerializedName("SDT") public String sdt;
    @SerializedName("MACHINHANH") public String maChiNhanh;
    @SerializedName("CHUCVU") public String chucVu;

    // Khuôn Response riêng cho Nhân Viên
    public static class NhanVienRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<NhanVien> data;
    }
}
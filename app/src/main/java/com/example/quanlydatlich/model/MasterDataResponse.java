package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MasterDataResponse {

    // 1. KHUÔN NHÂN VIÊN
    public static class NhanVienRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<NhanVien> data;
    }
    public static class NhanVien {
        @SerializedName("MANV") public String maNV;
        @SerializedName("HOTEN") public String hoTen;
        @SerializedName("SDT") public String sdt;
        @SerializedName("MACHINHANH") public String maChiNhanh;
        @SerializedName("CHUCVU") public String chucVu;
    }

    // 2. KHUÔN LỊCH HẸN
    public static class LichHenRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<LichHen> data;
    }
    public static class LichHen {
        @SerializedName("MALICH") public String maLich;
        @SerializedName("NGAYHEN") public String ngayHen;
        @SerializedName("GIOHEN") public String gioHen;
        @SerializedName("TRANGTHAI") public String trangThai;
        @SerializedName("MACHINHANH") public String maChiNhanh;
    }

    // 3. KHUÔN CHI TIẾT LỊCH HẸN
    public static class ChiTietRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<ChiTiet> data;
    }
    public static class ChiTiet {
        @SerializedName("MALICH") public String maLich;
        @SerializedName("MANV") public String maNV;
        @SerializedName("MADV") public String maDV;
        @SerializedName("GIA_DUKIEN") public double giaDuKien;
    }

    // Ném cục này vào trong MasterDataResponse.java
    public static class KhachHangRes {
        @SerializedName("success") public boolean success;
        @SerializedName("message") public String message;
        @SerializedName("data") public KhachHang data;
    }

    public static class KhachHang {
        @SerializedName("MAKH") public String maKH;
        @SerializedName("HOTEN") public String hoTen;
        @SerializedName("SDT") public String sdt;
        @SerializedName("EMAIL") public String email;
        @SerializedName("MATK") public String maTK;

        public KhachHang(String hoTen, String email) {
            this.hoTen = hoTen;
            this.email = email;
        }
    }
}
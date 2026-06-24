package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StaffBookingResponse {
    @SerializedName("success") public boolean success;
    @SerializedName("data") public List<StaffBooking> data;

    public static class StaffBooking {
        @SerializedName("MALICH") public String maLich;
        @SerializedName("NGAYHEN") public String ngayHen;
        @SerializedName("GIOHEN") public String gioHen;
        @SerializedName("TRANGTHAI") public String trangThai;

        // 💡 Hứng Object Khách Hàng lồng bên trong
        @SerializedName("KHACHHANG") public CustomerInfo khachHang;

        // 💡 Hứng mảng Chi Tiết lồng bên trong
        @SerializedName("CHITIETLICHHEN") public List<DetailInfo> chiTiet;
    }

    public static class CustomerInfo {
        @SerializedName("HOTEN") public String hoTen;
        @SerializedName("SDT") public String sdt;
    }

    public static class DetailInfo {
        @SerializedName("MADV") public String maDV;
        @SerializedName("GHICHU") public String ghiChu;
    }
}
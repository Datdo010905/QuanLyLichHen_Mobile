package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {

    @SerializedName("booking")
    private BookingInfo booking;

    @SerializedName("details")
    private BookingDetails details;

    public BookingRequest(BookingInfo booking, BookingDetails details) {
        this.booking = booking;
        this.details = details;
    }

    // 1. KHUÔN CHO BẢNG LỊCH HẸN
    public static class BookingInfo {
        @SerializedName("MALICH") private String maLich;
        @SerializedName("NGAYHEN") private String ngayHen;
        @SerializedName("GIOHEN") private String gioHen;
        @SerializedName("TRANGTHAI") private String trangThai;
        @SerializedName("MACHINHANH") private String maChiNhanh;
        @SerializedName("MAKH") private String maKH;

        public BookingInfo(String maLich, String ngayHen, String gioHen, String maChiNhanh, String maKH) {
            this.maLich = maLich;
            this.ngayHen = ngayHen; // Chuỗi định dạng "yyyy-MM-dd"
            this.gioHen = gioHen;   // Chuỗi định dạng "HH:mm"
            this.trangThai = "Đã đặt"; // Web mặc định
            this.maChiNhanh = maChiNhanh;
            this.maKH = maKH;
        }
    }

    // 2. KHUÔN CHO BẢNG CHI TIẾT LỊCH HẸN
    public static class BookingDetails {
        @SerializedName("MALICH") private String maLich;
        @SerializedName("MADV") private String maDV;
        @SerializedName("MANV") private String maNV;
        @SerializedName("SOLUONG") private int soLuong;
        @SerializedName("GIA_DUKIEN") private double giaDuKien;
        @SerializedName("GHICHU") private String ghiChu;

        public BookingDetails(String maLich, String maDV, String maNV, double giaDuKien, String ghiChu) {
            this.maLich = maLich;
            this.maDV = maDV;
            this.maNV = maNV;
            this.soLuong = 1; // Mặc định là 1 dịch vụ
            this.giaDuKien = giaDuKien;
            this.ghiChu = ghiChu != null && !ghiChu.isEmpty() ? ghiChu : "Không có ghi chú";
        }
    }
}
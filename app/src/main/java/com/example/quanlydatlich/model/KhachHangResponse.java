package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

public class KhachHangResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private KhachHangDetail data;
    public boolean isSuccess() { return success; }
    public KhachHangDetail getData() { return data; }

    // --- LỚP LÕI ---
    public static class KhachHangDetail {
        @SerializedName("MAKH") private String maKH;
        @SerializedName("HOTEN") private String hoTen;
        @SerializedName("SDT") private String sdt;
        @SerializedName("EMAIL") private String email;
        @SerializedName("MATK") private String maTK;
        public String getMaKH() { return maKH != null ? maKH.trim() : ""; }
        public String getHoTen() { return hoTen != null ? hoTen : "Chưa cập nhật"; }
        public String getSdt() { return sdt != null ? sdt.trim() : "Chưa cập nhật"; }
        public String getEmail() { return email != null ? email : "Chưa cập nhật"; }
        public String getMaTK() { return maTK != null ? maTK.trim() : ""; }
    }
}
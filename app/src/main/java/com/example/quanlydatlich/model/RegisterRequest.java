package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("HOTEN")
    private String hoTen;

    @SerializedName("SDT")
    private String sdt;

    @SerializedName("EMAIL")
    private String email;

    @SerializedName("PASS")
    private String pass;

    @SerializedName("TRANGTHAI")
    private String trangThai;

    @SerializedName("PHANQUYEN")
    private int phanQuyen;

    // Constructor để gán dữ liệu từ Activity truyền vào
    public RegisterRequest(String hoTen, String sdt, String email, String pass) {
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.pass = pass;
        this.trangThai = "Hoạt động";
        this.phanQuyen = 0;        // 0 là Khách hàng
    }
}
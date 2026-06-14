package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

public class AuthModel {

    // 1. Khuôn GỬI LÊN
    public static class LoginRequest {
        @SerializedName("username")
        private String username;

        @SerializedName("pass")
        private String pass;

        public LoginRequest(String username, String pass) {
            this.username = username;
            this.pass = pass;
        }
    }

    // Khuôn NHẬN VỀ data user từ Prisma
    public static class UserData {
        @SerializedName("MATK")
        private String matk;

        @SerializedName("PHANQUYEN")
        private int phanQuyen;

        @SerializedName("TRANGTHAI")
        private String trangThai;

        public String getMatk() { return matk; }
        public int getPhanQuyen() { return phanQuyen; }
        public String getTrangThai() { return trangThai; }
    }

    // Vỏ bọc Response
    public static class LoginResponse {
        @SerializedName("success") private boolean success;
        @SerializedName("message") private String message;
        @SerializedName("token")
        private String token;
        @SerializedName("data") private UserData data;
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getToken() { return token; }
        public UserData getData() { return data; }
    }
}
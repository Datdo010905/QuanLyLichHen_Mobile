package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceResponse {
    // --- LỚP VỎ CỦA API TRẢ VỀ---
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<ServiceModel> data;
    public boolean isSuccess() {
        return success;
    }
    public List<ServiceModel> getData() {
        return data;
    }
    // --- LỚP LÕI BÊN TRONG cục DATA---
    public static class ServiceModel implements java.io.Serializable {
        @SerializedName("MADV")
        private String maDV;
        @SerializedName("LOAI")
        private String loai;
        @SerializedName("TENDV")
        private String name;
        @SerializedName("GIADV")
        private double gia;
        @SerializedName("MOTA")
        private String moTa;
        @SerializedName("THOIGIAN")
        private int thoiGian;
        @SerializedName("HINH")
        private String hinh;
        @SerializedName("TRANGTHAI")
        private String trangThai;
        @SerializedName("QUYTRINH")
        private String quyTrinh;
        private int defaultImage = com.example.quanlydatlich.R.mipmap.ic_launcher;
        public String getMaDV() {
            return maDV != null ? maDV.trim() : "";
        }
        public String getName() {
            return name != null ? name.trim() : "";
        }
        public double getGia() {
            return gia;
        }
        public String getMoTa() {
            return moTa != null ? moTa.trim() : "";
        }
        public int getThoiGian() {
            return thoiGian;
        }
        public String getHinh() {
            return hinh != null ? hinh.trim() : "";
        }
        public String getTrangThai() {
            return trangThai != null ? trangThai.trim() : "";
        }
        public String getQuyTrinh() {
            return quyTrinh != null ? quyTrinh.trim() : "";
        }
        public int getDefaultImage() {
            return defaultImage;
        }
    }
}
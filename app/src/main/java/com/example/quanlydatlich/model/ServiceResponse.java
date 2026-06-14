package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServiceResponse {
    // --- LỚP VỎ CỦA API TRẢ VỀ---
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<ServiceModel> data;
    public boolean isSuccess() { return success; }
    public List<ServiceModel> getData() { return data; }
    // --- LỚP LÕI BÊN TRONG cục DATA---
    public static class ServiceModel {
        //serial để tránh lỗi
        @SerializedName("MADV") private String maDV;
        @SerializedName("TENDV") private String name;
        @SerializedName("GIADV") private int gia;
        @SerializedName("HINH") private String hinh;
        private int defaultImage = com.example.quanlydatlich.R.mipmap.ic_launcher;

        //tạo các hàm get để tránh lỗi
        public String getMaDV() {
            return maDV != null ? maDV.trim() : "";
        }
        public String getName() {
            return name != null ? name.trim() : "";
        }
        public int getGia() {
            return gia;
        }
        public String getHinh() {
            return hinh;
        }
        public int getDefaultImage() {
            return defaultImage;
        }
    }
}
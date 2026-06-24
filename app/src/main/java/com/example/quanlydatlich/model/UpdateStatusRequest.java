package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
public class UpdateStatusRequest {
    @SerializedName("TRANGTHAI") private String trangThai;
    public UpdateStatusRequest(String trangThai) { this.trangThai = trangThai; }
}
package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LichHen {
    @SerializedName("MALICH") public String maLich;
    @SerializedName("NGAYHEN") public String ngayHen;
    @SerializedName("GIOHEN") public String gioHen;
    @SerializedName("TRANGTHAI") public String trangThai;
    @SerializedName("MACHINHANH") public String maChiNhanh;

    public static class LichHenRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<LichHen> data;
    }
}
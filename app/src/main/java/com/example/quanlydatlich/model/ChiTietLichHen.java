package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChiTietLichHen {
    @SerializedName("MALICH") public String maLich;
    @SerializedName("MANV") public String maNV;
    @SerializedName("MADV") public String maDV;
    @SerializedName("GIA_DUKIEN") public double giaDuKien;

    public static class ChiTietRes {
        @SerializedName("success") public boolean success;
        @SerializedName("data") public List<ChiTietLichHen> data;
    }
}
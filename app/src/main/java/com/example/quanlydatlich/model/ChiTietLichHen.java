package com.example.quanlydatlich.model;
import java.util.List;

public class ChiTietLichHen {
    public String maChiTiet, maLich, maDV, maNV;
    public double gia;

    public static class ChiTietRes {
        public List<ChiTietLichHen> data;
    }
}
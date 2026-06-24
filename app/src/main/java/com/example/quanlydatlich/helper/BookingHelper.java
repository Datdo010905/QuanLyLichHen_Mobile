package com.example.quanlydatlich.helper;

import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingHelper {

    //static để gọi ở bất cứ đâu mà không cần 'new BookingHelper()'
    public static List<String> calculateAvailableHours(
            String selectedThoCatId,
            String selectedNgay,
            List<ChiTietLichHen> listChiTiet,
            List<LichHen> listLichHen) {

        List<String> hoursList = new ArrayList<>();
        hoursList.add("-- Chọn giờ hẹn --");

        //Nếu khách chưa chọn Thợ hoặc Ngày
        if (selectedThoCatId.isEmpty() || selectedNgay.isEmpty()) {
            return hoursList;
        }

        // ==========================================
        //1: LỌC MÃ LỊCH CỦA THỢ ĐƯỢC CHỌN
        // ==========================================
        List<String> bookedIdsForStaff = new ArrayList<>();
        if (listChiTiet != null) {
            for (ChiTietLichHen ct : listChiTiet) {
                // Dùng equalsIgnoreCase để so sánh mã thợ (NV001 = nv001).
                if (ct.maNV != null && ct.maNV.trim().equalsIgnoreCase(selectedThoCatId.trim())) {
                    // Ép mã lịch in hoa (toUpperCase)
                    bookedIdsForStaff.add(ct.maLich != null ? ct.maLich.trim().toUpperCase() : "");
                }
            }
        }

        // ==========================================
        // 2: TÌM CÁC KHUNG GIỜ ĐÃ BỊ ĐẶT (TRÙNG NGÀY + ĐÚNG THỢ + CHƯA HỦY)
        // ==========================================
        List<String> bookedHours = new ArrayList<>();
        if (listLichHen != null) {
            for (LichHen lh : listLichHen) {
                if (lh.ngayHen == null || lh.trangThai == null || lh.maLich == null) continue;

                // XỬ LÝ CHUỖI NGÀY: "YYYY-MM-DDTHH:mm:ss.sssZ"
                // Đoạn này băm chuỗi ra để so sánh chính xác ngày "YYYY-MM-DD"

                String dbNgay = lh.ngayHen.trim();
                if (dbNgay.contains("T")) dbNgay = dbNgay.split("T")[0];
                String[] dParts = dbNgay.split("-");
                boolean isTrungNgay = false;

                if (dParts.length == 3) {
                    try {
                        String cleanDate = String.format("%04d-%02d-%02d",
                                Integer.parseInt(dParts[0]), Integer.parseInt(dParts[1]), Integer.parseInt(dParts[2]));
                        isTrungNgay = cleanDate.equals(selectedNgay);
                    } catch (Exception e) { }
                } else {
                    isTrungNgay = dbNgay.startsWith(selectedNgay);
                }

                String trangThai = lh.trangThai.toLowerCase();
                boolean isHuy = trangThai.contains("huỷ") || trangThai.contains("hủy");

                // So sánh xem cái mã lịch này có thuộc về ông thợ đang được chọn không
                boolean isNVDuocChon = bookedIdsForStaff.contains(lh.maLich.trim().toUpperCase());

                // Nếu 3 ĐIỀU KIỆN hội tụ -> Giờ này đã có người!
                if (isTrungNgay && !isHuy && isNVDuocChon) {
                    try {
                        //  GIỜ VỀ CHUẨN "HH:mm" (Ví dụ: 8:30 -> 08:30)
                        String timeStr = lh.gioHen.trim();
                        if (timeStr.contains("T")) timeStr = timeStr.split("T")[1];

                        String[] tParts = timeStr.split(":");
                        if (tParts.length >= 2) {
                            int h = Integer.parseInt(tParts[0].trim());
                            int m = Integer.parseInt(tParts[1].trim());
                            String time = String.format("%02d:%02d", h, m);
                            bookedHours.add(time); // Thêm giờ vào danh sách
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // ==========================================
        // XỬ LÝ LOGIC NGÀY/GIỜ HIỆN TẠI ĐỂ CHẶN QUÁ KHỨ
        // ==========================================
        Calendar now = Calendar.getInstance();
        String todayString = String.format("%04d-%02d-%02d",
                now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));

        boolean isToday = selectedNgay.equals(todayString);

        // TÍNH THỜI GIAN ĐỆM: Đổi giờ hiện tại ra phút, cộng thêm 30 phút để thợ có thời gian chuẩn bị.
        // Ví dụ: Bây giờ 15:05 -> Khách chỉ được book từ 15:35 trở đi.
        int currentTotalMinutes = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE) + 30;

        // ==========================================
        // SINH GIỜ TRỐNG
        // ==========================================
        for (int h = 8; h <= 22; h++) { // Quét từ 8h sáng đến 22h tối
            for (int m : new int[]{0, 30}) { // Chỉ lấy 2 mốc phút: chẵn 00 và rưỡi 30

                // NẾU LÀ HÔM NAY: Lọc bỏ các khung giờ đã trôi qua
                if (isToday) {
                    int slotTotalMinutes = (h * 60) + m;
                    if (slotTotalMinutes < currentTotalMinutes) continue; // Bỏ qua
                }

                String timeSlot = String.format("%02d:%02d", h, m);

                // Nếu giờ này không nằm trong bookedHours -> Thêm
                if (!bookedHours.contains(timeSlot)) {
                    hoursList.add(timeSlot);
                }
            }
        }
        return hoursList;
    }
}
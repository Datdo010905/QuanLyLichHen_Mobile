package com.example.quanlydatlich.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.StaffBookingResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffScheduleAdapter extends RecyclerView.Adapter<StaffScheduleAdapter.ViewHolder> {

    private List<StaffBookingResponse.StaffBooking> listLich;

    private HashMap<String, String> mapDichVu = new HashMap<>();

    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onUpdateStatus(String maLich, String trangThaiMoi);
    }

    public StaffScheduleAdapter(List<StaffBookingResponse.StaffBooking> listLich,
                                List<ServiceResponse.ServiceModel> listDichVu,
                                OnActionClickListener listener) {
        this.listLich = listLich;
        this.listener = listener;

        // 💡 Nhồi Data vào Map 1 lần duy nhất lúc khởi tạo Adapter
        if (listDichVu != null) {
            for (ServiceResponse.ServiceModel dv : listDichVu) {
                if (dv.getMaDV() != null) {
                    mapDichVu.put(dv.getMaDV().trim(), dv.getName());
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lich_lam_viec, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffBookingResponse.StaffBooking booking = listLich.get(position);

        // 1. Cắt giờ và ngày
        String ngay = booking.ngayHen != null ? booking.ngayHen.split("T")[0] : "N/A";
        String gio = booking.gioHen != null ? booking.gioHen.split("T")[1].substring(0, 5) : "N/A";
        holder.tvThoiGianTho.setText(gio + " | " + ngay);

        // 2. Thông tin Khách hàng
        if (booking.khachHang != null) {
            String ten = booking.khachHang.hoTen != null ? booking.khachHang.hoTen : "Khách vô danh";
            String sdt = booking.khachHang.sdt != null ? booking.khachHang.sdt : "Không có SĐT";
            holder.tvTenKhachTho.setText("👤 Khách: " + ten + " - " + sdt);
        } else {
            holder.tvTenKhachTho.setText("👤 Khách hàng: Dữ liệu lỗi");
        }

        // 3. Xử lý Chi tiết (Dịch vụ và Ghi chú)
        List<String> listTenDV = new ArrayList<>();
        String ghiChuText = "Không có ghi chú";

        if (booking.chiTiet != null && !booking.chiTiet.isEmpty()) {
            // Lấy ghi chú của thằng đầu tiên (thường 1 bill chung 1 ghi chú)
            if (booking.chiTiet.get(0).ghiChu != null) {
                ghiChuText = booking.chiTiet.get(0).ghiChu;
            }

            // lặp qua danh sách chi tiết để lấy mã dịch vụ để tra tên
            for (StaffBookingResponse.DetailInfo ct : booking.chiTiet) {
                if (ct.maDV != null) {
                    listTenDV.add(getTenDichVu(ct.maDV.trim()));
                }
            }
        }

        holder.tvGhiChuTho.setText("📝 Ghi chú: " + ghiChuText);
        holder.tvDichVuTho.setText("💈 Dịch vụ: " + (listTenDV.isEmpty() ? "Chưa chọn" : TextUtils.join(", ", listTenDV)));

        // 4. Xử lý Trạng thái và Nút bấm
        String trangThai = booking.trangThai != null ? booking.trangThai.trim() : "";
        holder.tvTrangThaiTho.setText(trangThai);

        // Mặc định ẩn nút
        holder.layoutActionButtons.setVisibility(View.GONE);

        switch (trangThai) {

            case "Đã đặt":
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#1890FF"));
                holder.tvTrangThaiTho.setBackgroundResource(R.drawable.bg_status_booked);
                break;

            case "Đang chờ":
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#722ED1"));
                holder.tvTrangThaiTho.setBackgroundResource(R.drawable.bg_status_pending);
                holder.layoutActionButtons.setVisibility(View.VISIBLE);// cho nv huỷ khi quá giờ hẹn
                break;

            case "Đang thực hiện":
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#FA8C16"));
                holder.tvTrangThaiTho.setBackgroundResource(R.drawable.bg_status_processing);
                break;

            case "Đã hoàn thành":
            case "Hoàn thành":
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#52C41A"));
                holder.tvTrangThaiTho.setBackgroundResource(R.drawable.bg_status_completed);
                break;

            case "Đã huỷ":
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#F5222D"));
                holder.tvTrangThaiTho.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;

            default:
                holder.tvTrangThaiTho.setTextColor(Color.parseColor("#94A3B8"));
                break;
        }

        //check in KH
        holder.btnXong.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateStatus(booking.maLich, "Đang thực hiện");
        });

        holder.btnKhachKhongDen.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateStatus(booking.maLich, "Đã huỷ");
        });
    }
    //map dịch vụ
    private String getTenDichVu(String maDV) {
        return mapDichVu.containsKey(maDV) ? mapDichVu.get(maDV) : maDV;
    }

    @Override
    public int getItemCount() {
        return listLich != null ? listLich.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvThoiGianTho, tvTrangThaiTho, tvTenKhachTho, tvDichVuTho, tvGhiChuTho;
        LinearLayout layoutActionButtons;
        Button btnXong, btnKhachKhongDen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThoiGianTho = itemView.findViewById(R.id.tvThoiGianTho);
            tvTrangThaiTho = itemView.findViewById(R.id.tvTrangThaiTho);
            tvTenKhachTho = itemView.findViewById(R.id.tvTenKhachTho);
            tvDichVuTho = itemView.findViewById(R.id.tvDichVuTho);
            tvGhiChuTho = itemView.findViewById(R.id.tvGhiChuTho);
            layoutActionButtons = itemView.findViewById(R.id.layoutActionButtons);
            btnXong = itemView.findViewById(R.id.btnXong);
            btnKhachKhongDen = itemView.findViewById(R.id.btnKhachKhongDen);
        }
    }
}
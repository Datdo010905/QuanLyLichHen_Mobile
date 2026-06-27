package com.example.quanlydatlich.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.ServiceResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<LichHen> listLichHen;
    private List<ChiTietLichHen> listChiTiet;

    private HashMap<String, String> mapNhanVien = new HashMap<>();
    private HashMap<String, String> mapDichVu = new HashMap<>();

    public interface OnItemCancelListener {
        void onCancelClick(String maLich);
    }

    private OnItemCancelListener listener;

    public HistoryAdapter(List<LichHen> listLichHen,
                          List<ChiTietLichHen> listChiTiet,
                          List<NhanVien> listNhanVien,
                          List<ServiceResponse.ServiceModel> listDichVu,
                          OnItemCancelListener listener) {
        this.listLichHen = listLichHen;
        this.listChiTiet = listChiTiet;
        this.listener = listener;

        // cho Data vào Map 1 lần duy nhất lúc khởi tạo Adapter
        if (listNhanVien != null) {
            for (NhanVien nv : listNhanVien) {
                if (nv.maNV != null) mapNhanVien.put(nv.maNV.trim(), nv.hoTen);
            }
        }
        if (listDichVu != null) {
            for (ServiceResponse.ServiceModel dv : listDichVu) {
                if (dv.getMaDV() != null) mapDichVu.put(dv.getMaDV().trim(), dv.getName());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lich_su, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LichHen lh = listLichHen.get(position);

        holder.tvMaLichItem.setText("Mã lịch: " + lh.maLich);

        String ngay = lh.ngayHen != null ? lh.ngayHen.split("T")[0] : "N/A";
        String gio = lh.gioHen != null ? lh.gioHen.split("T")[1].substring(0, 5) : "N/A";
        holder.tvNgayGioItem.setText(ngay + " | " + gio);

        String trangThai = lh.trangThai != null ? lh.trangThai.trim() : "";
        holder.tvTrangThaiItem.setText(trangThai);

        String tenChiNhanh = "Đang cập nhật";
        if (lh.maChiNhanh != null) {
            switch (lh.maChiNhanh.trim()) {
                case "CN001": tenChiNhanh = "30Shine - Nguyễn Trãi"; break;
                case "CN002": tenChiNhanh = "30Shine - Cầu Giấy"; break;
                case "CN003": tenChiNhanh = "30Shine - Tân Bình"; break;
                case "CN004": tenChiNhanh = "30Shine - Đà Nẵng"; break;
                default: tenChiNhanh = lh.maChiNhanh; break;
            }
        }
        holder.tvChiNhanhItem.setText("🏠 Chi nhánh: " + tenChiNhanh);

        List<String> danhSachTho = new ArrayList<>();
        List<String> danhSachDichVu = new ArrayList<>();
        double tongTien = 0;

        //check chi tiết lịch
        if (listChiTiet != null) {
            for (ChiTietLichHen ct : listChiTiet) {
                if (ct.maLich != null && ct.maLich.trim().equals(lh.maLich.trim())) {

                    tongTien += ct.giaDuKien;

                    //lấy tên Thợ
                    if (ct.maNV != null) {
                        String tenTho = getTenTho(ct.maNV.trim());
                        if (!danhSachTho.contains(tenTho)) {
                            danhSachTho.add(tenTho);
                        }
                    }

                    //lấy tên dv
                    if (ct.maDV != null) {
                        danhSachDichVu.add(getTenDichVu(ct.maDV.trim()));
                    }
                }
            }
        }

        // Nối tên bằng dấu phẩy
        String textTho = danhSachTho.isEmpty() ? "Đang cập nhật" : TextUtils.join(", ", danhSachTho);
        String textDichVu = danhSachDichVu.isEmpty() ? "Đang cập nhật" : TextUtils.join(", ", danhSachDichVu);

        holder.tvThoCatItem.setText("✂️ Stylist: " + textTho);
        holder.tvDichVuItem.setText("💈 Dịch vụ: " + textDichVu);


        holder.btnHuyLichItem.setVisibility(View.GONE);
        holder.tvTongTienItem.setVisibility(View.GONE);

        switch (trangThai) {

            case "Đã đặt":
                holder.tvTrangThaiItem.setTextColor(Color.parseColor("#1890FF"));
                holder.tvTrangThaiItem.setBackgroundResource(R.drawable.bg_status_booked);
                holder.btnHuyLichItem.setVisibility(View.VISIBLE);//cho huỷ khi mới đặt
                break;

            case "Đang chờ":
                holder.tvTrangThaiItem.setTextColor(Color.parseColor("#722ED1"));
                holder.tvTrangThaiItem.setBackgroundResource(R.drawable.bg_status_pending);
                break;

            case "Đang thực hiện":
                holder.tvTrangThaiItem.setTextColor(Color.parseColor("#FA8C16"));
                holder.tvTrangThaiItem.setBackgroundResource(R.drawable.bg_status_processing);
                break;

            case "Đã huỷ":
                holder.tvTrangThaiItem.setTextColor(Color.parseColor("#F5222D"));
                holder.tvTrangThaiItem.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;

            case "Đã hoàn thành":
            case "Hoàn thành":
                holder.tvTrangThaiItem.setTextColor(Color.parseColor("#52C41A"));
                holder.tvTrangThaiItem.setBackgroundResource(R.drawable.bg_status_completed);
                holder.tvTongTienItem.setVisibility(View.VISIBLE);

                String giaFormat = String.format("%,.0f VNĐ", tongTien);
                holder.tvTongTienItem.setText("Tổng thanh toán: " + giaFormat);
                break;
        }

        holder.btnHuyLichItem.setOnClickListener(v -> {
            if (listener != null) listener.onCancelClick(lh.maLich);
        });
    }

    // 💡 HÀM HELPER: Dịch mã Thợ và Dịch Vụ sang Tên thật không dùng vòng lặp
    private String getTenTho(String maNV) {
        return mapNhanVien.containsKey(maNV) ? mapNhanVien.get(maNV) : maNV;
    }

    private String getTenDichVu(String maDV) {
        return mapDichVu.containsKey(maDV) ? mapDichVu.get(maDV) : maDV;
    }

    @Override
    public int getItemCount() {
        return listLichHen != null ? listLichHen.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNgayGioItem, tvTrangThaiItem, tvMaLichItem, tvTongTienItem;
        TextView tvChiNhanhItem, tvThoCatItem, tvDichVuItem;
        Button btnHuyLichItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNgayGioItem = itemView.findViewById(R.id.tvNgayGioItem);
            tvTrangThaiItem = itemView.findViewById(R.id.tvTrangThaiItem);
            tvMaLichItem = itemView.findViewById(R.id.tvMaLichItem);
            tvTongTienItem = itemView.findViewById(R.id.tvTongTienItem);
            tvChiNhanhItem = itemView.findViewById(R.id.tvChiNhanhItem);
            tvThoCatItem = itemView.findViewById(R.id.tvThoCatItem);
            tvDichVuItem = itemView.findViewById(R.id.tvDichVuItem);
            btnHuyLichItem = itemView.findViewById(R.id.btnHuyLichItem);
        }
    }
}
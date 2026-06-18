package com.example.quanlydatlich.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlydatlich.R;

import com.example.quanlydatlich.model.ServiceResponse.ServiceModel;
import com.example.quanlydatlich.ui.ServiceDetailActivity;

import java.util.List;


//lắp ráp để hiển thị danh sách dịch vụ
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<ServiceModel> serviceList;
    //gõ tìm kiếm đến đâu, danh sách được cập nhật
    public void updateList(List<ServiceModel> newList) {
        this.serviceList = newList;
        notifyDataSetChanged(); //check thay đổi
    }
    // Hàm khởi tạo Adapter
    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    //lớp view holder để giữ view trong danh sách
    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgService;
        TextView tvServiceName;
        //hàm khởi tạo dịch vụ có tên và ảnh
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgService = itemView.findViewById(R.id.imgService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }
    }

    @NonNull
    @Override
    //tạo view holder để hiển thị danh sách dịch vụ
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    //đổ dữ liệu vào view holder
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        // Lấy dịch vụ hiện tại
        ServiceModel currentItem = serviceList.get(position);
        // Gắn tên dịch vụ
        holder.tvServiceName.setText(currentItem.getName());

        // 1. Lấy chuỗi đường dẫn ảnh từ API (Ví dụ: "/img/product/abc.jpg")
        String hinhAnh = currentItem.getHinh();

        // 2. Nối thêm địa chỉ Server vào đầu để lấy ảnh đầy đủ
        String baseUrl = "http://192.168.90.101:5000";
        String fullImageUrl = baseUrl + hinhAnh;

        // 3. Gọi Glide tải ảnh và đắp vào ImageView
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(com.example.quanlydatlich.R.mipmap.ic_launcher) // Ảnh hiển thị tạm trong lúc chờ mạng load
                .error(com.example.quanlydatlich.R.mipmap.ic_launcher) // Ảnh hiển thị nếu link lỗi/không tìm thấy
                .into(holder.imgService);

        // Click sang chi tiết dịch vụ
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lấy context từ view holder
                Intent intent = new Intent(v.getContext(), ServiceDetailActivity.class);
                intent.putExtra("SERVICE_DATA", currentItem);
                //next
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    //hàm trả về số lượng dịch vụ để hiển thị
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }
}
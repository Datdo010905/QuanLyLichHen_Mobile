package com.example.quanlydatlich.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlydatlich.R;
import com.example.quanlydatlich.model.ServiceModel;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceModel> serviceList;

    public ServiceAdapter(List<ServiceModel> serviceList) {
        this.serviceList = serviceList;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgService;
        TextView tvServiceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgService = itemView.findViewById(R.id.imgService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel currentItem = serviceList.get(position);
        holder.tvServiceName.setText(currentItem.getName());
        holder.imgService.setImageResource(currentItem.getImageResId());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }
}
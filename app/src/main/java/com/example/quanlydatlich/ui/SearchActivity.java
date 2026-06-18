package com.example.quanlydatlich.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.quanlydatlich.R;
import com.example.quanlydatlich.adapter.ServiceAdapter;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.ServiceResponse.ServiceModel;
import com.example.quanlydatlich.repository.ServiceRepository;
// Import API Client của bro vào đây nếu cần gọi API

public class SearchActivity extends AppCompatActivity {
    private EditText edtSearchInput;
    private ImageView btnBackSearch, btnClearText;
    private RecyclerView rvSearchResults;
    private ServiceAdapter adapter;
    private List<ServiceModel> fullServiceList; // Danh sách gốc chứa TẤT CẢ dịch vụ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtSearchInput = findViewById(R.id.edtSearchInput);
        btnBackSearch = findViewById(R.id.btnBackSearch);
        btnClearText = findViewById(R.id.btnClearText);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        //Setup Grid & Adapter
        fullServiceList = new ArrayList<>();
        adapter = new ServiceAdapter(fullServiceList);

        //Chia màn hình thành lưới 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
        rvSearchResults.setLayoutManager(gridLayoutManager);
        rvSearchResults.setAdapter(adapter);


        // 3. Gọi API lấy toàn bộ dịch vụ về nhét vào fullServiceList
        fetchDataFromApi();

        //gõ phím để tìm kiếm
        edtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //lọc theo chữ
                filterList(s.toString());
                //Nếu ô nhập có chữ
                //Hiện nút [X] để xóa nhanh.
                //Nếu rỗng -> Giấu đi
                if (s.toString().trim().length() > 0) {
                    btnClearText.setVisibility(View.VISIBLE);
                } else {
                    btnClearText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Back
        btnBackSearch.setOnClickListener(v -> finish());
        //Xóa trắng ô tìm kiếm
        btnClearText.setOnClickListener(v -> edtSearchInput.setText(""));
    }

    //lọc danh sách dịch vụ theo từ khóa
    private void filterList(String keyword) {
        List<ServiceModel> filteredList = new ArrayList<>();

        //Duyệt qua danh sách gốc theo tên
        for (ServiceModel item : fullServiceList) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        // lọc rồi -> gắn vào adapter
        adapter.updateList(filteredList);
    }
    //gọi api
    private void fetchDataFromApi() {
        ServiceRepository repository = new ServiceRepository();
        repository.fetchAllServices(new ServiceRepository.ServiceCallback() {
            @Override
            public void onSuccess(List<ServiceResponse.ServiceModel> serviceList) {
                //xoá cũ
                fullServiceList.clear();

                fullServiceList.addAll(serviceList);
                adapter.updateList(fullServiceList);
            }
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
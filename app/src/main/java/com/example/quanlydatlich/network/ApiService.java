package com.example.quanlydatlich.network;


import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.AuthModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Đường dẫn API để lấy danh sách dịch vụ
    @GET("/api/dichvu/get-all-DichVuCungCap")
    Call<ServiceResponse> getServicesAll();

    @GET("/api/dichvu/get-all-DichVuToc")
    Call<ServiceResponse> getServices();

    @GET("/api/dichvu/get-all-DichVuCSD")
    Call<ServiceResponse> getServices2();

    @POST("/api/login/login-taikhoan")
    Call<AuthModel.LoginResponse> loginUser(@Body AuthModel.LoginRequest request);

    @GET("/api/khachhang/get-byId-khachhang/{id}")
    Call<KhachHangResponse> getThongTinKhachHang(@Path("id") String matk);
}
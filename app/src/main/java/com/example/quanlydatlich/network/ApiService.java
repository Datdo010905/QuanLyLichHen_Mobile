package com.example.quanlydatlich.network;


import com.example.quanlydatlich.model.BookingRequest;
import com.example.quanlydatlich.model.BookingResponse;
import com.example.quanlydatlich.model.ChiTietLichHen;
import com.example.quanlydatlich.model.KhachHangResponse;
import com.example.quanlydatlich.model.LichHen;
import com.example.quanlydatlich.model.NhanVien;
import com.example.quanlydatlich.model.RegisterRequest;
import com.example.quanlydatlich.model.ServiceResponse;
import com.example.quanlydatlich.model.AuthModel;
import com.example.quanlydatlich.model.StaffBookingResponse;
import com.example.quanlydatlich.model.UpdateProfileRequest;
import com.example.quanlydatlich.model.UpdateStatusRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("/api/lichhen/create-full")
    Call<BookingResponse> createBookingTransaction(@Body BookingRequest request);


    // ----- CÁC API CHO LUỒNG ĐẶT LỊCH -----
    @GET("/api/nhanvien/get-all-nhanvien")
    Call<NhanVien.NhanVienRes> getAllNhanVien();

    // Lấy Lịch Hẹn
    @GET("/api/lichhen/get-all-lichhen")
    Call<LichHen.LichHenRes> getAllLichHen();

    // Lấy Chi Tiết Lịch Hẹn
    @GET("/api/lichhen/get-all-CTlichhen")
    Call<ChiTietLichHen.ChiTietRes> getAllChiTietLichHen();


    // Gọi API cập nhật trạng thái (Dùng cho Nút Hủy Lịch)
    @PUT("/api/lichhen/update-lichhen/{id}")
    Call<BookingResponse> updateBookingStatus(@Path("id") String maLich, @Body UpdateStatusRequest request);

    // Gọi API lấy lịch hẹn của ĐÚNG khách hàng đang đăng nhập
    @GET("/api/lichhen/get-byIdKH-lichhen/{id}")
    Call<LichHen.LichHenRes> getLichHenByKhachHang(@Path("id") String maKH);

    // Kéo thông tin Khách hàng
    @GET("/api/khachhang/get-byId-khachhang/{id}")
    Call<KhachHangResponse> getKhachHangById(@Path("id") String id);

    /// Thay thế hàm update cũ bằng dòng này
    @PUT("/api/khachhang/update-profile/{id}")
    Call<KhachHangResponse> updateKhachHangProfile(@Path("id") String id, @Body UpdateProfileRequest request);

    // Lấy lịch hẹn dành riêng cho Nhân viên (Truyền MATK vào)
    @GET("/api/lichhen/get-byIdNV-lichhen/{matk}")
    Call<StaffBookingResponse> getLichHenCuaNhanVien(@Path("matk") String matk);

    @POST("api/khachhang/insert-khachhangVoiTaiKhoan")
    Call<ResponseBody> registerUser(@Body RegisterRequest request);
}
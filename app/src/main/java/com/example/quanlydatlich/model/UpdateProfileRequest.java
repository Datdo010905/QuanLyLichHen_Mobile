package com.example.quanlydatlich.model;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("customerData")
    private CustomerData customerData;

    @SerializedName("accountData")
    private AccountData accountData;

    // Truyền null vào accountData nếu khách không muốn đổi pass
    public UpdateProfileRequest(CustomerData customerData, AccountData accountData) {
        this.customerData = customerData;
        this.accountData = accountData;
    }

    public static class CustomerData {
        @SerializedName("HOTEN") public String hoTen;
        @SerializedName("EMAIL") public String email;

        public CustomerData(String hoTen, String email) {
            this.hoTen = hoTen;
            this.email = email;
        }
    }

    public static class AccountData {
        @SerializedName("PASS") public String pass;

        public AccountData(String pass) {
            this.pass = pass;
        }
    }
}
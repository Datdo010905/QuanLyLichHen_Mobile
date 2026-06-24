package com.example.quanlydatlich.model;

import com.google.gson.annotations.SerializedName;

public class BookingResponse {
    // --- LỚP VỎ CỦA API TRẢ VỀ ---
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private BookingData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BookingData getData() {
        return data;
    }

    // --- LỚP LÕI BÊN TRONG DATA ---
    public static class BookingData implements java.io.Serializable {
        @SerializedName("newBooking")
        private Object newBooking;

        @SerializedName("newBookingDetail")
        private Object newBookingDetail;

        public Object getNewBooking() {
            return newBooking;
        }

        public Object getNewBookingDetail() {
            return newBookingDetail;
        }
    }
}
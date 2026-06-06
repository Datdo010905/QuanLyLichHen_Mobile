package com.example.quanlydatlich.model;

public class ServiceModel {
    private String name;
    private int imageResId;

    public ServiceModel(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
}

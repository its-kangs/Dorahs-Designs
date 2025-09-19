package com.example.designs;

public class GalleryItem {
    private final int imageResId;
    private final String name;
    private final double price;

    public GalleryItem(int imageResId, String name, double price) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

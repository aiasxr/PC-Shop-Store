package com.softwareengineering.pcstore;

import android.net.Uri;

public class Item {

    private String id, name , price , brand , rating ;
    Uri profile;

    public Item(String id, String name, String price, String brand, String rating, Uri profile) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.rating = rating;
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Uri getProfile() {
        return profile;
    }

    public void setProfile(Uri profile) {
        this.profile = profile;
    }
}

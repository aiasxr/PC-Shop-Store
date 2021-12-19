package com.softwareengineering.pcstore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order {
    String ID;
    String email;
    String date;
    String total;


    public Order() {
    }

    public Order(String ID, String email, String date , String total ) {
        this.ID = ID;
        this.email = email;
        this.total = total;

        this.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

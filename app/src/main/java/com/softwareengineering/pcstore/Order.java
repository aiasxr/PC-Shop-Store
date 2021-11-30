package com.softwareengineering.pcstore;

public class Order {
    String ID;
    String email;
    String total;

    public Order() {
    }

    public Order(String ID, String email, String total) {
        this.ID = ID;
        this.email = email;
        this.total = total;
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

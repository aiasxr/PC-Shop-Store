package com.softwareengineering.pcstore;

public class OrderList {
    String orderID;
    String itemID;
    String count;
    String price;


    public OrderList() {
    }

    public OrderList(String orderID, String itemID, String count, String price) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.count = count;
        this.price = price;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

package com.softwareengineering.pcstore;

public class cartItem {
    String id;
    String email;
    String amount;
    String unitPrice;
    String itemName;

    public cartItem(String id, String itemName ,String email, String amount, String unitPrice) {
        this.id = id;
        this.email = email;
        this.itemName = itemName;
        this.amount = amount;
        this.unitPrice = unitPrice;
    }

    public cartItem() {

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}

package com.example.ondwings;

public class QueueItem {

    String productCode;
    String productName;
    String size;
    String quantity;
    String price;
    double total;
    String date;
    String time;
    String randomCode;
    String addOns; // New field for add-ons

    public QueueItem() {
    }

    public QueueItem(String productCode, String productName, String size, String quantity, String price, double total, String date, String time, String randomCode, String addOns) {
        this.productCode = productCode;
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date = date;
        this.time = time;
        this.randomCode = randomCode;
        this.addOns = addOns; // Assign add-ons
    }


    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getRandomCode() {
        return randomCode;
    }
    public String getAddOns() {
        return addOns;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }
}

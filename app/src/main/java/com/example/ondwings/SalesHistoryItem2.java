package com.example.ondwings;

public class SalesHistoryItem2 {
    private String productCode;
    private String productName;
    private String size;
    private String quantity;
    private String price;
    private double total;
    private String date;
    private String time;
    String addOns; // New field for add-ons

    String random;

    public SalesHistoryItem2() {
        // Default constructor required for Firebase
    }

    public SalesHistoryItem2(String productCode, String productName, String size, String quantity, String price, double total, String date, String time, String addOns) {
        this.productCode = productCode;
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date = date;
        this.time = time;
        this.random = random;
        this.addOns = addOns; // Assign add-ons
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public  String getRandom(){return  random;}
    public void setRandom(String random){this.random = random;}
    public String getAddOns() {
        return addOns;
    }

    public void setAddOns(String addOns) {
        this.addOns = addOns;
    }
}


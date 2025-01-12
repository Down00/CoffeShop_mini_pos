package com.example.ondwings;

public class yearlyItem {

    String date;
    double total;

    public yearlyItem() {
    }

    public yearlyItem(String date, double total) {
        this.date = date;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public double getTotal() {
        return total;
    }
}


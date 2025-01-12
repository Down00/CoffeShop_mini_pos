package com.example.ondwings;

public class dailyItem {

    String date;
    double total;

    public dailyItem() {
    }

    public dailyItem(String date, double total) {
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


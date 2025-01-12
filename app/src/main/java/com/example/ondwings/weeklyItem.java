package com.example.ondwings;

public class weeklyItem {

    String date;
    double total;

    public weeklyItem() {
    }

    public weeklyItem(String date, double total) {
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


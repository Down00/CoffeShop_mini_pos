package com.example.ondwings;

public class monthlyItem {

    String date;
    double total;

    public monthlyItem() {
    }

    public monthlyItem(String date, double total) {
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


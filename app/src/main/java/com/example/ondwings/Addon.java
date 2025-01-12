package com.example.ondwings;

public class Addon {
    private String name;
    private String price;
    private boolean enabled;

    public Addon() {
        // Required empty constructor for Firebase
    }

    public Addon(String name, String price, boolean enabled) {
        this.name = name;
        this.price = price;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

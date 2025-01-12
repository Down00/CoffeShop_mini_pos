package com.example.ondwings;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String id;
    private String productcode;
    private String productname;
    private String price;
    private String pricetall;
    private String pricegrande;
    private int quantity;
    private boolean hasAddons;
    private List<Addon> addons;

    public Item() {
        // Required empty constructor for Firebase
    }

    public Item(String id, String productcode, String productname, String price, 
                String pricetall, String pricegrande, int quantity, boolean hasAddons) {
        this.id = id;
        this.productcode = productcode;
        this.productname = productname;
        this.price = price;
        this.pricetall = pricetall;
        this.pricegrande = pricegrande;
        this.quantity = quantity;
        this.hasAddons = hasAddons;
        this.addons = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPricetall() {
        return pricetall;
    }

    public void setPricetall(String pricetall) {
        this.pricetall = pricetall;
    }

    public String getPricegrande() {
        return pricegrande;
    }

    public void setPricegrande(String pricegrande) {
        this.pricegrande = pricegrande;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isHasAddons() {
        return hasAddons;
    }

    public void setHasAddons(boolean hasAddons) {
        this.hasAddons = hasAddons;
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }

    public void addAddon(Addon addon) {
        if (this.addons == null) {
            this.addons = new ArrayList<>();
        }
        this.addons.add(addon);
    }

    public void removeAddon(Addon addon) {
        if (this.addons != null) {
            this.addons.remove(addon);
        }
    }

    public double calculateTotal(String selectedSize, List<Addon> selectedAddons) {
        double basePrice = 0.0;
        
        // Get base price based on size
        if (selectedSize != null && !selectedSize.isEmpty()) {
            if (selectedSize.equals("Tall") && pricetall != null) {
                basePrice = Double.parseDouble(pricetall);
            } else if (selectedSize.equals("Grande") && pricegrande != null) {
                basePrice = Double.parseDouble(pricegrande);
            }
        } else if (price != null && !price.isEmpty()) {
            basePrice = Double.parseDouble(price);
        }

        // Add addon prices
        double addonTotal = 0.0;
        if (selectedAddons != null) {
            for (Addon addon : selectedAddons) {
                if (addon.isEnabled()) {
                    addonTotal += Double.parseDouble(addon.getPrice());
                }
            }
        }

        return basePrice + addonTotal;
    }
}

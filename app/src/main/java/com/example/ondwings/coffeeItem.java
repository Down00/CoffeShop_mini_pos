package com.example.ondwings;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class coffeeItem {

    private String id;
    private String productcode;
    private String productname;
    private String pricetall;
    private String pricegrande;
    private boolean hasAddons;
    private List<Addon> addons;
    private String random;
    private String price;

    public coffeeItem() {
        // Required empty constructor for Firebase
    }

    public coffeeItem(String id, String productcode, String productname, String pricetall, String pricegrande) {
        this.id = id;
        this.productcode = productcode;
        this.productname = productname;
        this.pricetall = pricetall;
        this.pricegrande = pricegrande;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
}

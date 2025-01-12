package com.example.ondwings;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class menuItem {

    private String id;
    private String productcode;
    private String productname;
    private String price;
    private boolean hasAddons;
    private List<Addon> addons;

    public menuItem() {
        // Required empty constructor for Firebase
    }

    public menuItem(String id, String productcode, String productname, String price) {
        this.id = id;
        this.productcode = productcode;
        this.productname = productname;
        this.price = price;
    }

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

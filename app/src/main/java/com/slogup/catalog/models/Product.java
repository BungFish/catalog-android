package com.slogup.catalog.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Product {

    private String productName;
    private String description;
    private String manufacturer;
    private int price;
    private ProductCategory productCategory;
    private ArrayList<ProductImage> productImageArrayList = new ArrayList<>();

    public Product() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public ArrayList<ProductImage> getProductImageArrayList() {
        return productImageArrayList;
    }

    public void setProductImageArrayList(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject imageObject = jsonArray.getJSONObject(i).getJSONObject("image");
                this.productImageArrayList.add(new ProductImage(imageObject.getInt("id"), imageObject.getString("folder"), imageObject.getString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
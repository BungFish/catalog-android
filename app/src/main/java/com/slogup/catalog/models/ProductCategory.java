package com.slogup.catalog.models;

public class ProductCategory {

    private int productCategoryId;
    private String productCategoryName;

    public ProductCategory(int productCategoryId, String productCategoryName) {
        this.productCategoryId = productCategoryId;
        this.productCategoryName = productCategoryName;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }
}

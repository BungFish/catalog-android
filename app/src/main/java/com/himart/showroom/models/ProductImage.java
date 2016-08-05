package com.himart.showroom.models;

/**
 * Created by CharmSae on 7/22/16.
 */
public class ProductImage {
    private int productImageId;
    private String folder;
    private String name;

    public ProductImage(int productImageId, String folder, String name) {
        this.productImageId = productImageId;
        this.folder = folder;
        this.name = name;
    }

    public int getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(int productImageId) {
        this.productImageId = productImageId;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return folder + "/" + name;
    }

    public String getSmallImageUrlName() {
        return folder + "/s_" + name;
    }
}

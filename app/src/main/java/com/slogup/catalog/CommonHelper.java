package com.slogup.catalog;

import com.slogup.catalog.manager.AppManager;
import com.slogup.catalog.models.Product;
import com.slogup.catalog.network.APIConstants;

import java.text.NumberFormat;

public class CommonHelper {
    public static String moneyFormatter(int number) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(Integer.valueOf(number)) + "원";
    }

    public static String urlFormatter(Product product, int tempPosition) {
        return APIConstants.ROOT_URL_DEVELOPMENT + AppManager.meta.getRootUrl() + product.getProductImageArrayList().get(tempPosition).getImageUrl();
    }
}

package com.himart.showroom.manager;

import android.content.Context;

import com.himart.showroom.models.Metadata;
import com.himart.showroom.models.Product;

import java.util.ArrayList;

public class AppManager {

    public static Context mContext;
    public static boolean isDebug = false;
    public static String appVersion;
    public static Metadata meta;
    public static ArrayList<Product> productArrayList = new ArrayList<>();

}

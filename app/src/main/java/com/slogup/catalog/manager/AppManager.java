package com.slogup.catalog.manager;

import android.content.Context;

import com.slogup.catalog.models.Metadata;
import com.slogup.catalog.models.Product;

import org.json.JSONObject;

import java.util.ArrayList;

public class AppManager {

    public static Context mContext;
    public static boolean isDebug = true;
    public static Metadata meta;
    public static ArrayList<Product> productArrayList = new ArrayList<>();

}

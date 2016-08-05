package com.himart.showroom.network;

public class APIConstants {

    //Development
    public static final String ROOT_URL_DEVELOPMENT = "http://192.168.0.125:3001/";

    //Production
    public static final String ROOT_URL_PRODUCTION = "http://ec2-52-78-72-154.ap-northeast-2.compute.amazonaws.com:8080/";

    //공통 응답 객체 Keys
    public static final String COMMON_RESP_LIST = "list";


    private static final String API_CATALOG = "api/catalog/";
    public static final String API_CATALOG_PRODUCT = API_CATALOG + "products";
    public static final String API_CATALOG_CATEGORY = API_CATALOG + "product-categories";

    private static final String API_ETC = "api/etc/";
    public static final String API_ETC_META = API_ETC + "meta";

    public static final String API_ETC_META_STD = API_ETC_META + "?type=std";

}

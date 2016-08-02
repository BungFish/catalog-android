package com.slogup.catalog.network;

public class APIConstants {

    //Development
    public static final String ROOT_URL_DEVELOPMENT = "http://ec2-52-78-72-154.ap-northeast-2.compute.amazonaws.com:8080/";

    //Production
    public static final String ROOT_URL_PRODUCTION = "http://himart_go.team-slogup.net/";

    //공통 응답 객체 Keys
    public static final String COMMON_RESP_LIST = "list";


    private static final String API_CATALOG = "api/catalog/";
    public static final String API_CATALOG_PRODUCT = API_CATALOG + "products";
    public static final String API_CATALOG_CATEGORY = API_CATALOG + "product-categories";

    private static final String API_ETC = "api/etc/";
    public static final String API_ETC_META = API_ETC + "meta";

}

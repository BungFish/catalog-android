package com.slogup.catalog.network;

/**
 * Created by sngjoong on 16. 1. 23..
 */
public class APIConstants {

    //Development
    public static final String ROOT_URL_DEVELOPMENT = "http://192.168.137.111:3001/";
    //Production
    public static final String ROOT_URL_PRODUCTION = "http://192.168.0.125:3001/";

    //공통 요청 객체 Keys
    public static final String COMMON_PARAM_ROOT_KEY_TRASACTION = "transaction";
    public static final String COMMON_PARAM_ROOT_KEY_ATTRIBUTES = "attributes";
    public static final String COMMON_PARAM_ROOT_KEY_DATASETS = "dataSet";

    // 파라미터 서브오브젝트 키정보
    public static final String COMMON_PARAM_SUB_KEY_OF_TRANSACTION_FID = "fid";
    public static final String COMMON_PARAM_SUB_KEY_OF_TRANSACTION_ID = "id";
    public static final String COMMON_PARAM_SUB_KEY_OF_ATTRIBUTE_FRST_TRNM_CHNL_CD = "FRST_TRNM_CHNL_CD";
    public static final String COMMON_PARAM_SUB_KEY_OF_ATTRIBUTE_SSO_SESN_KEY_RENEW = "SSO_SESN_KEY_RENEW";
    public static final String COMMON_PARAM_SUB_KEY_OF_ATTRIBUTE_SSO_SESN_KEY = "SSO_SESN_KEY";
    public static final String COMMON_PARAM_SUB_KEY_OF_DATASETS_FIELDS = "fields";
    public static final String COMMON_PARAM_SUB_KEY_OF_DATASETS_RECORDS_SETS = "recordSets";

    //공통 응답 객체 Keys
    public static final String COMMON_RESP_LIST = "list";


    private static final String API_CATALOG = "api/catalog/";
    public static final String API_CATALOG_PRODUCT = API_CATALOG + "products";

    private static final String API_ETC = "api/etc/";
    public static final String API_ETC_META = API_ETC + "meta";

}

package com.myshop.core.constant;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String ADDRESSES = API_V1 + "/addresses";
    public static final String CARTS = API_V1 + "/carts";
    public static final String CATEGORIES = API_V1 + "/categories";
    public static final String CATEGORIES_ALL = CATEGORIES + "/**";
    public static final String COUPONS = API_V1 + "/coupons";
    public static final String INVENTORY = API_V1 + "/inventory";
    public static final String ORDERS = API_V1 + "/orders";
    public static final String PRODUCTS = API_V1 + "/products";
    public static final String PRODUCTS_ALL = PRODUCTS + "/**";
    public static final String PROFILES = API_V1 + "/profiles";
    public static final String REPORT = API_V1 + "/report";
    public static final String REVIEWS = API_V1 + "/reviews";

    /** Feign → payment-service */
    public static final String PAYMENT_SERVICE = API_V1 + "/payment";
    public static final String PAYMENT_SERVICE_BY_ID = PAYMENT_SERVICE + "/{id}";
    public static final String PAYMENT_SERVICE_BY_ORDER = PAYMENT_SERVICE + "/order/{orderId}";

    /** Feign → file-service */
    public static final String FILE_SERVICE_PRESIGN = API_V1 + "/files/presign";
}

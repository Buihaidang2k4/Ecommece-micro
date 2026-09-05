package com.myshop.core.constant;

import com.myshop.commons.constants.ApiPrefixes;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String ADDRESSES = ApiPrefixes.V1 + "/addresses";
    public static final String CARTS = ApiPrefixes.V1 + "/carts";
    public static final String CATEGORIES = ApiPrefixes.V1 + "/categories";
    public static final String CATEGORIES_ALL = CATEGORIES + "/**";
    public static final String COUPONS = ApiPrefixes.V1 + "/coupons";
    public static final String INVENTORY = ApiPrefixes.V1 + "/inventory";
    public static final String ORDERS = ApiPrefixes.V1 + "/orders";
    public static final String PRODUCTS = ApiPrefixes.V1 + "/products";
    public static final String PRODUCTS_ALL = PRODUCTS + "/**";
    public static final String PROFILES = ApiPrefixes.V1 + "/profiles";
    public static final String REPORT = ApiPrefixes.V1 + "/report";
    public static final String REVIEWS = ApiPrefixes.V1 + "/reviews";

    /** Feign → payment-service */
    public static final String PAYMENT_SERVICE = ApiPrefixes.V1 + "/payment";
    public static final String PAYMENT_SERVICE_BY_ID = PAYMENT_SERVICE + "/{id}";
    public static final String PAYMENT_SERVICE_BY_ORDER = PAYMENT_SERVICE + "/order/{orderId}";

    /** Feign → file-service */
    public static final String FILE_SERVICE_PRESIGN = ApiPrefixes.V1 + "/files/presign";
}

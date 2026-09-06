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
    public static final String PRODUCT_BY_ID = "/{id}";
    public static final String PRODUCT_BY_SLUG = "/slug/{slug}";
    public static final String PRODUCT_IMAGES = "/{id}/images";
    public static final String PRODUCT_IMAGES_PRESIGN = "/{id}/images/presign";
    public static final String PRODUCT_IMAGE_BY_ID = "/images/{imageId}";

    public static final String PROFILES = ApiPrefixes.V1 + "/profiles";
    public static final String PROFILE_ME = "/me";
    public static final String PROFILE_BY_ID = "/{profileId}";
    public static final String PROFILE_BY_USER = "/by-user/{userId}";
    public static final String PROFILE_ME_AVATAR = "/me/avatar";
    public static final String PROFILE_ME_AVATAR_PRESIGN = "/me/avatar/presign";
    public static final String REPORT = ApiPrefixes.V1 + "/report";
    public static final String REVIEWS = ApiPrefixes.V1 + "/reviews";

    /** Feign → payment-service */
    public static final String PAYMENT_SERVICE = ApiPrefixes.V1 + "/payment";
    public static final String PAYMENT_SERVICE_BY_ID = PAYMENT_SERVICE + "/{id}";
    public static final String PAYMENT_SERVICE_BY_ORDER = PAYMENT_SERVICE + "/order/{orderId}";

    /** Feign → file-service (caller must pass bucket + objectKey) */
    public static final String FILE_SERVICE = ApiPrefixes.V1 + "/files";
    public static final String FILE_SERVICE_PRESIGN = FILE_SERVICE + "/presign";
    public static final String FILE_SERVICE_PRESIGN_UPLOAD = FILE_SERVICE + "/presign-upload";
}

package com.myshop.gateway.constant;

import com.myshop.commons.constants.ApiPrefixes;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String ACTUATOR_ALL = "/actuator/**";
    public static final String AUTH_ALL = ApiPrefixes.V1 + "/auth/**";
    public static final String USER_REGISTRATION = ApiPrefixes.V1 + "/users/registration";
    public static final String FILES_ALL = ApiPrefixes.V1 + "/files/**";
    public static final String IMAGES_ALL = ApiPrefixes.V1 + "/images/**";
    public static final String PRODUCTS_ALL = ApiPrefixes.V1 + "/products/**";
    public static final String CATEGORIES_ALL = ApiPrefixes.V1 + "/categories/**";
    public static final String PAYMENT_VN_PAY_CALLBACK = ApiPrefixes.V1 + "/payment/vn-pay-callback";
}

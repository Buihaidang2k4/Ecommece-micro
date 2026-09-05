package com.myshop.gateway.constant;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String ACTUATOR_ALL = "/actuator/**";
    public static final String AUTH_ALL = API_V1 + "/auth/**";
    public static final String USER_REGISTRATION = API_V1 + "/users/registration";
    public static final String FILES_ALL = API_V1 + "/files/**";
    public static final String IMAGES_ALL = API_V1 + "/images/**";
    public static final String PRODUCTS_ALL = API_V1 + "/products/**";
    public static final String CATEGORIES_ALL = API_V1 + "/categories/**";
    public static final String PAYMENT_VN_PAY_CALLBACK = API_V1 + "/payment/vn-pay-callback";
}
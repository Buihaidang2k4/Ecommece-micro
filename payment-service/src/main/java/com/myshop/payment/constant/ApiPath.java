package com.myshop.payment.constant;

import com.myshop.commons.constants.ApiPrefixes;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String PAYMENT = ApiPrefixes.V1 + "/payment";

    public static final String BY_ID = "/{paymentId}";
    public static final String BY_ORDER = "/order/{orderId}";
    public static final String VNPAY_URL = "/{paymentId}/vnpay-url";
    public static final String VN_PAY_CALLBACK = "/vn-pay-callback";
    public static final String VN_PAY_CALLBACK_FULL = PAYMENT + VN_PAY_CALLBACK;
    public static final String CONFIRM_COD = "/{paymentId}/confirm-cod";
}

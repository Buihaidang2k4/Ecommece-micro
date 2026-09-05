package com.myshop.payment.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public final class VnpayConstants {

    private VnpayConstants() {
    }

    public static final String CURRENCY_VND = "VND";
    public static final String LOCALE_VN = "vn";
    public static final String RESPONSE_SUCCESS = "00";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String DATE_PATTERN = "yyyyMMddHHmmss";
    public static final String TIMEZONE = "Etc/GMT+7";
    public static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";
    public static final String QUERY_SECURE_HASH_PREFIX = "&" + Param.SECURE_HASH + "=";
    public static final String ORDER_INFO_PREFIX = "Payment for order #";
    public static final String FAILED_REASON_PREFIX = "VNPay response code: ";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Param {
        public static final String VERSION = "vnp_Version";
        public static final String COMMAND = "vnp_Command";
        public static final String TMN_CODE = "vnp_TmnCode";
        public static final String CURR_CODE = "vnp_CurrCode";
        public static final String ORDER_TYPE = "vnp_OrderType";
        public static final String LOCALE = "vnp_Locale";
        public static final String RETURN_URL = "vnp_ReturnUrl";
        public static final String CREATE_DATE = "vnp_CreateDate";
        public static final String EXPIRE_DATE = "vnp_ExpireDate";
        public static final String AMOUNT = "vnp_Amount";
        public static final String TXN_REF = "vnp_TxnRef";
        public static final String ORDER_INFO = "vnp_OrderInfo";
        public static final String IP_ADDR = "vnp_IpAddr";
        public static final String BANK_CODE = "vnp_BankCode";
        public static final String SECURE_HASH = "vnp_SecureHash";
        public static final String SECURE_HASH_TYPE = "vnp_SecureHashType";
        public static final String RESPONSE_CODE = "vnp_ResponseCode";
        public static final String TRANSACTION_NO = "vnp_TransactionNo";
        public static final String CARD_TYPE = "vnp_CardType";
    }
}

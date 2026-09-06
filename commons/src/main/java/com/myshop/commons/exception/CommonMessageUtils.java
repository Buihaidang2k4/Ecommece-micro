package com.myshop.commons.exception;

public final class CommonMessageUtils {

    private CommonMessageUtils() {
    }

    public static final class Common {
        private Common() {
        }

        public static final String UNAUTHENTICATED = "common.unauthenticated";
        public static final String UNAUTHORIZED = "common.unauthorized";
        public static final String RESOURCE_NOT_FOUND = "common.resource_not_found";
        public static final String VALIDATION_ERROR = "common.validation_error";
        public static final String INTERNAL_ERROR = "common.internal_error";
        public static final String TOKEN_EXPIRED = "common.token_expired";
        public static final String TOKEN_REVOKED = "common.token_revoked";
    }
}

package com.myshop.commons.exception;

public final class CommonMessageUtils {

    private CommonMessageUtils() {
    }

    public static final class Auth {
        private Auth() {
        }

        public static final String USER_EXISTED = "auth.user.existed";
        public static final String USER_NOT_FOUND = "auth.user.not_found";
        public static final String INVALID_CREDENTIALS = "auth.invalid_credentials";
        public static final String PASSWORD_NOT_MATCHES = "auth.password_not_matches";
        public static final String USER_ALREADY_LOCKED = "auth.user.already_locked";
        public static final String USER_ALREADY_UNLOCKED = "auth.user.already_unlocked";
        public static final String LOCK_REASON_REQUIRED = "auth.lock.reason_required";
        public static final String CANNOT_LOCK_YOURSELF = "auth.lock.cannot_lock_yourself";
        public static final String CANNOT_LOCK_ADMIN = "auth.lock.cannot_lock_admin";
        public static final String ROLE_NOT_FOUND = "auth.role.not_found";
        public static final String ROLE_ALREADY_EXISTS = "auth.role.already_exists";
        public static final String USER_ROLE_NOT_FOUND = "auth.role.user_role_not_found";
        public static final String PERMISSIONS_NOT_FOUND = "auth.permission.not_found";
        public static final String FAILED_CREATE_TOKEN = "auth.token.create_failed";
        public static final String FAILED_BLACKLIST_TOKEN = "auth.token.blacklist_failed";
        public static final String FAILED_SEND_OTP = "auth.otp.send_failed";
    }

    public static final class Core {
        private Core() {
        }

        public static final String PRODUCT_NOT_FOUND = "core.product.not_found";
        public static final String CATEGORY_NOT_FOUND = "core.category.not_found";
        public static final String IMAGE_NOT_FOUND = "core.image.not_found";
        public static final String ADDRESS_NOT_FOUND = "core.address.not_found";
        public static final String PROFILE_NOT_FOUND = "core.profile.not_found";
        public static final String PROFILE_ALREADY_EXISTS = "core.profile.already_exists";
        public static final String CART_NOT_FOUND = "core.cart.not_found";
        public static final String CART_EMPTY = "core.cart.empty";
        public static final String CART_ITEM_NOT_FOUND = "core.cart.item_not_found";
        public static final String CART_ITEM_NOT_IN_CART = "core.cart.item_not_in_cart";
        public static final String ORDER_NOT_FOUND = "core.order.not_found";
        public static final String CANNOT_CANCEL_ORDER = "core.order.cannot_cancel";
        public static final String INVENTORY_NOT_FOUND = "core.inventory.not_found";
        public static final String INSUFFICIENT_STOCK = "core.inventory.insufficient_stock";
        public static final String COUPON_NOT_FOUND = "core.coupon.not_found";
        public static final String COUPON_DISABLED = "core.coupon.disabled";
        public static final String COUPON_NOT_ACTIVE = "core.coupon.not_active";
        public static final String COUPON_EXPIRED = "core.coupon.expired";
        public static final String COUPON_USAGE_LIMIT = "core.coupon.usage_limit";
        public static final String COUPON_MIN_ORDER = "core.coupon.min_order";
        public static final String FAILED_CREATE_PAYMENT = "core.payment.create_failed";
        public static final String REVIEW_NOT_FOUND = "core.review.not_found";
    }

    public static final class Payment {
        private Payment() {
        }

        public static final String ONLY_VNPAY_REDIRECT = "payment.only_vnpay_redirect";
        public static final String ALREADY_PROCESSED = "payment.already_processed";
        public static final String EXPIRED = "payment.expired";
        public static final String INVALID_SIGNATURE = "payment.invalid_signature";
        public static final String NOT_FOUND = "payment.not_found";
        public static final String ONLY_CASH_CONFIRM = "payment.only_cash_confirm";
    }

    public static final class Common {
        private Common() {
        }

        public static final String UNAUTHENTICATED = "common.unauthenticated";
        public static final String UNAUTHORIZED = "common.unauthorized";
        public static final String INVALID_TOKEN = "common.invalid_token";
        public static final String RESOURCE_NOT_FOUND = "common.resource_not_found";
        public static final String VALIDATION_ERROR = "common.validation_error";
        public static final String INTERNAL_ERROR = "common.internal_error";
        public static final String TOKEN_EXPIRED = "common.token_expired";
        public static final String TOKEN_REVOKED = "common.token_revoked";
    }
}

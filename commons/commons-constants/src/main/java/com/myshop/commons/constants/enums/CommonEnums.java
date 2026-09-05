package com.myshop.commons.constants.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Domain status/type codes stored as Integer on entities and API payloads.
 * Nested classes group constants by entity/concept (not Java enums).
 */
public final class CommonEnums {

    private CommonEnums() {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AddressType {
        public static final int HOME = 0;
        public static final int WORK = 1;
        public static final int SCHOOL = 2;
        public static final int OTHER = 3;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CouponScope {
        public static final int GLOBAL = 0;
        public static final int CATEGORY = 1;
        public static final int PRODUCT = 2;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DiscountType {
        public static final int PERCENTAGE = 0;
        public static final int FIXED_AMOUNT = 1;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OrderStatus {
        public static final int CREATED = 0;
        public static final int PENDING = 1;
        public static final int SHIPPED = 2;
        public static final int DELIVERED = 3;
        public static final int CANCELLED = 4;
        public static final int RETURNED = 5;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PaymentMethod {
        public static final int VNPAY = 0;
        public static final int MOMO = 1;
        public static final int CASH = 2;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PaymentStatus {
        public static final int INIT = 0;
        public static final int UNPAID = 1;
        public static final int PAID = 2;
        public static final int PENDING = 3;
        public static final int FAILED = 4;
        public static final int EXPIRED = 5;
    }
}

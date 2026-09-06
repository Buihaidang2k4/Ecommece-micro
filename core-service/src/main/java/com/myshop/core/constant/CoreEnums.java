package com.myshop.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Domain numeric codes owned by core-service (not shared across services).
 */
public final class CoreEnums {

    private CoreEnums() {
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AddressType {
        public static final int HOME = 0;
        public static final int WORK = 1;
        public static final int SCHOOL = 2;
        public static final int OTHER = 3;
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
}

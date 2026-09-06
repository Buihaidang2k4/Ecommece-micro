package com.myshop.payment.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Domain numeric codes owned by payment-service (not shared across services).
 */
public final class PaymentEnums {

    private PaymentEnums() {
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

package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {

    // ========== SYSTEM ==========
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid key", BAD_REQUEST),
    REDIS_ERROR(1028, "Redis error", INTERNAL_SERVER_ERROR),
    FORBIDDEN(1029, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),

    // ========== AUTH ==========
    UNAUTHENTICATED(1001, "Authentication required", HttpStatus.UNAUTHORIZED),           // 401
    UNAUTHORIZED(1002, "You do not have permission", HttpStatus.FORBIDDEN),              // 403
    TOKEN_EXPIRED(1003, "Token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1004, "Token has been revoked or is no longer valid", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_ALLOWED(1005, "Role is not allowed for this action", HttpStatus.FORBIDDEN),
    DATABASE_ERROR(1006, "Database failed to check backlist for token ", INTERNAL_SERVER_ERROR),

    // ========== USER ==========
    USER_EXISTED(1100, "User already exists", BAD_REQUEST),
    USER_NOT_EXISTED(1101, "User not found", NOT_FOUND),
    USER_INVALID(1102, "Username must be at least {min} characters", BAD_REQUEST),
    INVALID_PASSWORD(1103, "Password must be at least {min} characters", BAD_REQUEST),
    INVALID_CREDENTIALS(1104, "Incorrect username or password", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCHES(1105, "Current password is incorrect", BAD_REQUEST),
    PASSWORD_CONFIRM_NOT_MATCHES(1106, "New password and confirm password do not match", HttpStatus.BAD_REQUEST),
    USER_ALREADY_LOCKED(1107, "This user account is already locked.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_UNLOCKED(1108, "This user account is already unlocked.", HttpStatus.BAD_REQUEST),
    LOCK_REASON_REQUIRED(1109, "Lock reason is required", HttpStatus.BAD_REQUEST),
    CANNOT_LOCK_YOURSELF(1110, "You cannot lock yourself", HttpStatus.BAD_REQUEST),
    CANNOT_LOCK_ADMIN(1111, "You cannot lock ADMIN", HttpStatus.BAD_REQUEST),

    // ========== PROFILE ==========
    PROFILE_EXISTED(1200, "Profile already exists", BAD_REQUEST),
    PROFILE_NOT_EXISTED(1201, "Profile not exists", BAD_REQUEST),

    // ========== ADDRESS ==========
    ADDRESS_NOT_EXISTED(1300, "Address not found", NOT_FOUND),
    ADDRESS_EXISTED(1301, "Address already exists", BAD_REQUEST),

    // ========== PRODUCT ==========
    PRODUCT_NOT_EXISTED(1400, "Product not found", NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1401, "Product is out of stock", BAD_REQUEST),
    PRODUCT_NOT_ENOUGH(1402, "Requested: %d, Available: %d", BAD_REQUEST),
    PRODUCT_HAS_ORDERS(1403, "Product has orders and cannot be deleted", CONFLICT),
    PRODUCT_NAME_IS_EXISTED(1404, "Product name is existed", BAD_REQUEST),

    // ========== CATEGORY ==========
    CATEGORY_NOT_EXISTED(1500, "Category not found", NOT_FOUND),
    CATEGORY_EXISTED(1501, "Category already exists", BAD_REQUEST),

    // ========== PAYMENT ==========
    PAYMENT_EXISTED(1600, "Payment already exists", BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1601, "Payment not found", NOT_FOUND),
    PAYMENT_METHOD_NOT_SUPPORT(1602, "Payment method not support", NOT_FOUND),
    PAYMENT_FAILED(1602, "Process payment failed", NOT_FOUND),
    PAYMENT_DECLINED(1602, "Payment declined", NOT_FOUND),
    INVALID_PAYMENT_METHOD(1602, "Invalid payment method", NOT_FOUND),
    PAYMENT_NOT_PAID(1603, "Payment must be PAID before confirming delivery", HttpStatus.CONFLICT),
    BANK_CODE_REQUIRED(1604, "Bank code is required for this payment method", BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED(1605, "Payment has already been processed", HttpStatus.CONFLICT),
    PAYMENT_EXPIRED(1606, "Payment session has expired", HttpStatus.GONE),


    // ========== CART ==========
    CART_NOT_EXISTED(1700, "Cart not found", NOT_FOUND),
    CART_EXISTED(1701, "Cart already exists", BAD_REQUEST),
    CART_ITEM_NOT_EXISTED(1702, "Cart item not found", NOT_FOUND),
    CART_ITEM_EXISTED(1703, "Cart item already exists", BAD_REQUEST),
    CART_NOT_MATCH(1704, "Cart does not match with cart item", BAD_REQUEST),
    CART_EMPTY(1704, "Your cart is empty", BAD_REQUEST),
    LIST_CART_ITEMS_EMPTY(1705, "List cart items is empty", BAD_REQUEST),

    // ========== ORDER ==========
    ORDER_EXISTED(1800, "Order already exists", BAD_REQUEST),
    ORDER_NOT_EXISTED(1801, "Order not found", NOT_FOUND),
    ORDER_ITEM_NOT_EXISTED(1802, "Order item not found", NOT_FOUND),
    ORDER_ITEM_EXISTED(1803, "Order item already exists", BAD_REQUEST),
    ORDER_CANCEL_FAILED(1804, "Cannot cancel shipped or delivered orders", BAD_REQUEST),
    ORDER_ITEM_EMPTY(1805, "Order item is empty", BAD_REQUEST),
    ORDER_ALREADY_DELIVERED(1806, "The order has been delivered but cannot be confirmed.", BAD_REQUEST),
    ORDER_ALREADY_CANCELLED(1807, "The order has been cancelled but cannot be update status.", BAD_REQUEST),
    ORDER_STATUS_FINAL(1808, "Unable to change completed order", BAD_REQUEST),
    ORDER_NOT_DELIVERED(1809, "he order has not been delivered.", BAD_REQUEST),
    ORDER_NOT_BELONG_TO_USER(1809, "You did not place this order.", BAD_REQUEST),
    ORDER_CANNOT_CHANGE_ADDRESS(1810, "The delivery address cannot be changed because the shipment has already been made.", BAD_REQUEST),
    ORDER_ADDRESS_MISMATCH(1810, "The delivery address you selected does not match the customer associated with this order.", BAD_REQUEST),

    // ========== INVENTORY ==========
    INVENTORY_DOES_NOT_EXIST(1900, "Inventory does not exist", NOT_FOUND),
    INVENTORY_NOT_ENOUGH(1901, "Not enough stock:  available: %d  , requestQuantity: %d", BAD_REQUEST),
    CANNOT_CANCEL_RESERVATION(1902, "Cannot cancel this reservation", BAD_REQUEST),
    NOT_ENOUGH_RESERVED_STOCK(1903, "Not enough reserved stock", BAD_REQUEST),

    // ========== IMAGE ==========
    IMAGE_NOT_FOUND(2000, "Image not found", NOT_FOUND),

    // ========== VALIDATION ==========
    VALIDATION_ERROR(2100, "Input data is not valid", BAD_REQUEST),

    // ========== COUPON ==========
    COUPON_INVALID(2200, "This coupon is invalid or cannot be applied to your order.", BAD_REQUEST),
    COUPON_LIMIT_PER_USER_EXCEEDED(2200, "Coupon limit per user exceeded", BAD_REQUEST),
    COUPON_INVALID_PERCENTAGE(2201, "Invalid percentage discount value", BAD_REQUEST),
    COUPON_INVALID_FIXED_AMOUNT(2202, "Invalid fixed amount value", BAD_REQUEST),
    COUPON_INVALID_TYPE(2203, "Invalid coupon discount type", BAD_REQUEST),
    COUPON_INVALID_DATE_RANGE(2204, "Start date must be before expiry date", BAD_REQUEST),
    COUPON_CODE_IS_EXISTED(2205, "Coupon code already exists", BAD_REQUEST),
    COUPON_INVALID_USAGE_LIMIT(2206, "Used count cannot exceed usage limit", BAD_REQUEST),
    COUPON_INVALID_MAX_USES_PER_USER(2208, "Max uses per user must be >= 1 when per-user limit is enabled", BAD_REQUEST),
    COUPON_NOT_EXISTED(2209, "Coupon not existed", BAD_REQUEST),
    COUPON_EXPIRED(2210, "Coupon has already expired", BAD_REQUEST),
    COUPON_ALREADY_STARTED(2211, "Coupon has already started and cannot be modified", BAD_REQUEST),
    EXPIRY_BEFORE_START(2212, "Coupon expiry date must be after start date", BAD_REQUEST),
    CANNOT_ENABLE_EXPIRED_COUPON(2213, "Cannot enable an expired coupon", BAD_REQUEST),
    COUPON_ALREADY_USED(2214, "Coupon has already been used and cannot be deleted", BAD_REQUEST),
    CANNOT_DELETE_ACTIVE_COUPON(2215, "Cannot delete an active coupon", BAD_REQUEST),
    COUPON_INVALID_SCOPE(2216, "Invalid coupon scope", BAD_REQUEST),
    COUPON_SCOPE_CONFLICT(2217, "Coupon scope conflicts with provided categories or products", CONFLICT),
    COUPON_CATEGORY_REQUIRED(2218, "Category IDs are required when coupon scope is CATEGORY", BAD_REQUEST),
    COUPON_PRODUCT_REQUIRED(2219, "Product IDs are required when coupon scope is PRODUCT", BAD_REQUEST),
    CANNOT_UPDATE_START_DATE(2220, "Cannot update start date after the coupon has started", BAD_REQUEST),
    CANNOT_SHORTEN_EXPIRY(2221, "Cannot shorten the expiry date of an active coupon", BAD_REQUEST),
    CANNOT_UPDATE_COUPON_AFTER_USED(2222, "Cannot update coupon rules after it has been used", BAD_REQUEST),
    COUPON_CATEGORY_NOT_APPLICABLE(2223, "Coupon not applicable for selected categories", BAD_REQUEST),
    COUPON_PRODUCT_NOT_APPLICABLE(2224, "Coupon not applicable for selected products", BAD_REQUEST),


    // =========== REVIEW =================
    REVIEW_NOT_PURCHASED(2300, "You cannot review this item because you haven’t purchased it or it has not been delivered yet.", BAD_REQUEST),
    REVIEW_ALREADY_EXISTS(2301, "Review already exists in this order.", BAD_REQUEST),
    REVIEW_ALREADY_EXISTS_FOR_THIS_ORDER(2302, "Review already exists for this order", BAD_REQUEST),
    REVIEW_NOT_EXISTED(2303, "Review is not found", BAD_REQUEST),

    // ============ EMAIL ===============
    EMAIL_NOT_FOUND(2400, "Email not found", HttpStatus.NOT_FOUND),
    OTP_INVALID(2401, "OTP is invalid", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(2402, "OTP has expired", HttpStatus.BAD_REQUEST);

    final int code;
    final String messageTemplate;
    final HttpStatus httpStatus;

    ErrorCode(int code, String messageTemplate, HttpStatus httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }
}

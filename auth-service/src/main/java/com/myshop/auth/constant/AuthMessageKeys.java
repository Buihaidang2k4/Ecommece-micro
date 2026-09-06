package com.myshop.auth.constant;

public final class AuthMessageKeys {

    private AuthMessageKeys() {
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
    public static final String OTP_INVALID = "auth.otp.invalid";
    public static final String USER_INVALID = "auth.user.invalid";
}

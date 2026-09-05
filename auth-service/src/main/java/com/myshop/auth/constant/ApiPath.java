package com.myshop.auth.constant;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String AUTH = API_V1 + "/auth";
    public static final String AUTH_PREFIX = AUTH + "/";
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String USERS = API_V1 + "/users";
    public static final String USER_REGISTRATION = USERS + "/registration";
    public static final String ADMIN_ROLES = API_V1 + "/admin/roles";
    public static final String ADMIN_PERMISSIONS = API_V1 + "/admin/permissions";
    public static final String ME_PERMISSIONS = AUTH + "/me/permissions";
}

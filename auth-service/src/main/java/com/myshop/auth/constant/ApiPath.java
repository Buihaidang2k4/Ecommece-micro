package com.myshop.auth.constant;

import com.myshop.commons.constants.ApiPrefixes;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String AUTH = ApiPrefixes.V1 + "/auth";
    public static final String AUTH_PREFIX = AUTH + "/";
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String USERS = ApiPrefixes.V1 + "/users";
    public static final String USER_REGISTRATION = USERS + "/registration";
    public static final String ADMIN_ROLES = ApiPrefixes.V1 + "/admin/roles";
    public static final String ADMIN_PERMISSIONS = ApiPrefixes.V1 + "/admin/permissions";
    public static final String ME_PERMISSIONS = AUTH + "/me/permissions";
}

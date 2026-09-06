package com.myshop.commons.constants;

public final class JwtConstants {

    private JwtConstants() {
    }

    public static final String ISSUER = "myshop-auth-service";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_SCOPE = "scope";
    public static final String TYPE_ACCESS = "access_token";
    public static final String TYPE_REFRESH = "refresh_token";
    public static final String ROLE_PREFIX = "ROLE_";
}

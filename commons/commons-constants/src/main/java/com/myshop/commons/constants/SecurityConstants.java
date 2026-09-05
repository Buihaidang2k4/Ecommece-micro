package com.myshop.commons.constants;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String ACCESS_COOKIE = "access_token";
    public static final String REFRESH_COOKIE = "refresh_token";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String ACTUATOR_PREFIX = "/actuator/";
    public static final String ACTUATOR_ALL = "/actuator/**";

    public static final String SWAGGER_UI_PREFIX = "/swagger-ui/";
    public static final String SWAGGER_UI_ALL = "/swagger-ui/**";
    public static final String V3_API_DOCS_PREFIX = "/v3/api-docs";
    public static final String V3_API_DOCS_ALL = "/v3/api-docs/**";
    public static final String SWAGGER_RESOURCES_PREFIX = "/swagger-resources/";
    public static final String SWAGGER_RESOURCES_ALL = "/swagger-resources/**";
    public static final String WEBJARS_PREFIX = "/webjars/";
    public static final String WEBJARS_ALL = "/webjars/**";
}

package com.myshop.commons.constants;

/**
 * Single source for HTTP API version prefix used by controllers / security matchers.
 * YAML counterpart: {@code api.prefix} in config-*.commons (gateway routes).
 */
public final class ApiPrefixes {

    private ApiPrefixes() {
    }

    public static final String V1 = "/api/v1";
}

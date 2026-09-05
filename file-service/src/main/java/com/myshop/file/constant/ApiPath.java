package com.myshop.file.constant;

import com.myshop.commons.constants.ApiPrefixes;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String FILES = ApiPrefixes.V1 + "/files";

    public static final String UPLOAD = "/upload";
    public static final String PRESIGN_UPLOAD = "/presign-upload";
    public static final String PRESIGN = "/presign";
    public static final String PRESIGN_FULL = FILES + PRESIGN;
}

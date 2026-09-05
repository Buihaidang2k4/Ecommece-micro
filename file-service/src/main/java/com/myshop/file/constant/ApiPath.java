package com.myshop.file.constant;

public final class ApiPath {

    private ApiPath() {
    }

    public static final String API_V1 = "/api/v1";
    public static final String FILES = API_V1 + "/files";

    public static final String UPLOAD = "/upload";
    public static final String PRESIGN_UPLOAD = "/presign-upload";
    public static final String PRESIGN = "/presign";
    public static final String PRESIGN_FULL = FILES + PRESIGN;
}

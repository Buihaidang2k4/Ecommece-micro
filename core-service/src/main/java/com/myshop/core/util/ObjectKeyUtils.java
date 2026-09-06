package com.myshop.core.util;

import java.util.UUID;

public final class ObjectKeyUtils {

    private ObjectKeyUtils() {
    }

    public static String productImageKey(Long productId, String fileName) {
        return "products/" + productId + "/" + UUID.randomUUID() + "-" + sanitize(fileName);
    }

    public static String avatarKey(Long userId, String fileName) {
        return "avatars/" + userId + "/" + UUID.randomUUID() + "-" + sanitize(fileName);
    }

    private static String sanitize(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "file";
        }
        return fileName.replaceAll("[\\\\/\\s]+", "_");
    }
}

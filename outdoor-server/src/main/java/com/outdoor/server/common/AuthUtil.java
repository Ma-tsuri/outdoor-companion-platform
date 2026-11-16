package com.outdoor.server.common;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static void requireAdmin(Integer role) {
        if (role == null || role != 2) {
            throw new RuntimeException("无管理员权限");
        }
    }

    public static void requireGuide(Integer role) {
        if (role == null || (role != 1 && role != 2)) {
            throw new RuntimeException("仅领队可操作");
        }
    }
}

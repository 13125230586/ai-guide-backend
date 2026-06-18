package com.aiguide.platform.common.util;

import cn.hutool.crypto.digest.DigestUtil;

public class PasswordUtils {
    private static final String SALT = "aiguide_platform_2026";

    public static String encrypt(String password) {
        return DigestUtil.md5Hex(SALT + password);
    }

    public static boolean match(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}

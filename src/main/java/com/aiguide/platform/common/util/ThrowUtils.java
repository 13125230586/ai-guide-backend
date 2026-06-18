package com.aiguide.platform.common.util;

import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;

public class ThrowUtils {
    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition) {
            throw e;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw new BusinessException(errorCode);
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        if (condition) {
            throw new BusinessException(errorCode, message);
        }
    }
}

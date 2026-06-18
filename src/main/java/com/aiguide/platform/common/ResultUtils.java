package com.aiguide.platform.common;

import com.aiguide.platform.common.exception.ErrorCode;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.success(data);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return BaseResponse.error(code, message);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return BaseResponse.error(errorCode);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}

package com.aiguide.platform.common;

import com.aiguide.platform.common.exception.ErrorCode;
import lombok.Data;
import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage());
    }
}

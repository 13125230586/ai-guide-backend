package com.aiguide.platform.common.util;

import com.aiguide.platform.auth.support.SessionHelper;
import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserContextUtil {

    public static Long getLoginUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = SessionHelper.getLoginUserId(session);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userId;
    }

    public static String getRoleCode(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return SessionHelper.getRoleCode(session);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        try {
            return "ADMIN".equals(getRoleCode(request));
        } catch (Exception e) {
            return false;
        }
    }
}

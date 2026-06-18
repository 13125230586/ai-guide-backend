package com.aiguide.platform.auth.support;

import com.aiguide.platform.common.constant.SessionConstant;

import javax.servlet.http.HttpSession;

public class SessionHelper {

    public static void setLoginInfo(HttpSession session, Long userId, String username, String roleCode) {
        session.setAttribute(SessionConstant.USER_ID, userId);
        session.setAttribute(SessionConstant.USERNAME, username);
        session.setAttribute(SessionConstant.ROLE_CODE, roleCode);
        session.setAttribute(SessionConstant.LOGIN_USER, true);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(SessionConstant.LOGIN_USER));
    }

    public static Long getLoginUserId(HttpSession session) {
        return (Long) session.getAttribute(SessionConstant.USER_ID);
    }

    public static String getUsername(HttpSession session) {
        return (String) session.getAttribute(SessionConstant.USERNAME);
    }

    public static String getRoleCode(HttpSession session) {
        return (String) session.getAttribute(SessionConstant.ROLE_CODE);
    }

    public static void invalidate(HttpSession session) {
        session.invalidate();
    }
}

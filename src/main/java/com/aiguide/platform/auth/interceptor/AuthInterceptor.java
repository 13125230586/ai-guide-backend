package com.aiguide.platform.auth.interceptor;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.auth.annotation.RequireLogin;
import com.aiguide.platform.auth.support.SessionHelper;
import com.aiguide.platform.common.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法级和类级注解
        RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }
        if (requireLogin == null) {
            requireLogin = handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
        }

        HttpSession session = request.getSession(false);

        // 未登录校验
        if (session == null || !SessionHelper.isLoggedIn(session)) {
            if (requireLogin != null || requireAdmin != null) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":40100,\"message\":\"未登录\",\"data\":null}");
                return false;
            }
            return true;
        }

        // 管理员权限校验
        if (requireAdmin != null) {
            String roleCode = SessionHelper.getRoleCode(session);
            if (!BusinessConstant.ROLE_ADMIN.equals(roleCode)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":40101,\"message\":\"无权限\",\"data\":null}");
                return false;
            }
        }

        return true;
    }
}

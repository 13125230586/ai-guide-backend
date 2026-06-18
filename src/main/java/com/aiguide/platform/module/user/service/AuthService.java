package com.aiguide.platform.module.user.service;

import com.aiguide.platform.module.user.model.req.LoginReq;
import com.aiguide.platform.module.user.model.req.RegisterReq;
import com.aiguide.platform.module.user.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Long register(RegisterReq req);

    LoginUserVO login(LoginReq req, HttpServletRequest request);

    void logout(HttpServletRequest request);

    LoginUserVO getLoginUser(HttpServletRequest request);
}

package com.aiguide.platform.auth.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.module.user.model.req.LoginReq;
import com.aiguide.platform.module.user.model.req.RegisterReq;
import com.aiguide.platform.module.user.model.vo.LoginUserVO;
import com.aiguide.platform.module.user.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> register(@Valid @RequestBody RegisterReq req) {
        return ResultUtils.success(authService.register(req));
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<LoginUserVO> login(@Valid @RequestBody LoginReq req, HttpServletRequest request) {
        return ResultUtils.success(authService.login(req, request));
    }

    @PostMapping("/logout")
    @ApiOperation("用户退出")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResultUtils.success(true);
    }

    @GetMapping("/me")
    @ApiOperation("获取当前登录用户")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        return ResultUtils.success(authService.getLoginUser(request));
    }
}

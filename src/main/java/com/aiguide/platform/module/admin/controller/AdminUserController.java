package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.user.model.req.UserPageReq;
import com.aiguide.platform.module.user.model.req.UserStatusUpdateReq;
import com.aiguide.platform.module.user.model.vo.UserVO;
import com.aiguide.platform.module.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/user")
@RequireAdmin
@Api(tags = "管理端-用户管理")
public class AdminUserController {

    @Resource
    private UserService userService;

    @GetMapping("/page")
    @ApiOperation("用户分页")
    public BaseResponse<PageResponse<UserVO>> page(UserPageReq req) {
        return ResultUtils.success(userService.pageUsers(req));
    }

    @PostMapping("/status")
    @ApiOperation("更新用户状态")
    public BaseResponse<Boolean> updateStatus(@Valid @RequestBody UserStatusUpdateReq req) {
        userService.updateUserStatus(req);
        return ResultUtils.success(true);
    }
}

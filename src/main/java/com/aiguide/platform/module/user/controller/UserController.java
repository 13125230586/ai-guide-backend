package com.aiguide.platform.module.user.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.module.file.manager.FileStorageManager;
import com.aiguide.platform.module.user.model.req.PasswordChangeReq;
import com.aiguide.platform.module.user.model.req.UserUpdateReq;
import com.aiguide.platform.module.user.model.vo.LoginUserVO;
import com.aiguide.platform.module.user.service.AuthService;
import com.aiguide.platform.module.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;
    @Resource
    private FileStorageManager fileStorageManager;

    @GetMapping("/profile")
    @ApiOperation("获取个人信息")
    public BaseResponse<LoginUserVO> getProfile(HttpServletRequest request) {
        return ResultUtils.success(authService.getLoginUser(request));
    }

    @PostMapping("/update")
    @ApiOperation("更新个人信息")
    public BaseResponse<Boolean> updateProfile(@RequestBody UserUpdateReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        userService.updateUserProfile(userId, req);
        return ResultUtils.success(true);
    }

    @PostMapping("/change-password")
    @ApiOperation("修改密码")
    public BaseResponse<Boolean> changePassword(@Valid @RequestBody PasswordChangeReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        return ResultUtils.success(true);
    }

    @PostMapping("/avatar/upload")
    @ApiOperation("上传头像")
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        String avatarUrl = fileStorageManager.uploadFile(file, "user_avatar");
        // 更新用户头像
        UserUpdateReq updateReq = new UserUpdateReq();
        updateReq.setAvatarUrl(avatarUrl);
        userService.updateUserProfile(userId, updateReq);
        return ResultUtils.success(avatarUrl);
    }
}

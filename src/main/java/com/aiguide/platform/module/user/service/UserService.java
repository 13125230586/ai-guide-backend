package com.aiguide.platform.module.user.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.module.user.model.req.UserPageReq;
import com.aiguide.platform.module.user.model.req.UserStatusUpdateReq;
import com.aiguide.platform.module.user.model.req.UserUpdateReq;
import com.aiguide.platform.module.user.model.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<SysUser> {
    UserVO getUserVO(SysUser user);

    void updateUserProfile(Long userId, UserUpdateReq req);

    void changePassword(Long userId, String oldPassword, String newPassword);

    PageResponse<UserVO> pageUsers(UserPageReq req);

    void updateUserStatus(UserStatusUpdateReq req);
}

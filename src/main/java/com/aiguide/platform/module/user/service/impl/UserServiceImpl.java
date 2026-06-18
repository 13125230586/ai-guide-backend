package com.aiguide.platform.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.constant.BusinessConstant;
import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.PasswordUtils;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.SysUserMapper;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.module.user.model.req.UserPageReq;
import com.aiguide.platform.module.user.model.req.UserStatusUpdateReq;
import com.aiguide.platform.module.user.model.req.UserUpdateReq;
import com.aiguide.platform.module.user.model.vo.UserVO;
import com.aiguide.platform.module.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Override
    public UserVO getUserVO(SysUser user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRoleCode(user.getRoleCode());
        vo.setUserStatus(user.getUserStatus());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserUpdateReq req) {
        SysUser user = new SysUser();
        user.setId(userId);
        if (StringUtils.isNotBlank(req.getNickname())) user.setNickname(req.getNickname());
        if (req.getAvatarUrl() != null) user.setAvatarUrl(req.getAvatarUrl());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        ThrowUtils.throwIf(!updateById(user), ErrorCode.OPERATION_ERROR, "更新失败");
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!PasswordUtils.match(oldPassword, user.getPassword()),
                ErrorCode.PARAMS_ERROR, "旧密码不正确");
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(PasswordUtils.encrypt(newPassword));
        ThrowUtils.throwIf(!updateById(update), ErrorCode.OPERATION_ERROR, "修改密码失败");
    }

    @Override
    public PageResponse<UserVO> pageUsers(UserPageReq req) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(req.getUsername()), SysUser::getUsername, req.getUsername());
        wrapper.eq(StringUtils.isNotBlank(req.getRoleCode()), SysUser::getRoleCode, req.getRoleCode());
        wrapper.eq(req.getUserStatus() != null, SysUser::getUserStatus, req.getUserStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<UserVO> voList = page.getRecords().stream().map(this::getUserVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    @Transactional
    public void updateUserStatus(UserStatusUpdateReq req) {
        SysUser user = getById(req.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        // 不允许禁用管理员
        ThrowUtils.throwIf(BusinessConstant.ROLE_ADMIN.equals(user.getRoleCode()),
                ErrorCode.PARAMS_ERROR, "不允许修改管理员状态");
        SysUser update = new SysUser();
        update.setId(req.getId());
        update.setUserStatus(req.getUserStatus());
        ThrowUtils.throwIf(!updateById(update), ErrorCode.OPERATION_ERROR);
    }
}

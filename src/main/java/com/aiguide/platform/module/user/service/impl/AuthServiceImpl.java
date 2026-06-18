package com.aiguide.platform.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiguide.platform.auth.support.SessionHelper;
import com.aiguide.platform.common.constant.BusinessConstant;
import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.util.PasswordUtils;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.SysUserMapper;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.module.user.model.req.LoginReq;
import com.aiguide.platform.module.user.model.req.RegisterReq;
import com.aiguide.platform.module.user.model.vo.LoginUserVO;
import com.aiguide.platform.module.user.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public Long register(RegisterReq req) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, req.getUsername());
        ThrowUtils.throwIf(sysUserMapper.selectCount(wrapper) > 0,
                ErrorCode.PARAMS_ERROR, "用户名已存在");

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(PasswordUtils.encrypt(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRoleCode(BusinessConstant.ROLE_TOURIST);
        user.setUserStatus(1);
        ThrowUtils.throwIf(sysUserMapper.insert(user) <= 0,
                ErrorCode.OPERATION_ERROR, "注册失败");
        return user.getId();
    }

    @Override
    public LoginUserVO login(LoginReq req, HttpServletRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, req.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        ThrowUtils.throwIf(!PasswordUtils.match(req.getPassword(), user.getPassword()),
                ErrorCode.PARAMS_ERROR, "密码错误");
        ThrowUtils.throwIf(user.getUserStatus() == 0,
                ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");

        // 更新最后登录时间
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setLastLoginTime(new Date());
        sysUserMapper.updateById(update);

        // 写入 Session
        HttpSession session = request.getSession();
        SessionHelper.setLoginInfo(session, user.getId(), user.getUsername(), user.getRoleCode());

        return toLoginUserVO(user);
    }

    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            SessionHelper.invalidate(session);
        }
    }

    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !SessionHelper.isLoggedIn(session)) {
            return null;
        }
        Long userId = SessionHelper.getLoginUserId(session);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) return null;
        return toLoginUserVO(user);
    }

    private LoginUserVO toLoginUserVO(SysUser user) {
        LoginUserVO vo = new LoginUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRoleCode(user.getRoleCode());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}

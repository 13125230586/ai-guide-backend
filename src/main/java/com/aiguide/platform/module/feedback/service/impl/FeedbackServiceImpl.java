package com.aiguide.platform.module.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.SysUserMapper;
import com.aiguide.platform.mapper.UserFeedbackMapper;
import com.aiguide.platform.model.entity.SysUser;
import com.aiguide.platform.model.entity.UserFeedback;
import com.aiguide.platform.module.feedback.model.req.FeedbackCreateReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackPageReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackReplyReq;
import com.aiguide.platform.module.feedback.model.vo.FeedbackVO;
import com.aiguide.platform.module.feedback.service.FeedbackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback>
        implements FeedbackService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public Long createFeedback(Long userId, FeedbackCreateReq req) {
        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(userId);
        feedback.setFeedbackType(req.getFeedbackType());
        feedback.setContent(req.getContent());
        feedback.setContactInfo(req.getContactInfo());
        feedback.setFeedbackStatus(0);
        ThrowUtils.throwIf(!save(feedback), ErrorCode.OPERATION_ERROR);
        return feedback.getId();
    }

    @Override
    public PageResponse<FeedbackVO> pageMyFeedbacks(Long userId, FeedbackPageReq req) {
        LambdaQueryWrapper<UserFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFeedback::getUserId, userId);
        wrapper.eq(req.getFeedbackStatus() != null, UserFeedback::getFeedbackStatus, req.getFeedbackStatus());
        wrapper.orderByDesc(UserFeedback::getCreateTime);
        Page<UserFeedback> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<FeedbackVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    public PageResponse<FeedbackVO> pageAllFeedbacks(FeedbackPageReq req) {
        LambdaQueryWrapper<UserFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(req.getFeedbackType()), UserFeedback::getFeedbackType, req.getFeedbackType());
        wrapper.eq(req.getFeedbackStatus() != null, UserFeedback::getFeedbackStatus, req.getFeedbackStatus());
        wrapper.orderByDesc(UserFeedback::getCreateTime);
        Page<UserFeedback> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<FeedbackVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    @Transactional
    public void replyFeedback(FeedbackReplyReq req) {
        UserFeedback feedback = getById(req.getId());
        ThrowUtils.throwIf(feedback == null, ErrorCode.NOT_FOUND_ERROR);
        feedback.setReplyContent(req.getReplyContent());
        feedback.setReplyTime(new Date());
        feedback.setFeedbackStatus(req.getFeedbackStatus() != null ? req.getFeedbackStatus() : 1);
        ThrowUtils.throwIf(!updateById(feedback), ErrorCode.OPERATION_ERROR);
    }

    private FeedbackVO toVO(UserFeedback feedback) {
        FeedbackVO vo = new FeedbackVO();
        vo.setId(feedback.getId());
        vo.setUserId(feedback.getUserId());
        vo.setFeedbackType(feedback.getFeedbackType());
        vo.setContent(feedback.getContent());
        vo.setContactInfo(feedback.getContactInfo());
        vo.setFeedbackStatus(feedback.getFeedbackStatus());
        vo.setReplyContent(feedback.getReplyContent());
        vo.setReplyTime(feedback.getReplyTime());
        vo.setCreateTime(feedback.getCreateTime());
        SysUser user = sysUserMapper.selectById(feedback.getUserId());
        if (user != null) vo.setUsername(user.getUsername());
        return vo;
    }
}

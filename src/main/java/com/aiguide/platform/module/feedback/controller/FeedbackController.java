package com.aiguide.platform.module.feedback.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.module.feedback.model.req.FeedbackCreateReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackPageReq;
import com.aiguide.platform.module.feedback.model.vo.FeedbackVO;
import com.aiguide.platform.module.feedback.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/feedback")
@Api(tags = "反馈管理")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @PostMapping("/create")
    @ApiOperation("提交反馈")
    public BaseResponse<Long> create(@Valid @RequestBody FeedbackCreateReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(feedbackService.createFeedback(userId, req));
    }

    @GetMapping("/my/page")
    @ApiOperation("我的反馈列表")
    public BaseResponse<PageResponse<FeedbackVO>> myPage(FeedbackPageReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(feedbackService.pageMyFeedbacks(userId, req));
    }
}

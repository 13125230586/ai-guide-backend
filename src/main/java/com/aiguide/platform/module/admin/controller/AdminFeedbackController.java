package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.feedback.model.req.FeedbackPageReq;
import com.aiguide.platform.module.feedback.model.req.FeedbackReplyReq;
import com.aiguide.platform.module.feedback.model.vo.FeedbackVO;
import com.aiguide.platform.module.feedback.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/feedback")
@RequireAdmin
@Api(tags = "管理端-反馈管理")
public class AdminFeedbackController {

    @Resource
    private FeedbackService feedbackService;

    @GetMapping("/page")
    @ApiOperation("反馈分页")
    public BaseResponse<PageResponse<FeedbackVO>> page(FeedbackPageReq req) {
        return ResultUtils.success(feedbackService.pageAllFeedbacks(req));
    }

    @PostMapping("/reply")
    @ApiOperation("回复反馈")
    public BaseResponse<Boolean> reply(@Valid @RequestBody FeedbackReplyReq req) {
        feedbackService.replyFeedback(req);
        return ResultUtils.success(true);
    }
}

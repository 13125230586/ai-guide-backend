package com.aiguide.platform.module.ai.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.auth.annotation.RequireLogin;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.ai.model.req.*;
import com.aiguide.platform.module.ai.model.vo.AiChatRecordVO;
import com.aiguide.platform.module.ai.model.vo.AiGuideVO;
import com.aiguide.platform.module.ai.service.AiGuideService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/ai/guide")
@Api(tags = "AI智能导游")
public class AiGuideController {

    @Resource
    private AiGuideService aiGuideService;

    @PostMapping("/explain-scenic")
    @ApiOperation("景点讲解")
    public BaseResponse<AiGuideVO> explainScenic(@Valid @RequestBody AiGuideReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.explainScenic(userId, req));
    }

    @PostMapping("/compare-scenic")
    @ApiOperation("景点对比")
    public BaseResponse<AiGuideVO> compareScenic(@Valid @RequestBody AiCompareReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.compareScenic(userId, req));
    }

    @PostMapping("/recommend-route")
    @ApiOperation("路线推荐")
    public BaseResponse<AiGuideVO> recommendRoute(@Valid @RequestBody AiRecommendReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.recommendRoute(userId, req));
    }

    @PostMapping("/common-question")
    @ApiOperation("常见问题问答")
    public BaseResponse<AiGuideVO> commonQuestion(@Valid @RequestBody AiGuideReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.commonQuestion(userId, req));
    }

    @PostMapping("/recognize-scenic")
    @ApiOperation("上传图片识别景点")
    public BaseResponse<AiGuideVO> recognizeScenic(@Valid @RequestBody AiRecognizeReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.recognizeScenic(userId, req));
    }

    @PostMapping("/translate-answer")
    @ApiOperation("多语种翻译")
    public BaseResponse<AiGuideVO> translateAnswer(@Valid @RequestBody AiGuideReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.translateAnswer(userId, req));
    }

    @PostMapping("/record/page")
    @RequireLogin
    @ApiOperation("AI问答记录分页")
    public BaseResponse<PageResponse<AiChatRecordVO>> pageRecord(@Valid @RequestBody AiRecordPageReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(aiGuideService.pageChatRecords(userId, req));
    }
}

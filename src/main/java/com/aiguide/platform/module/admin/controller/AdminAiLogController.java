package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.ai.model.req.AiLogPageReq;
import com.aiguide.platform.module.ai.model.vo.AiGuideLogVO;
import com.aiguide.platform.module.ai.service.AiGuideService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/ai")
@RequireAdmin
@Api(tags = "管理端-AI日志")
public class AdminAiLogController {

    @Resource
    private AiGuideService aiGuideService;

    @GetMapping("/log/page")
    @ApiOperation("AI调用日志分页")
    public BaseResponse<PageResponse<AiGuideLogVO>> page(AiLogPageReq req) {
        return ResultUtils.success(aiGuideService.pageAiLogs(req));
    }
}

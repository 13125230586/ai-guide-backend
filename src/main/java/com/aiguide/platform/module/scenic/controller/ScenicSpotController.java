package com.aiguide.platform.module.scenic.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.LanguageUtil;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotPageReq;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/scenic")
@Api(tags = "景点管理")
public class ScenicSpotController {

    @Resource
    private ScenicSpotService scenicSpotService;

    @GetMapping("/page")
    @ApiOperation("景点分页列表")
    public BaseResponse<PageResponse<ScenicSpotVO>> page(ScenicSpotPageReq req, HttpServletRequest request) {
        String languageCode = LanguageUtil.getLanguageCode(request);
        return ResultUtils.success(scenicSpotService.pageScenicSpots(req, languageCode));
    }

    @GetMapping("/detail")
    @ApiOperation("景点详情")
    public BaseResponse<ScenicSpotVO> detail(@RequestParam Long id, HttpServletRequest request) {
        String languageCode = LanguageUtil.getLanguageCode(request);
        return ResultUtils.success(scenicSpotService.getScenicSpotDetail(id, languageCode));
    }

    @GetMapping("/search")
    @ApiOperation("景点搜索")
    public BaseResponse<PageResponse<ScenicSpotVO>> search(ScenicSpotPageReq req, HttpServletRequest request) {
        String languageCode = LanguageUtil.getLanguageCode(request);
        return ResultUtils.success(scenicSpotService.pageScenicSpots(req, languageCode));
    }
}

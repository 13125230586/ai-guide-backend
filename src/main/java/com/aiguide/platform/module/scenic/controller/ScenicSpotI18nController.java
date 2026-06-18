package com.aiguide.platform.module.scenic.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotI18nVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotI18nService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/scenic/i18n")
@Api(tags = "景点多语种内容")
public class ScenicSpotI18nController {

    @Resource
    private ScenicSpotI18nService scenicSpotI18nService;

    @GetMapping("/detail")
    @ApiOperation("获取景点多语种内容列表")
    public BaseResponse<List<ScenicSpotI18nVO>> listBySpotId(@RequestParam Long scenicSpotId) {
        return ResultUtils.success(scenicSpotI18nService.listBySpotId(scenicSpotId));
    }

    @GetMapping("/lang")
    @ApiOperation("获取景点指定语言内容")
    public BaseResponse<ScenicSpotI18nVO> getByLang(@RequestParam Long scenicSpotId, @RequestParam String languageCode) {
        return ResultUtils.success(scenicSpotI18nService.getBySpotAndLang(scenicSpotId, languageCode));
    }
}

package com.aiguide.platform.module.scenic.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotMediaVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotMediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/scenic/media")
@Api(tags = "景点媒体资源")
public class ScenicSpotMediaController {

    @Resource
    private ScenicSpotMediaService scenicSpotMediaService;

    @GetMapping("/list")
    @ApiOperation("获取景点媒体资源列表")
    public BaseResponse<List<ScenicSpotMediaVO>> listBySpotId(@RequestParam Long scenicSpotId) {
        return ResultUtils.success(scenicSpotMediaService.listBySpotId(scenicSpotId));
    }
}

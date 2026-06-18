package com.aiguide.platform.module.route.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.route.model.req.RoutePageReq;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.aiguide.platform.module.route.model.vo.RouteVO;
import com.aiguide.platform.module.route.service.RouteI18nService;
import com.aiguide.platform.module.route.service.RouteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/route")
@Api(tags = "路线管理")
public class RouteController {

    @Resource
    private RouteService routeService;
    @Resource
    private RouteI18nService routeI18nService;

    @GetMapping("/page")
    @ApiOperation("路线分页列表")
    public BaseResponse<PageResponse<RouteVO>> page(RoutePageReq req) {
        return ResultUtils.success(routeService.pageRoutes(req));
    }

    @GetMapping("/detail")
    @ApiOperation("路线详情")
    public BaseResponse<RouteVO> detail(@RequestParam Long id) {
        return ResultUtils.success(routeService.getRouteDetail(id));
    }

    @GetMapping("/recommend")
    @ApiOperation("路线推荐")
    public BaseResponse<List<RouteVO>> recommend(
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String suitableCrowd,
            @RequestParam(defaultValue = "10") int limit) {
        return ResultUtils.success(routeService.recommendRoutes(theme, suitableCrowd, limit));
    }

    @GetMapping("/i18n/detail")
    @ApiOperation("路线多语种内容")
    public BaseResponse<List<RouteI18nVO>> i18nList(@RequestParam Long routeId) {
        return ResultUtils.success(routeI18nService.listByRouteId(routeId));
    }
}

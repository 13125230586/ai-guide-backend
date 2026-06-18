package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.IdReq;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.module.route.model.req.RouteI18nSaveReq;
import com.aiguide.platform.module.route.model.req.RoutePageReq;
import com.aiguide.platform.module.route.model.req.RouteSaveReq;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.aiguide.platform.module.route.model.vo.RouteVO;
import com.aiguide.platform.module.route.service.RouteI18nService;
import com.aiguide.platform.module.route.service.RouteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/route")
@RequireAdmin
@Api(tags = "管理端-路线管理")
public class AdminRouteController {

    @Resource
    private RouteService routeService;
    @Resource
    private RouteI18nService routeI18nService;

    @GetMapping("/page")
    @ApiOperation("路线分页")
    public BaseResponse<PageResponse<RouteVO>> page(RoutePageReq req) {
        return ResultUtils.success(routeService.pageRoutes(req));
    }

    @GetMapping("/detail")
    @ApiOperation("路线详情")
    public BaseResponse<RouteVO> detail(@RequestParam Long id) {
        return ResultUtils.success(routeService.getRouteDetail(id));
    }

    @PostMapping("/save")
    @ApiOperation("新增/更新路线")
    public BaseResponse<Long> save(@Valid @RequestBody RouteSaveReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        if (req.getId() != null) {
            routeService.updateRoute(req);
            return ResultUtils.success(req.getId());
        }
        return ResultUtils.success(routeService.saveRoute(req, userId));
    }

    @PostMapping("/delete")
    @ApiOperation("删除路线")
    public BaseResponse<Boolean> delete(@RequestBody IdReq req) {
        routeService.deleteRoute(req.getId());
        return ResultUtils.success(true);
    }

    @PostMapping("/status")
    @ApiOperation("上下架路线")
    public BaseResponse<Boolean> status(@RequestParam Long id, @RequestParam Integer status) {
        routeService.updateRouteStatus(id, status);
        return ResultUtils.success(true);
    }

    @GetMapping("/i18n/list")
    @ApiOperation("路线多语种内容")
    public BaseResponse<List<RouteI18nVO>> i18nList(@RequestParam Long routeId) {
        return ResultUtils.success(routeI18nService.listByRouteId(routeId));
    }

    @PostMapping("/i18n/save")
    @ApiOperation("新增/更新路线多语种")
    public BaseResponse<Long> i18nSave(@Valid @RequestBody RouteI18nSaveReq req) {
        if (req.getId() != null) {
            routeI18nService.updateI18n(req);
            return ResultUtils.success(req.getId());
        }
        return ResultUtils.success(routeI18nService.saveI18n(req));
    }
}

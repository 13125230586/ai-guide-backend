package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.module.admin.model.vo.AdminDashboardVO;
import com.aiguide.platform.module.admin.service.AdminDashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin")
@Api(tags = "管理后台")
public class AdminDashboardController {

    @Resource
    private AdminDashboardService adminDashboardService;

    @GetMapping("/statistics/dashboard")
    @RequireAdmin
    @ApiOperation("管理端仪表盘统计")
    public BaseResponse<AdminDashboardVO> dashboard() {
        return ResultUtils.success(adminDashboardService.getDashboard());
    }
}

package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.module.file.model.req.FileResourcePageReq;
import com.aiguide.platform.module.file.model.vo.FileResourceVO;
import com.aiguide.platform.module.file.service.FileResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/file")
@RequireAdmin
@Api(tags = "管理端-文件管理")
public class AdminFileController {

    @Resource
    private FileResourceService fileResourceService;

    @GetMapping("/page")
    @ApiOperation("文件资源分页")
    public BaseResponse<PageResponse<FileResourceVO>> page(FileResourcePageReq req) {
        return ResultUtils.success(fileResourceService.pageFileResources(req));
    }
}

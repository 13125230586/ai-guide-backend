package com.aiguide.platform.module.scenic.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.module.scenic.model.vo.CategoryVO;
import com.aiguide.platform.module.scenic.service.ScenicCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/scenic/category")
@Api(tags = "景点分类")
public class ScenicCategoryController {

    @Resource
    private ScenicCategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("获取所有启用的分类")
    public BaseResponse<List<CategoryVO>> listAll() {
        return ResultUtils.success(categoryService.listAllCategories());
    }
}

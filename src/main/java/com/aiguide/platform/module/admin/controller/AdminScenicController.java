package com.aiguide.platform.module.admin.controller;

import com.aiguide.platform.auth.annotation.RequireAdmin;
import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.IdReq;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.module.scenic.model.req.*;
import com.aiguide.platform.module.scenic.model.vo.*;
import com.aiguide.platform.module.scenic.service.*;
import com.aiguide.platform.module.file.manager.FileStorageManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/scenic")
@RequireAdmin
@Api(tags = "管理端-景点管理")
public class AdminScenicController {

    @Resource
    private ScenicCategoryService categoryService;
    @Resource
    private ScenicSpotService scenicSpotService;
    @Resource
    private ScenicSpotI18nService scenicSpotI18nService;
    @Resource
    private ScenicSpotMediaService scenicSpotMediaService;
    @Resource
    private FileStorageManager fileStorageManager;

    // ========== 分类管理 ==========

    @GetMapping("/category/page")
    @ApiOperation("分类分页")
    public BaseResponse<PageResponse<CategoryVO>> categoryPage(CategoryPageReq req) {
        return ResultUtils.success(categoryService.pageCategories(req));
    }

    @PostMapping("/category/save")
    @ApiOperation("新增/更新分类")
    public BaseResponse<Long> categorySave(@Valid @RequestBody CategorySaveReq req) {
        if (req.getId() != null) {
            categoryService.updateCategory(req);
            return ResultUtils.success(req.getId());
        }
        return ResultUtils.success(categoryService.saveCategory(req));
    }

    @PostMapping("/category/status")
    @ApiOperation("更新分类状态")
    public BaseResponse<Boolean> categoryStatus(@Valid @RequestBody CategoryStatusUpdateReq req) {
        categoryService.updateCategoryStatus(req);
        return ResultUtils.success(true);
    }

    @PostMapping("/category/delete")
    @ApiOperation("删除分类")
    public BaseResponse<Boolean> categoryDelete(@RequestBody IdReq req) {
        categoryService.deleteCategory(req.getId());
        return ResultUtils.success(true);
    }

    // ========== 景点管理 ==========

    @GetMapping("/page")
    @ApiOperation("景点分页")
    public BaseResponse<PageResponse<ScenicSpotVO>> page(ScenicSpotPageReq req) {
        return ResultUtils.success(scenicSpotService.pageScenicSpots(req));
    }

    @GetMapping("/detail")
    @ApiOperation("景点详情")
    public BaseResponse<ScenicSpotVO> detail(@RequestParam Long id) {
        return ResultUtils.success(scenicSpotService.getScenicSpotDetail(id));
    }

    @PostMapping("/save")
    @ApiOperation("新增/更新景点")
    public BaseResponse<Long> save(@Valid @RequestBody ScenicSpotSaveReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        if (req.getId() != null) {
            scenicSpotService.updateScenicSpot(req);
            return ResultUtils.success(req.getId());
        }
        return ResultUtils.success(scenicSpotService.saveScenicSpot(req, userId));
    }

    @PostMapping("/delete")
    @ApiOperation("删除景点")
    public BaseResponse<Boolean> delete(@RequestBody IdReq req) {
        scenicSpotService.deleteScenicSpot(req.getId());
        return ResultUtils.success(true);
    }

    @PostMapping("/status")
    @ApiOperation("上下架景点")
    public BaseResponse<Boolean> status(@RequestParam Long id, @RequestParam Integer status) {
        scenicSpotService.updateSpotStatus(id, status);
        return ResultUtils.success(true);
    }

    // ========== 多语种内容管理 ==========

    @GetMapping("/i18n/list")
    @ApiOperation("景点多语种内容列表")
    public BaseResponse<List<ScenicSpotI18nVO>> i18nList(@RequestParam Long scenicSpotId) {
        return ResultUtils.success(scenicSpotI18nService.listBySpotId(scenicSpotId));
    }

    @PostMapping("/i18n/save")
    @ApiOperation("新增/更新多语种内容")
    public BaseResponse<Long> i18nSave(@Valid @RequestBody ScenicSpotI18nSaveReq req) {
        if (req.getId() != null) {
            scenicSpotI18nService.updateI18n(req);
            return ResultUtils.success(req.getId());
        }
        return ResultUtils.success(scenicSpotI18nService.saveI18n(req));
    }

    // ========== 媒体资源管理 ==========

    @GetMapping("/media/list")
    @ApiOperation("景点媒体资源列表")
    public BaseResponse<List<ScenicSpotMediaVO>> mediaList(@RequestParam Long scenicSpotId) {
        return ResultUtils.success(scenicSpotMediaService.listBySpotId(scenicSpotId));
    }

    @PostMapping("/media/add")
    @ApiOperation("添加景点媒体资源")
    public BaseResponse<Long> mediaAdd(@Valid @RequestBody ScenicSpotMediaAddReq req) {
        return ResultUtils.success(scenicSpotMediaService.addMedia(
                req.getScenicSpotId(), req.getMediaType(), req.getMediaUrl(), req.getMediaName()));
    }

    @PostMapping("/media/delete")
    @ApiOperation("删除景点媒体资源")
    public BaseResponse<Boolean> mediaDelete(@RequestBody IdReq req) {
        scenicSpotMediaService.deleteMedia(req.getId());
        return ResultUtils.success(true);
    }

    @PostMapping("/media/upload")
    @ApiOperation("上传并添加景点轮播图")
    public BaseResponse<Long> mediaUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scenicSpotId") Long scenicSpotId,
            @RequestParam(value = "mediaName", required = false) String mediaName,
            HttpServletRequest request) {
        UserContextUtil.getLoginUserId(request);
        String fileUrl = fileStorageManager.uploadFile(file, "scenic");
        String finalMediaName = mediaName;
        if (finalMediaName == null || finalMediaName.trim().isEmpty()) {
            finalMediaName = file.getOriginalFilename();
        }
        return ResultUtils.success(scenicSpotMediaService.addMedia(scenicSpotId, "IMAGE", fileUrl, finalMediaName));
    }
}

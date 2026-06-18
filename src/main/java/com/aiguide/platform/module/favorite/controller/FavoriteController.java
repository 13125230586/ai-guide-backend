package com.aiguide.platform.module.favorite.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.module.favorite.model.req.FavoriteAddReq;
import com.aiguide.platform.module.favorite.model.req.FavoritePageReq;
import com.aiguide.platform.module.favorite.model.vo.FavoriteVO;
import com.aiguide.platform.module.favorite.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/favorite")
@Api(tags = "收藏管理")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    @PostMapping("/add")
    @ApiOperation("添加收藏")
    public BaseResponse<Boolean> add(@Valid @RequestBody FavoriteAddReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        favoriteService.addFavorite(userId, req.getBizType(), req.getBizId());
        return ResultUtils.success(true);
    }

    @PostMapping("/cancel")
    @ApiOperation("取消收藏")
    public BaseResponse<Boolean> cancel(@Valid @RequestBody FavoriteAddReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        favoriteService.cancelFavorite(userId, req.getBizType(), req.getBizId());
        return ResultUtils.success(true);
    }

    @GetMapping("/page")
    @ApiOperation("我的收藏列表")
    public BaseResponse<PageResponse<FavoriteVO>> page(FavoritePageReq req, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(favoriteService.pageMyFavorites(userId, req));
    }

    @GetMapping("/check")
    @ApiOperation("检查是否已收藏")
    public BaseResponse<Boolean> check(@RequestParam String bizType, @RequestParam Long bizId, HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        return ResultUtils.success(favoriteService.isFavorite(userId, bizType, bizId));
    }
}

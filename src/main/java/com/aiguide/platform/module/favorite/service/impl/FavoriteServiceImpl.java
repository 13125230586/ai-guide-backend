package com.aiguide.platform.module.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.constant.BusinessConstant;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.LanguageUtil;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.*;
import com.aiguide.platform.model.entity.*;
import com.aiguide.platform.module.favorite.model.req.FavoritePageReq;
import com.aiguide.platform.module.favorite.model.vo.FavoriteVO;
import com.aiguide.platform.module.favorite.service.FavoriteService;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.aiguide.platform.module.route.service.RouteI18nService;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotI18nVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotI18nService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
        implements FavoriteService {

    @Resource
    private ScenicSpotMapper scenicSpotMapper;
    @Resource
    private GuideRouteMapper guideRouteMapper;
    @Resource
    private ScenicSpotI18nService scenicSpotI18nService;
    @Resource
    private RouteI18nService routeI18nService;

    @Override
    @Transactional
    public void addFavorite(Long userId, String bizType, Long bizId) {
        // 先看是否存在未删除记录，存在则直接返回
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getBizType, bizType)
                .eq(UserFavorite::getBizId, bizId);
        UserFavorite existed = getOne(wrapper, false);
        if (existed != null && existed.getDeleted() == null || (existed != null && existed.getDeleted() == 0)) {
            return;
        }
        // 如果是逻辑删除过的记录，直接恢复，避免 unique key 冲突
        if (existed != null) {
            existed.setDeleted(0);
            ThrowUtils.throwIf(!updateById(existed), ErrorCode.OPERATION_ERROR, "收藏失败");
            return;
        }
        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setBizType(bizType);
        fav.setBizId(bizId);
        ThrowUtils.throwIf(!save(fav), ErrorCode.OPERATION_ERROR, "收藏失败");
    }

    @Override
    @Transactional
    public void cancelFavorite(Long userId, String bizType, Long bizId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getBizType, bizType)
                .eq(UserFavorite::getBizId, bizId);
        UserFavorite existed = getOne(wrapper, false);
        if (existed == null) {
            return;
        }
        // 这里用物理删除，释放联合唯一键，避免后续再次收藏撞库
        baseMapper.deleteById(existed.getId());
    }

    @Override
    public PageResponse<FavoriteVO> pageMyFavorites(Long userId, FavoritePageReq req) {
        return pageMyFavorites(userId, req, req.getLanguageCode());
    }

    @Override
    public PageResponse<FavoriteVO> pageMyFavorites(Long userId, FavoritePageReq req, String languageCode) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId);
        wrapper.eq(req.getBizType() != null, UserFavorite::getBizType, req.getBizType());
        wrapper.orderByDesc(UserFavorite::getCreateTime);

        Page<UserFavorite> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<FavoriteVO> voList = page.getRecords().stream()
                .map(favorite -> toVO(favorite, languageCode))
                .collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    public boolean isFavorite(Long userId, String bizType, Long bizId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getBizType, bizType)
                .eq(UserFavorite::getBizId, bizId);
        return getOne(wrapper, false) != null;
    }

    private FavoriteVO toVO(UserFavorite fav, String languageCode) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(fav.getId());
        vo.setBizType(fav.getBizType());
        vo.setBizId(fav.getBizId());
        vo.setCreateTime(fav.getCreateTime());
        // 查询收藏对象信息
        if (BusinessConstant.BIZ_TYPE_SCENIC.equals(fav.getBizType())) {
            ScenicSpot spot = scenicSpotMapper.selectById(fav.getBizId());
            if (spot != null) {
                vo.setBizName(spot.getSpotName());
                vo.setCoverUrl(spot.getCoverUrl());
                vo.setSummary(spot.getSummary());
                applyScenicI18n(vo, fav.getBizId(), languageCode);
            }
        } else if (BusinessConstant.BIZ_TYPE_ROUTE.equals(fav.getBizType())) {
            GuideRoute route = guideRouteMapper.selectById(fav.getBizId());
            if (route != null) {
                vo.setBizName(route.getRouteName());
                vo.setCoverUrl(route.getCoverUrl());
                vo.setSummary(route.getSummary());
                applyRouteI18n(vo, fav.getBizId(), languageCode);
            }
        }
        return vo;
    }

    private void applyScenicI18n(FavoriteVO vo, Long scenicSpotId, String languageCode) {
        if (LanguageUtil.isDefaultLanguage(languageCode)) {
            return;
        }
        ScenicSpotI18nVO i18nVO = scenicSpotI18nService.getBySpotAndLang(
                scenicSpotId, LanguageUtil.normalizeLanguageCode(languageCode));
        if (i18nVO == null) {
            return;
        }
        if (StringUtils.isNotBlank(i18nVO.getTitle())) {
            vo.setBizName(i18nVO.getTitle());
        }
        if (StringUtils.isNotBlank(i18nVO.getSummary())) {
            vo.setSummary(i18nVO.getSummary());
        }
    }

    private void applyRouteI18n(FavoriteVO vo, Long routeId, String languageCode) {
        if (LanguageUtil.isDefaultLanguage(languageCode)) {
            return;
        }
        RouteI18nVO i18nVO = routeI18nService.getByRouteAndLang(
                routeId, LanguageUtil.normalizeLanguageCode(languageCode));
        if (i18nVO == null) {
            return;
        }
        if (StringUtils.isNotBlank(i18nVO.getTitle())) {
            vo.setBizName(i18nVO.getTitle());
        }
        if (StringUtils.isNotBlank(i18nVO.getSummary())) {
            vo.setSummary(i18nVO.getSummary());
        }
    }
}

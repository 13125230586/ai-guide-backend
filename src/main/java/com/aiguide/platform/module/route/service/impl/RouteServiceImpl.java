package com.aiguide.platform.module.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.LanguageUtil;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.GuideRouteSpotRelMapper;
import com.aiguide.platform.mapper.ScenicCategoryMapper;
import com.aiguide.platform.mapper.ScenicSpotMapper;
import com.aiguide.platform.mapper.GuideRouteMapper;
import com.aiguide.platform.model.entity.GuideRoute;
import com.aiguide.platform.model.entity.GuideRouteSpotRel;
import com.aiguide.platform.model.entity.ScenicCategory;
import com.aiguide.platform.model.entity.ScenicSpot;
import com.aiguide.platform.module.route.model.req.RoutePageReq;
import com.aiguide.platform.module.route.model.req.RouteSaveReq;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.aiguide.platform.module.route.model.vo.RouteVO;
import com.aiguide.platform.module.route.service.RouteI18nService;
import com.aiguide.platform.module.route.service.RouteService;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotI18nVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotI18nService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl extends ServiceImpl<GuideRouteMapper, GuideRoute> implements RouteService {

    @Resource
    private GuideRouteSpotRelMapper routeSpotRelMapper;
    @Resource
    private ScenicSpotMapper scenicSpotMapper;
    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;
    @Resource
    private RouteI18nService routeI18nService;
    @Resource
    private ScenicSpotI18nService scenicSpotI18nService;

    @Override
    public PageResponse<RouteVO> pageRoutes(RoutePageReq req) {
        return pageRoutes(req, req.getLanguageCode());
    }

    @Override
    public PageResponse<RouteVO> pageRoutes(RoutePageReq req, String languageCode) {
        LambdaQueryWrapper<GuideRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(req.getRouteName()), GuideRoute::getRouteName, req.getRouteName());
        wrapper.eq(StringUtils.isNotBlank(req.getTheme()), GuideRoute::getTheme, req.getTheme());
        wrapper.eq(req.getRouteStatus() != null, GuideRoute::getRouteStatus, req.getRouteStatus());
        wrapper.like(StringUtils.isNotBlank(req.getSuitableCrowd()), GuideRoute::getSuitableCrowd, req.getSuitableCrowd());
        wrapper.orderByDesc(GuideRoute::getCreateTime);

        Page<GuideRoute> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<RouteVO> voList = page.getRecords().stream()
                .map(route -> toVO(route, languageCode))
                .collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    public RouteVO getRouteDetail(Long id) {
        return getRouteDetail(id, null);
    }

    @Override
    public RouteVO getRouteDetail(Long id, String languageCode) {
        GuideRoute route = getById(id);
        ThrowUtils.throwIf(route == null, ErrorCode.NOT_FOUND_ERROR, "路线不存在");
        return toVO(route, languageCode);
    }

    @Override
    @Transactional
    public Long saveRoute(RouteSaveReq req, Long userId) {
        GuideRoute route = new GuideRoute();
        copyFromReq(route, req);
        route.setCreatorId(userId);
        route.setRouteStatus(1);
        ThrowUtils.throwIf(!save(route), ErrorCode.OPERATION_ERROR);
        saveRouteSpots(route.getId(), req.getSpots());
        return route.getId();
    }

    @Override
    @Transactional
    public void updateRoute(RouteSaveReq req) {
        ThrowUtils.throwIf(req.getId() == null, ErrorCode.PARAMS_ERROR);
        GuideRoute route = getById(req.getId());
        ThrowUtils.throwIf(route == null, ErrorCode.NOT_FOUND_ERROR);
        copyFromReq(route, req);
        ThrowUtils.throwIf(!updateById(route), ErrorCode.OPERATION_ERROR);
        // 删除旧关联，重建
        LambdaQueryWrapper<GuideRouteSpotRel> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(GuideRouteSpotRel::getRouteId, req.getId());
        routeSpotRelMapper.delete(delWrapper);
        saveRouteSpots(route.getId(), req.getSpots());
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        LambdaQueryWrapper<GuideRouteSpotRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideRouteSpotRel::getRouteId, id);
        routeSpotRelMapper.delete(wrapper);
        ThrowUtils.throwIf(!removeById(id), ErrorCode.OPERATION_ERROR);
    }

    @Override
    @Transactional
    public void updateRouteStatus(Long id, Integer status) {
        GuideRoute route = getById(id);
        ThrowUtils.throwIf(route == null, ErrorCode.NOT_FOUND_ERROR);
        route.setRouteStatus(status);
        ThrowUtils.throwIf(!updateById(route), ErrorCode.OPERATION_ERROR);
    }

    @Override
    public List<RouteVO> recommendRoutes(String theme, String suitableCrowd, int limit) {
        return recommendRoutes(theme, suitableCrowd, limit, null);
    }

    @Override
    public List<RouteVO> recommendRoutes(String theme, String suitableCrowd, int limit, String languageCode) {
        LambdaQueryWrapper<GuideRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideRoute::getRouteStatus, 1);
        wrapper.like(StringUtils.isNotBlank(theme), GuideRoute::getTheme, theme);
        wrapper.like(StringUtils.isNotBlank(suitableCrowd), GuideRoute::getSuitableCrowd, suitableCrowd);
        wrapper.orderByDesc(GuideRoute::getCreateTime).last("LIMIT " + limit);
        return list(wrapper).stream()
                .map(route -> toVO(route, languageCode))
                .collect(Collectors.toList());
    }

    private void saveRouteSpots(Long routeId, List<RouteSaveReq.RouteSpotItem> spots) {
        if (spots == null || spots.isEmpty()) return;
        for (int i = 0; i < spots.size(); i++) {
            RouteSaveReq.RouteSpotItem item = spots.get(i);
            GuideRouteSpotRel rel = new GuideRouteSpotRel();
            rel.setRouteId(routeId);
            rel.setScenicSpotId(item.getScenicSpotId());
            rel.setSortNo(item.getSortNo() != null ? item.getSortNo() : i + 1);
            rel.setStayDuration(item.getStayDuration());
            routeSpotRelMapper.insert(rel);
        }
    }

    private void copyFromReq(GuideRoute route, RouteSaveReq req) {
        route.setRouteName(req.getRouteName());
        route.setTheme(req.getTheme());
        route.setCoverUrl(req.getCoverUrl());
        route.setSummary(req.getSummary());
        route.setDescription(req.getDescription());
        route.setSuggestDuration(req.getSuggestDuration());
        route.setSuitableCrowd(req.getSuitableCrowd());
    }

    private RouteVO toVO(GuideRoute route, String languageCode) {
        RouteVO vo = new RouteVO();
        vo.setId(route.getId());
        vo.setRouteName(route.getRouteName());
        vo.setTheme(route.getTheme());
        vo.setCoverUrl(route.getCoverUrl());
        vo.setSummary(route.getSummary());
        vo.setDescription(route.getDescription());
        vo.setSuggestDuration(route.getSuggestDuration());
        vo.setSuitableCrowd(route.getSuitableCrowd());
        vo.setRouteStatus(route.getRouteStatus());
        vo.setCreateTime(route.getCreateTime());
        applyRouteI18n(vo, route.getId(), languageCode);
        // 查询关联景点
        LambdaQueryWrapper<GuideRouteSpotRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(GuideRouteSpotRel::getRouteId, route.getId());
        relWrapper.orderByAsc(GuideRouteSpotRel::getSortNo);
        List<GuideRouteSpotRel> rels = routeSpotRelMapper.selectList(relWrapper);
        List<RouteVO.RouteSpotVO> spotVOs = new ArrayList<>();
        for (GuideRouteSpotRel rel : rels) {
            ScenicSpot spot = scenicSpotMapper.selectById(rel.getScenicSpotId());
            if (spot != null) {
                RouteVO.RouteSpotVO svo = new RouteVO.RouteSpotVO();
                svo.setId(spot.getId());
                svo.setCategoryId(spot.getCategoryId());
                ScenicCategory category = scenicCategoryMapper.selectById(spot.getCategoryId());
                if (category != null) {
                    svo.setCategoryName(category.getCategoryName());
                }
                svo.setSpotName(spot.getSpotName());
                svo.setCity(spot.getCity());
                svo.setAddress(spot.getAddress());
                svo.setLongitude(spot.getLongitude());
                svo.setLatitude(spot.getLatitude());
                svo.setCoverUrl(spot.getCoverUrl());
                svo.setSummary(spot.getSummary());
                svo.setDescription(spot.getDescription());
                svo.setOpenTime(spot.getOpenTime());
                svo.setSuggestDuration(spot.getSuggestDuration());
                svo.setTips(spot.getTips());
                svo.setHotScore(spot.getHotScore());
                svo.setSpotStatus(spot.getSpotStatus());
                svo.setViewCount(spot.getViewCount());
                svo.setCreateTime(spot.getCreateTime());
                svo.setSortNo(rel.getSortNo());
                svo.setStayDuration(rel.getStayDuration());
                applySpotI18n(svo, spot.getId(), languageCode);
                spotVOs.add(svo);
            }
        }
        vo.setScenicSpots(spotVOs);
        return vo;
    }

    private void applyRouteI18n(RouteVO vo, Long routeId, String languageCode) {
        if (LanguageUtil.isDefaultLanguage(languageCode)) {
            return;
        }
        RouteI18nVO i18nVO = routeI18nService.getByRouteAndLang(
                routeId, LanguageUtil.normalizeLanguageCode(languageCode));
        if (i18nVO == null) {
            return;
        }
        if (StringUtils.isNotBlank(i18nVO.getTitle())) {
            vo.setRouteName(i18nVO.getTitle());
        }
        if (StringUtils.isNotBlank(i18nVO.getSummary())) {
            vo.setSummary(i18nVO.getSummary());
        }
        if (StringUtils.isNotBlank(i18nVO.getDescription())) {
            vo.setDescription(i18nVO.getDescription());
        }
    }

    private void applySpotI18n(RouteVO.RouteSpotVO vo, Long scenicSpotId, String languageCode) {
        if (LanguageUtil.isDefaultLanguage(languageCode)) {
            return;
        }
        ScenicSpotI18nVO i18nVO = scenicSpotI18nService.getBySpotAndLang(
                scenicSpotId, LanguageUtil.normalizeLanguageCode(languageCode));
        if (i18nVO == null) {
            return;
        }
        if (StringUtils.isNotBlank(i18nVO.getTitle())) {
            vo.setSpotName(i18nVO.getTitle());
        }
        if (StringUtils.isNotBlank(i18nVO.getSummary())) {
            vo.setSummary(i18nVO.getSummary());
        }
        if (StringUtils.isNotBlank(i18nVO.getDescription())) {
            vo.setDescription(i18nVO.getDescription());
        }
        if (StringUtils.isNotBlank(i18nVO.getTips())) {
            vo.setTips(i18nVO.getTips());
        }
    }
}


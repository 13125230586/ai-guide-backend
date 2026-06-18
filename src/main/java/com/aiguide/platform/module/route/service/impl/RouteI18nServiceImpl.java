package com.aiguide.platform.module.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.GuideRouteI18nMapper;
import com.aiguide.platform.model.entity.GuideRouteI18n;
import com.aiguide.platform.module.route.model.req.RouteI18nSaveReq;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.aiguide.platform.module.route.service.RouteI18nService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteI18nServiceImpl extends ServiceImpl<GuideRouteI18nMapper, GuideRouteI18n>
        implements RouteI18nService {

    @Override
    public List<RouteI18nVO> listByRouteId(Long routeId) {
        LambdaQueryWrapper<GuideRouteI18n> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideRouteI18n::getRouteId, routeId);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public RouteI18nVO getByRouteAndLang(Long routeId, String languageCode) {
        LambdaQueryWrapper<GuideRouteI18n> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GuideRouteI18n::getRouteId, routeId);
        wrapper.eq(GuideRouteI18n::getLanguageCode, languageCode);
        GuideRouteI18n i18n = getOne(wrapper);
        return i18n != null ? toVO(i18n) : null;
    }

    @Override
    @Transactional
    public Long saveI18n(RouteI18nSaveReq req) {
        GuideRouteI18n i18n = new GuideRouteI18n();
        copyFromReq(i18n, req);
        ThrowUtils.throwIf(!save(i18n), ErrorCode.OPERATION_ERROR);
        return i18n.getId();
    }

    @Override
    @Transactional
    public void updateI18n(RouteI18nSaveReq req) {
        ThrowUtils.throwIf(req.getId() == null, ErrorCode.PARAMS_ERROR);
        GuideRouteI18n i18n = getById(req.getId());
        ThrowUtils.throwIf(i18n == null, ErrorCode.NOT_FOUND_ERROR);
        copyFromReq(i18n, req);
        ThrowUtils.throwIf(!updateById(i18n), ErrorCode.OPERATION_ERROR);
    }

    private void copyFromReq(GuideRouteI18n i18n, RouteI18nSaveReq req) {
        i18n.setRouteId(req.getRouteId());
        i18n.setLanguageCode(req.getLanguageCode());
        i18n.setTitle(req.getTitle());
        i18n.setSummary(req.getSummary());
        i18n.setDescription(req.getDescription());
        i18n.setTravelTips(req.getTravelTips());
    }

    private RouteI18nVO toVO(GuideRouteI18n i18n) {
        RouteI18nVO vo = new RouteI18nVO();
        vo.setId(i18n.getId());
        vo.setRouteId(i18n.getRouteId());
        vo.setLanguageCode(i18n.getLanguageCode());
        vo.setTitle(i18n.getTitle());
        vo.setSummary(i18n.getSummary());
        vo.setDescription(i18n.getDescription());
        vo.setTravelTips(i18n.getTravelTips());
        return vo;
    }
}

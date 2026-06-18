package com.aiguide.platform.module.route.service;

import com.aiguide.platform.model.entity.GuideRouteI18n;
import com.aiguide.platform.module.route.model.req.RouteI18nSaveReq;
import com.aiguide.platform.module.route.model.vo.RouteI18nVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface RouteI18nService extends IService<GuideRouteI18n> {
    List<RouteI18nVO> listByRouteId(Long routeId);

    RouteI18nVO getByRouteAndLang(Long routeId, String languageCode);

    Long saveI18n(RouteI18nSaveReq req);

    void updateI18n(RouteI18nSaveReq req);
}

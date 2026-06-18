package com.aiguide.platform.module.route.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.GuideRoute;
import com.aiguide.platform.module.route.model.req.RoutePageReq;
import com.aiguide.platform.module.route.model.req.RouteSaveReq;
import com.aiguide.platform.module.route.model.vo.RouteVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface RouteService extends IService<GuideRoute> {
    PageResponse<RouteVO> pageRoutes(RoutePageReq req);

    RouteVO getRouteDetail(Long id);

    Long saveRoute(RouteSaveReq req, Long userId);

    void updateRoute(RouteSaveReq req);

    void deleteRoute(Long id);

    void updateRouteStatus(Long id, Integer status);

    List<RouteVO> recommendRoutes(String theme, String suitableCrowd, int limit);
}

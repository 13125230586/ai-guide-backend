package com.aiguide.platform.module.route.model.vo;

import lombok.Data;

@Data
public class RouteI18nVO {
    private Long id;
    private Long routeId;
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String travelTips;
}

package com.aiguide.platform.module.route.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RouteI18nSaveReq {
    private Long id;
    @NotNull
    private Long routeId;
    @NotBlank
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String travelTips;
}

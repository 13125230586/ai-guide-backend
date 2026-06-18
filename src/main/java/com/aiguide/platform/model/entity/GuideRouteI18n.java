package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("guide_route_i18n")
public class GuideRouteI18n extends BaseEntity {
    private Long routeId;
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String travelTips;
}

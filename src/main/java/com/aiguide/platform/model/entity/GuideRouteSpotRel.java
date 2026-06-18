package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("guide_route_spot_rel")
public class GuideRouteSpotRel extends BaseEntity {
    private Long routeId;
    private Long scenicSpotId;
    private Integer sortNo;
    private String stayDuration;
}

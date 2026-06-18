package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("guide_route")
public class GuideRoute extends BaseEntity {
    private String routeName;
    private String theme;
    private String coverUrl;
    private String summary;
    private String description;
    private String suggestDuration;
    private String suitableCrowd;
    private Integer routeStatus;
    private Long creatorId;
}

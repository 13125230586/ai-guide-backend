package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_guide_log")
public class AiGuideLog extends BaseEntity {
    private Long userId;
    private String modelName;
    private String bizType;
    private String languageCode;
    private Long scenicSpotId;
    private Long routeId;
    private String requestSummary;
    private String responseSummary;
    private Integer successFlag;
    private Long costMillis;
    private String errorMessage;
}

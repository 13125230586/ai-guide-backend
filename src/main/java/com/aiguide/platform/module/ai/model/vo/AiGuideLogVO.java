package com.aiguide.platform.module.ai.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class AiGuideLogVO {
    private Long id;
    private Long userId;
    private String username;
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
    private Date createTime;
}

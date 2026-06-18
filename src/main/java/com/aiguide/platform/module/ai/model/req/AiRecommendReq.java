package com.aiguide.platform.module.ai.model.req;

import lombok.Data;

@Data
public class AiRecommendReq {
    private String timeBudget;
    private String interest;
    private String crowd;
    private String languageCode = "zh-CN";
}

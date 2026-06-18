package com.aiguide.platform.module.ai.model.vo;

import lombok.Data;

@Data
public class AiGuideVO {
    private String answer;
    private String languageCode;
    private Long costMillis;
}

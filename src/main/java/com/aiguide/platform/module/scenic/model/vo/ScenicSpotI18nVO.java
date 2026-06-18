package com.aiguide.platform.module.scenic.model.vo;

import lombok.Data;

@Data
public class ScenicSpotI18nVO {
    private Long id;
    private Long scenicSpotId;
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String tips;
}

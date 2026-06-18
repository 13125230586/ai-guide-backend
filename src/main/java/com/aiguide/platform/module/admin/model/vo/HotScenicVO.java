package com.aiguide.platform.module.admin.model.vo;

import lombok.Data;

@Data
public class HotScenicVO {
    private Long id;
    private String spotName;
    private String city;
    private Integer viewCount;
    private Integer hotScore;
}

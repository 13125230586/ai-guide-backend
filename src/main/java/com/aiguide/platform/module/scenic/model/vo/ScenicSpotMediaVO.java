package com.aiguide.platform.module.scenic.model.vo;

import lombok.Data;

@Data
public class ScenicSpotMediaVO {
    private Long id;
    private Long scenicSpotId;
    private String mediaType;
    private String mediaUrl;
    private String mediaName;
    private Integer sortNo;
}

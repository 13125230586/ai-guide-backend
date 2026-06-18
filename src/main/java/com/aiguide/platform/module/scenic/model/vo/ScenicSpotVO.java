package com.aiguide.platform.module.scenic.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ScenicSpotVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String spotName;
    private String city;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String coverUrl;
    private String summary;
    private String description;
    private String openTime;
    private String suggestDuration;
    private String tips;
    private Integer hotScore;
    private Integer spotStatus;
    private Integer viewCount;
    private Date createTime;
}

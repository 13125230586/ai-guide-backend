package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scenic_spot")
public class ScenicSpot extends BaseEntity {
    private Long categoryId;
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
    private Long creatorId;
    private Integer viewCount;
}

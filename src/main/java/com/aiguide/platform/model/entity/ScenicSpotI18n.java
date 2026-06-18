package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scenic_spot_i18n")
public class ScenicSpotI18n extends BaseEntity {
    private Long scenicSpotId;
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String tips;
}

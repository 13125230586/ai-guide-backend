package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scenic_spot_media")
public class ScenicSpotMedia extends BaseEntity {
    private Long scenicSpotId;
    private String mediaType;
    private String mediaUrl;
    private String mediaName;
    private Integer sortNo;
}

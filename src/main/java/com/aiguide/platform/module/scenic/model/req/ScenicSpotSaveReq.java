package com.aiguide.platform.module.scenic.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ScenicSpotSaveReq {
    private Long id;
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    @NotBlank(message = "景点名称不能为空")
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
}

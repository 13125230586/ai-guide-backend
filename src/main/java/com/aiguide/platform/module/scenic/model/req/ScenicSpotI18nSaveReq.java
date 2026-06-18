package com.aiguide.platform.module.scenic.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ScenicSpotI18nSaveReq {
    private Long id;
    @NotNull(message = "景点ID不能为空")
    private Long scenicSpotId;
    @NotBlank(message = "语言编码不能为空")
    private String languageCode;
    private String title;
    private String summary;
    private String description;
    private String tips;
}

package com.aiguide.platform.module.ai.model.req;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class AiCompareReq {
    @NotNull(message = "景点A不能为空")
    private Long scenicSpotIdA;
    @NotNull(message = "景点B不能为空")
    private Long scenicSpotIdB;
    private String languageCode = "zh-CN";
}

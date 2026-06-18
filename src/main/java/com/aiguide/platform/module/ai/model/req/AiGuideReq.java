package com.aiguide.platform.module.ai.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class AiGuideReq {
    @NotBlank(message = "问题内容不能为空")
    private String question;
    // 关联景点ID（可选）
    private Long scenicSpotId;
    // 关联路线ID（可选）
    private Long routeId;
    // 目标语言 默认中文
    private String languageCode = "zh-CN";
}

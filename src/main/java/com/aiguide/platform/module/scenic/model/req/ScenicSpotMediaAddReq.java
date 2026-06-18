package com.aiguide.platform.module.scenic.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ScenicSpotMediaAddReq {
    @NotNull(message = "景点ID不能为空")
    private Long scenicSpotId;
    @NotBlank(message = "媒体类型不能为空")
    private String mediaType;
    @NotBlank(message = "媒体URL不能为空")
    private String mediaUrl;
    private String mediaName;
}

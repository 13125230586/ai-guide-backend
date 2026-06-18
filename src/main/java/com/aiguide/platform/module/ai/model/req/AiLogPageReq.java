package com.aiguide.platform.module.ai.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiLogPageReq extends PageRequest {
    private String bizType;
    private Integer successFlag;
    private String languageCode;
}

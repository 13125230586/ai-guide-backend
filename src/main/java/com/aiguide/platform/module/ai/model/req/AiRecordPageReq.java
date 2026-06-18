package com.aiguide.platform.module.ai.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiRecordPageReq extends PageRequest {

    private String questionType;

    private String keyword;
}

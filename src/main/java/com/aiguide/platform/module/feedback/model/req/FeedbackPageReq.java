package com.aiguide.platform.module.feedback.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackPageReq extends PageRequest {
    private String feedbackType;
    private Integer feedbackStatus;
}

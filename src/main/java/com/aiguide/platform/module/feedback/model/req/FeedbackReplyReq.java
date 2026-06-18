package com.aiguide.platform.module.feedback.model.req;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
public class FeedbackReplyReq {
    @NotNull
    private Long id;
    @NotBlank
    private String replyContent;
    private Integer feedbackStatus;
}

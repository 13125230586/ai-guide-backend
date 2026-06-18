package com.aiguide.platform.module.feedback.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class FeedbackCreateReq {
    @NotBlank(message = "反馈类型不能为空")
    private String feedbackType;
    @NotBlank(message = "反馈内容不能为空")
    private String content;
    private String contactInfo;
}

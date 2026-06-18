package com.aiguide.platform.module.feedback.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class FeedbackVO {
    private Long id;
    private Long userId;
    private String username;
    private String feedbackType;
    private String content;
    private String contactInfo;
    private Integer feedbackStatus;
    private String replyContent;
    private Date replyTime;
    private Date createTime;
}

package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_feedback")
public class UserFeedback extends BaseEntity {
    private Long userId;
    private String feedbackType;
    private String content;
    private String contactInfo;
    private Integer feedbackStatus;
    private String replyContent;
    private Date replyTime;
}

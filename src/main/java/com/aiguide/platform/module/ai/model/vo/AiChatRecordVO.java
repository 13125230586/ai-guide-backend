package com.aiguide.platform.module.ai.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AiChatRecordVO {
    private Long id;
    private Long userId;
    private String username;
    private Long scenicSpotId;
    private Long routeId;
    private String questionType;
    private String questionContent;
    private String promptText;
    private String answerContent;
    private String modelName;
    private String languageCode;
    private Date createTime;
}

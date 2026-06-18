package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_chat_record")
public class AiChatRecord extends BaseEntity {

    private Long userId;

    private Long scenicSpotId;

    private Long routeId;

    private String questionType;

    private String questionContent;

    private String promptText;

    private String answerContent;

    private String modelName;

    private String languageCode;
}

package com.aiguide.platform.module.ai.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AiRecognizeReq {

    @NotBlank(message = "附件地址不能为空")
    private String fileUrl;

    private String fileName;

    private String fileType;

    private String question;

    private String languageCode = "zh-CN";
}

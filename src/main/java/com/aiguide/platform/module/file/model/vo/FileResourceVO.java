package com.aiguide.platform.module.file.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class FileResourceVO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String bizType;
    private Long bizId;
    private Date createTime;
}

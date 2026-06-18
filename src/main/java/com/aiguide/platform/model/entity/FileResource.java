package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_resource")
public class FileResource extends BaseEntity {
    private String bizType;
    private Long bizId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String storageMode;
    private Long uploaderId;
}

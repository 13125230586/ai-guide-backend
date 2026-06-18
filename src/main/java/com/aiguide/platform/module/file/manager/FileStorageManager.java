package com.aiguide.platform.module.file.manager;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageManager {
    /**
     * 上传文件到 OSS，返回可访问 URL
     */
    String uploadFile(MultipartFile file, String bizType);
}

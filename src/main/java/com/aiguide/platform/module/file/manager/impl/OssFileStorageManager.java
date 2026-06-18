package com.aiguide.platform.module.file.manager.impl;

import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.config.StorageProperties;
import com.aiguide.platform.module.file.manager.FileStorageManager;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OssFileStorageManager implements FileStorageManager {

    @Resource
    private StorageProperties storageProperties;

    // 允许上传的文件类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "mp3", "wav", "aac", "ogg",
            "mp4", "avi", "mov",
            "pdf", "doc", "docx", "xls", "xlsx"
    );

    // 最大文件大小 20MB
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @Override
    public String uploadFile(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过20MB");
        }

        // 获取文件后缀
        String originalName = file.getOriginalFilename();
        String fileType = "";
        if (originalName != null && originalName.contains(".")) {
            fileType = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!ALLOWED_TYPES.contains(fileType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件类型: " + fileType);
        }

        // 生成 OSS 文件路径
        String dir = bizType != null ? bizType : "common";
        String fileName = dir + "/" + UUID.randomUUID().toString().replace("-", "") + "." + fileType;

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    storageProperties.getEndpoint(),
                    storageProperties.getAccessKeyId(),
                    storageProperties.getAccessKeySecret()
            );
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(storageProperties.getBucketName(), fileName, inputStream);
            return storageProperties.getHost() + "/" + fileName;
        } catch (Exception e) {
            log.error("OSS上传失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

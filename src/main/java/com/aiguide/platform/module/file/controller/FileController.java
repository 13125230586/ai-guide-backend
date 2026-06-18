package com.aiguide.platform.module.file.controller;

import com.aiguide.platform.common.BaseResponse;
import com.aiguide.platform.common.ResultUtils;
import com.aiguide.platform.common.util.UserContextUtil;
import com.aiguide.platform.module.file.manager.FileStorageManager;
import com.aiguide.platform.module.file.model.vo.FileResourceVO;
import com.aiguide.platform.mapper.FileResourceMapper;
import com.aiguide.platform.model.entity.FileResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
@Api(tags = "文件上传")
public class FileController {

    @Resource
    private FileStorageManager fileStorageManager;
    @Resource
    private FileResourceMapper fileResourceMapper;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public BaseResponse<FileResourceVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", required = false) String bizType,
            HttpServletRequest request) {
        Long userId = UserContextUtil.getLoginUserId(request);
        String fileUrl = fileStorageManager.uploadFile(file, bizType);

        // 保存文件资源记录
        FileResource resource = new FileResource();
        resource.setFileName(file.getOriginalFilename());
        resource.setFileUrl(fileUrl);
        resource.setFileType(getFileType(file.getOriginalFilename()));
        resource.setFileSize(file.getSize());
        resource.setStorageMode("oss");
        resource.setUploaderId(userId);
        resource.setBizType(bizType);
        fileResourceMapper.insert(resource);

        FileResourceVO vo = new FileResourceVO();
        vo.setId(resource.getId());
        vo.setFileName(resource.getFileName());
        vo.setFileUrl(fileUrl);
        vo.setFileType(resource.getFileType());
        vo.setFileSize(resource.getFileSize());
        return ResultUtils.success(vo);
    }

    private String getFileType(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
}

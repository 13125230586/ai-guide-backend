package com.aiguide.platform.module.file.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.FileResource;
import com.aiguide.platform.module.file.model.req.FileResourcePageReq;
import com.aiguide.platform.module.file.model.vo.FileResourceVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FileResourceService extends IService<FileResource> {
    PageResponse<FileResourceVO> pageFileResources(FileResourcePageReq req);
}

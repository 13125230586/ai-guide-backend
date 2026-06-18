package com.aiguide.platform.module.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.mapper.FileResourceMapper;
import com.aiguide.platform.model.entity.FileResource;
import com.aiguide.platform.module.file.model.req.FileResourcePageReq;
import com.aiguide.platform.module.file.model.vo.FileResourceVO;
import com.aiguide.platform.module.file.service.FileResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileResourceServiceImpl extends ServiceImpl<FileResourceMapper, FileResource>
        implements FileResourceService {

    @Override
    public PageResponse<FileResourceVO> pageFileResources(FileResourcePageReq req) {
        LambdaQueryWrapper<FileResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(req.getBizType()), FileResource::getBizType, req.getBizType());
        wrapper.eq(StringUtils.isNotBlank(req.getFileType()), FileResource::getFileType, req.getFileType());
        wrapper.like(StringUtils.isNotBlank(req.getFileName()), FileResource::getFileName, req.getFileName());
        wrapper.orderByDesc(FileResource::getCreateTime);

        Page<FileResource> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<FileResourceVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    private FileResourceVO toVO(FileResource resource) {
        FileResourceVO vo = new FileResourceVO();
        vo.setId(resource.getId());
        vo.setFileName(resource.getFileName());
        vo.setFileUrl(resource.getFileUrl());
        vo.setFileType(resource.getFileType());
        vo.setFileSize(resource.getFileSize());
        vo.setBizType(resource.getBizType());
        vo.setBizId(resource.getBizId());
        vo.setCreateTime(resource.getCreateTime());
        return vo;
    }
}

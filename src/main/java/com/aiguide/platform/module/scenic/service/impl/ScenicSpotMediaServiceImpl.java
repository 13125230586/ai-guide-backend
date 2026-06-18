package com.aiguide.platform.module.scenic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.ScenicSpotMediaMapper;
import com.aiguide.platform.model.entity.ScenicSpotMedia;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotMediaVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotMediaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenicSpotMediaServiceImpl extends ServiceImpl<ScenicSpotMediaMapper, ScenicSpotMedia>
        implements ScenicSpotMediaService {

    @Override
    public List<ScenicSpotMediaVO> listBySpotId(Long scenicSpotId) {
        LambdaQueryWrapper<ScenicSpotMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpotMedia::getScenicSpotId, scenicSpotId);
        wrapper.orderByAsc(ScenicSpotMedia::getSortNo);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long addMedia(Long scenicSpotId, String mediaType, String mediaUrl, String mediaName) {
        ScenicSpotMedia media = new ScenicSpotMedia();
        media.setScenicSpotId(scenicSpotId);
        media.setMediaType(mediaType);
        media.setMediaUrl(mediaUrl);
        media.setMediaName(mediaName);
        // 自动计算排序号
        LambdaQueryWrapper<ScenicSpotMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpotMedia::getScenicSpotId, scenicSpotId);
        long count = count(wrapper);
        media.setSortNo((int) count + 1);
        ThrowUtils.throwIf(!save(media), ErrorCode.OPERATION_ERROR, "添加媒体失败");
        return media.getId();
    }

    @Override
    @Transactional
    public void deleteMedia(Long id) {
        ThrowUtils.throwIf(!removeById(id), ErrorCode.OPERATION_ERROR, "删除媒体失败");
    }

    private ScenicSpotMediaVO toVO(ScenicSpotMedia media) {
        ScenicSpotMediaVO vo = new ScenicSpotMediaVO();
        vo.setId(media.getId());
        vo.setScenicSpotId(media.getScenicSpotId());
        vo.setMediaType(media.getMediaType());
        vo.setMediaUrl(media.getMediaUrl());
        vo.setMediaName(media.getMediaName());
        vo.setSortNo(media.getSortNo());
        return vo;
    }
}

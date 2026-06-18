package com.aiguide.platform.module.scenic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.ScenicCategoryMapper;
import com.aiguide.platform.mapper.ScenicSpotMapper;
import com.aiguide.platform.model.entity.ScenicCategory;
import com.aiguide.platform.model.entity.ScenicSpot;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotPageReq;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotSaveReq;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenicSpotServiceImpl extends ServiceImpl<ScenicSpotMapper, ScenicSpot>
        implements ScenicSpotService {

    @Resource
    private ScenicCategoryMapper categoryMapper;

    @Override
    public PageResponse<ScenicSpotVO> pageScenicSpots(ScenicSpotPageReq req) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(req.getCategoryId() != null, ScenicSpot::getCategoryId, req.getCategoryId());
        wrapper.like(StringUtils.isNotBlank(req.getCity()), ScenicSpot::getCity, req.getCity());
        wrapper.like(StringUtils.isNotBlank(req.getSpotName()), ScenicSpot::getSpotName, req.getSpotName());
        wrapper.eq(req.getSpotStatus() != null, ScenicSpot::getSpotStatus, req.getSpotStatus());
        if (StringUtils.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(ScenicSpot::getSpotName, req.getKeyword())
                    .or().like(ScenicSpot::getSummary, req.getKeyword())
                    .or().like(ScenicSpot::getCity, req.getKeyword()));
        }
        wrapper.orderByDesc(ScenicSpot::getHotScore).orderByDesc(ScenicSpot::getCreateTime);

        Page<ScenicSpot> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<ScenicSpotVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    public ScenicSpotVO getScenicSpotDetail(Long id) {
        ScenicSpot spot = getById(id);
        ThrowUtils.throwIf(spot == null, ErrorCode.NOT_FOUND_ERROR, "景点不存在");
        // 增加浏览量
        ScenicSpot update = new ScenicSpot();
        update.setId(id);
        update.setViewCount(spot.getViewCount() + 1);
        updateById(update);
        return toVO(spot);
    }

    @Override
    @Transactional
    public Long saveScenicSpot(ScenicSpotSaveReq req, Long userId) {
        ScenicSpot spot = new ScenicSpot();
        copyFromReq(spot, req);
        spot.setCreatorId(userId);
        spot.setSpotStatus(1);
        spot.setHotScore(0);
        spot.setViewCount(0);
        ThrowUtils.throwIf(!save(spot), ErrorCode.OPERATION_ERROR, "新增景点失败");
        return spot.getId();
    }

    @Override
    @Transactional
    public void updateScenicSpot(ScenicSpotSaveReq req) {
        ThrowUtils.throwIf(req.getId() == null, ErrorCode.PARAMS_ERROR, "ID不能为空");
        ScenicSpot spot = getById(req.getId());
        ThrowUtils.throwIf(spot == null, ErrorCode.NOT_FOUND_ERROR);
        copyFromReq(spot, req);
        ThrowUtils.throwIf(!updateById(spot), ErrorCode.OPERATION_ERROR);
    }

    @Override
    @Transactional
    public void deleteScenicSpot(Long id) {
        ThrowUtils.throwIf(!removeById(id), ErrorCode.OPERATION_ERROR);
    }

    @Override
    @Transactional
    public void updateSpotStatus(Long id, Integer status) {
        ScenicSpot spot = getById(id);
        ThrowUtils.throwIf(spot == null, ErrorCode.NOT_FOUND_ERROR);
        spot.setSpotStatus(status);
        ThrowUtils.throwIf(!updateById(spot), ErrorCode.OPERATION_ERROR);
    }

    private void copyFromReq(ScenicSpot spot, ScenicSpotSaveReq req) {
        spot.setCategoryId(req.getCategoryId());
        spot.setSpotName(req.getSpotName());
        spot.setCity(req.getCity());
        spot.setAddress(req.getAddress());
        spot.setLongitude(req.getLongitude());
        spot.setLatitude(req.getLatitude());
        spot.setCoverUrl(req.getCoverUrl());
        spot.setSummary(req.getSummary());
        spot.setDescription(req.getDescription());
        spot.setOpenTime(req.getOpenTime());
        spot.setSuggestDuration(req.getSuggestDuration());
        spot.setTips(req.getTips());
    }

    private ScenicSpotVO toVO(ScenicSpot spot) {
        ScenicSpotVO vo = new ScenicSpotVO();
        vo.setId(spot.getId());
        vo.setCategoryId(spot.getCategoryId());
        vo.setSpotName(spot.getSpotName());
        vo.setCity(spot.getCity());
        vo.setAddress(spot.getAddress());
        vo.setLongitude(spot.getLongitude());
        vo.setLatitude(spot.getLatitude());
        vo.setCoverUrl(spot.getCoverUrl());
        vo.setSummary(spot.getSummary());
        vo.setDescription(spot.getDescription());
        vo.setOpenTime(spot.getOpenTime());
        vo.setSuggestDuration(spot.getSuggestDuration());
        vo.setTips(spot.getTips());
        vo.setHotScore(spot.getHotScore());
        vo.setSpotStatus(spot.getSpotStatus());
        vo.setViewCount(spot.getViewCount());
        vo.setCreateTime(spot.getCreateTime());
        // 查询分类名称
        ScenicCategory category = categoryMapper.selectById(spot.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        return vo;
    }
}

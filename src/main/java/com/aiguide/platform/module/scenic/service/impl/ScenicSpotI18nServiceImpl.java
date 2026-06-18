package com.aiguide.platform.module.scenic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.ScenicSpotI18nMapper;
import com.aiguide.platform.model.entity.ScenicSpotI18n;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotI18nSaveReq;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotI18nVO;
import com.aiguide.platform.module.scenic.service.ScenicSpotI18nService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenicSpotI18nServiceImpl extends ServiceImpl<ScenicSpotI18nMapper, ScenicSpotI18n>
        implements ScenicSpotI18nService {

    @Override
    public List<ScenicSpotI18nVO> listBySpotId(Long scenicSpotId) {
        LambdaQueryWrapper<ScenicSpotI18n> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpotI18n::getScenicSpotId, scenicSpotId);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public ScenicSpotI18nVO getBySpotAndLang(Long scenicSpotId, String languageCode) {
        LambdaQueryWrapper<ScenicSpotI18n> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicSpotI18n::getScenicSpotId, scenicSpotId);
        wrapper.eq(ScenicSpotI18n::getLanguageCode, languageCode);
        ScenicSpotI18n i18n = getOne(wrapper);
        return i18n != null ? toVO(i18n) : null;
    }

    @Override
    @Transactional
    public Long saveI18n(ScenicSpotI18nSaveReq req) {
        // 检查是否已存在
        ScenicSpotI18nVO existing = getBySpotAndLang(req.getScenicSpotId(), req.getLanguageCode());
        ThrowUtils.throwIf(existing != null, ErrorCode.PARAMS_ERROR, "该语言版本已存在");
        ScenicSpotI18n i18n = new ScenicSpotI18n();
        copyFromReq(i18n, req);
        ThrowUtils.throwIf(!save(i18n), ErrorCode.OPERATION_ERROR);
        return i18n.getId();
    }

    @Override
    @Transactional
    public void updateI18n(ScenicSpotI18nSaveReq req) {
        ThrowUtils.throwIf(req.getId() == null, ErrorCode.PARAMS_ERROR, "ID不能为空");
        ScenicSpotI18n i18n = getById(req.getId());
        ThrowUtils.throwIf(i18n == null, ErrorCode.NOT_FOUND_ERROR);
        copyFromReq(i18n, req);
        ThrowUtils.throwIf(!updateById(i18n), ErrorCode.OPERATION_ERROR);
    }

    private void copyFromReq(ScenicSpotI18n i18n, ScenicSpotI18nSaveReq req) {
        i18n.setScenicSpotId(req.getScenicSpotId());
        i18n.setLanguageCode(req.getLanguageCode());
        i18n.setTitle(req.getTitle());
        i18n.setSummary(req.getSummary());
        i18n.setDescription(req.getDescription());
        i18n.setTips(req.getTips());
    }

    private ScenicSpotI18nVO toVO(ScenicSpotI18n i18n) {
        ScenicSpotI18nVO vo = new ScenicSpotI18nVO();
        vo.setId(i18n.getId());
        vo.setScenicSpotId(i18n.getScenicSpotId());
        vo.setLanguageCode(i18n.getLanguageCode());
        vo.setTitle(i18n.getTitle());
        vo.setSummary(i18n.getSummary());
        vo.setDescription(i18n.getDescription());
        vo.setTips(i18n.getTips());
        return vo;
    }
}

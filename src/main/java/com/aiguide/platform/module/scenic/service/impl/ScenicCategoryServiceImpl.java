package com.aiguide.platform.module.scenic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguide.platform.common.exception.BusinessException;
import com.aiguide.platform.common.exception.ErrorCode;
import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.common.util.ThrowUtils;
import com.aiguide.platform.mapper.ScenicCategoryMapper;
import com.aiguide.platform.model.entity.ScenicCategory;
import com.aiguide.platform.module.scenic.model.req.CategoryPageReq;
import com.aiguide.platform.module.scenic.model.req.CategorySaveReq;
import com.aiguide.platform.module.scenic.model.req.CategoryStatusUpdateReq;
import com.aiguide.platform.module.scenic.model.vo.CategoryVO;
import com.aiguide.platform.module.scenic.service.ScenicCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenicCategoryServiceImpl extends ServiceImpl<ScenicCategoryMapper, ScenicCategory>
        implements ScenicCategoryService {

    @Override
    public List<CategoryVO> listAllCategories() {
        LambdaQueryWrapper<ScenicCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicCategory::getCategoryStatus, 1);
        wrapper.orderByAsc(ScenicCategory::getSortNo);
        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResponse<CategoryVO> pageCategories(CategoryPageReq req) {
        LambdaQueryWrapper<ScenicCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(req.getCategoryName()), ScenicCategory::getCategoryName, req.getCategoryName());
        wrapper.eq(req.getCategoryStatus() != null, ScenicCategory::getCategoryStatus, req.getCategoryStatus());
        wrapper.orderByAsc(ScenicCategory::getSortNo);
        Page<ScenicCategory> page = page(new Page<>(req.getCurrent(), req.getPageSize()), wrapper);
        List<CategoryVO> voList = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResponse.of(page, voList);
    }

    @Override
    @Transactional
    public Long saveCategory(CategorySaveReq req) {
        ScenicCategory category = new ScenicCategory();
        category.setCategoryName(req.getCategoryName());
        category.setCategoryDesc(req.getCategoryDesc());
        category.setSortNo(req.getSortNo() != null ? req.getSortNo() : 0);
        category.setCategoryStatus(1);
        ThrowUtils.throwIf(!save(category), ErrorCode.OPERATION_ERROR, "新增分类失败");
        return category.getId();
    }

    @Override
    @Transactional
    public void updateCategory(CategorySaveReq req) {
        ThrowUtils.throwIf(req.getId() == null, ErrorCode.PARAMS_ERROR, "ID不能为空");
        ScenicCategory category = getById(req.getId());
        ThrowUtils.throwIf(category == null, ErrorCode.NOT_FOUND_ERROR);
        category.setCategoryName(req.getCategoryName());
        category.setCategoryDesc(req.getCategoryDesc());
        if (req.getSortNo() != null) category.setSortNo(req.getSortNo());
        ThrowUtils.throwIf(!updateById(category), ErrorCode.OPERATION_ERROR);
    }

    @Override
    @Transactional
    public void updateCategoryStatus(CategoryStatusUpdateReq req) {
        ScenicCategory category = getById(req.getId());
        ThrowUtils.throwIf(category == null, ErrorCode.NOT_FOUND_ERROR);
        category.setCategoryStatus(req.getCategoryStatus());
        ThrowUtils.throwIf(!updateById(category), ErrorCode.OPERATION_ERROR);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ThrowUtils.throwIf(!removeById(id), ErrorCode.OPERATION_ERROR);
    }

    private CategoryVO toVO(ScenicCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setCategoryName(category.getCategoryName());
        vo.setCategoryDesc(category.getCategoryDesc());
        vo.setSortNo(category.getSortNo());
        vo.setCategoryStatus(category.getCategoryStatus());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }
}

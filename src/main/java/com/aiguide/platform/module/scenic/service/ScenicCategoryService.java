package com.aiguide.platform.module.scenic.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.ScenicCategory;
import com.aiguide.platform.module.scenic.model.req.CategoryPageReq;
import com.aiguide.platform.module.scenic.model.req.CategorySaveReq;
import com.aiguide.platform.module.scenic.model.req.CategoryStatusUpdateReq;
import com.aiguide.platform.module.scenic.model.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ScenicCategoryService extends IService<ScenicCategory> {
    List<CategoryVO> listAllCategories();

    PageResponse<CategoryVO> pageCategories(CategoryPageReq req);

    Long saveCategory(CategorySaveReq req);

    void updateCategory(CategorySaveReq req);

    void updateCategoryStatus(CategoryStatusUpdateReq req);

    void deleteCategory(Long id);
}

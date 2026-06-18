package com.aiguide.platform.module.scenic.service;

import com.aiguide.platform.model.entity.ScenicSpotI18n;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotI18nSaveReq;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotI18nVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ScenicSpotI18nService extends IService<ScenicSpotI18n> {
    List<ScenicSpotI18nVO> listBySpotId(Long scenicSpotId);

    ScenicSpotI18nVO getBySpotAndLang(Long scenicSpotId, String languageCode);

    Long saveI18n(ScenicSpotI18nSaveReq req);

    void updateI18n(ScenicSpotI18nSaveReq req);
}

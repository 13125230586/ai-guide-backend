package com.aiguide.platform.module.scenic.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.ScenicSpot;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotPageReq;
import com.aiguide.platform.module.scenic.model.req.ScenicSpotSaveReq;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ScenicSpotService extends IService<ScenicSpot> {
    PageResponse<ScenicSpotVO> pageScenicSpots(ScenicSpotPageReq req);

    ScenicSpotVO getScenicSpotDetail(Long id);

    Long saveScenicSpot(ScenicSpotSaveReq req, Long userId);

    void updateScenicSpot(ScenicSpotSaveReq req);

    void deleteScenicSpot(Long id);

    void updateSpotStatus(Long id, Integer status);
}

package com.aiguide.platform.module.scenic.service;

import com.aiguide.platform.model.entity.ScenicSpotMedia;
import com.aiguide.platform.module.scenic.model.vo.ScenicSpotMediaVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ScenicSpotMediaService extends IService<ScenicSpotMedia> {
    List<ScenicSpotMediaVO> listBySpotId(Long scenicSpotId);

    Long addMedia(Long scenicSpotId, String mediaType, String mediaUrl, String mediaName);

    void deleteMedia(Long id);
}

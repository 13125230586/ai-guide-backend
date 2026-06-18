package com.aiguide.platform.module.favorite.service;

import com.aiguide.platform.common.model.PageResponse;
import com.aiguide.platform.model.entity.UserFavorite;
import com.aiguide.platform.module.favorite.model.req.FavoritePageReq;
import com.aiguide.platform.module.favorite.model.vo.FavoriteVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FavoriteService extends IService<UserFavorite> {
    void addFavorite(Long userId, String bizType, Long bizId);

    void cancelFavorite(Long userId, String bizType, Long bizId);

    PageResponse<FavoriteVO> pageMyFavorites(Long userId, FavoritePageReq req);

    boolean isFavorite(Long userId, String bizType, Long bizId);
}

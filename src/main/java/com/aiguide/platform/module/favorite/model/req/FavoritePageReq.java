package com.aiguide.platform.module.favorite.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FavoritePageReq extends PageRequest {
    private String bizType;
    private String languageCode;
}

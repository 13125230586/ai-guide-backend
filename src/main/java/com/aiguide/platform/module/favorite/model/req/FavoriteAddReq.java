package com.aiguide.platform.module.favorite.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FavoriteAddReq {
    @NotBlank(message = "收藏类型不能为空")
    private String bizType;
    @NotNull(message = "收藏对象ID不能为空")
    private Long bizId;
}

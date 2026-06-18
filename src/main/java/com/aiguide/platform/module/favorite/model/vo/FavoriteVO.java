package com.aiguide.platform.module.favorite.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class FavoriteVO {
    private Long id;
    private String bizType;
    private Long bizId;
    private String bizName;
    private String coverUrl;
    private String summary;
    private Date createTime;
}

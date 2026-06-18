package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_favorite")
public class UserFavorite extends BaseEntity {
    private Long userId;
    private String bizType;
    private Long bizId;
}

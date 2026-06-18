package com.aiguide.platform.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scenic_category")
public class ScenicCategory extends BaseEntity {
    private String categoryName;
    private String categoryDesc;
    private Integer sortNo;
    private Integer categoryStatus;
}

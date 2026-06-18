package com.aiguide.platform.module.scenic.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class CategoryVO {
    private Long id;
    private String categoryName;
    private String categoryDesc;
    private Integer sortNo;
    private Integer categoryStatus;
    private Date createTime;
}

package com.aiguide.platform.module.scenic.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryPageReq extends PageRequest {
    private String categoryName;
    private Integer categoryStatus;
}

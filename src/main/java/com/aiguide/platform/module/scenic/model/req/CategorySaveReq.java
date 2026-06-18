package com.aiguide.platform.module.scenic.model.req;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CategorySaveReq {
    private Long id;
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    private String categoryDesc;
    private Integer sortNo;
}

package com.aiguide.platform.module.file.model.req;

import com.aiguide.platform.common.model.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileResourcePageReq extends PageRequest {
    private String bizType;
    private String fileType;
    private String fileName;
}

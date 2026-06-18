package com.aiguide.platform.common.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private long current;
    private long pageSize;
    private long total;
    private List<T> records;

    public static <T> PageResponse<T> of(Page<?> page, List<T> records) {
        PageResponse<T> resp = new PageResponse<>();
        resp.setCurrent(page.getCurrent());
        resp.setPageSize(page.getSize());
        resp.setTotal(page.getTotal());
        resp.setRecords(records);
        return resp;
    }
}

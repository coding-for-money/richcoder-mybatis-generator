package com.huobi.finance.model.req;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class PageReq<T> implements Serializable {
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private int pageNum = 1;
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private int pageSize = 50;
    private long total;
    private int pages;
    private List<T> items;

    public void setItems(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page)list;
            this.pages = page.getPages();
            this.items = list;
            this.total = page.getTotal();
        } else if (list != null) {
            this.pages = this.pageSize > 0 ? 1 : 0;
            this.items = list;
            this.total = (long)list.size();
        }
    }
}

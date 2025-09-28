package work.chncyl.base.global.pojo;

import lombok.Data;

/**
 * 实现功能描述:
 * 分页dto类型，分页参数继承此类
 */
public class PagedInputPojo {
    //    每页的数量
    private Integer size;
    //    页码
    private Integer pageIndex;


    public Integer getSize() {
        if (size == null || size <= 0) {
            size = 10;
        }
        return size;
    }

    public void setSize(Integer size) {
        if (size == null || size <= 0) {
            size = 10;
        }
        this.size = size;
    }

    public Integer getPageIndex() {
        if (pageIndex == null || pageIndex <= 0) {
            pageIndex = 1;
        }
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        if (pageIndex == null || pageIndex <= 0) {
            pageIndex = 1;
        }
        this.pageIndex = pageIndex;
    }
}

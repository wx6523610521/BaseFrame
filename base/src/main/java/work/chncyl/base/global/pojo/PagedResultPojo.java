package work.chncyl.base.global.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PagedResultPojo<T> {
    private long totalCount;
    private List<T> items;
}

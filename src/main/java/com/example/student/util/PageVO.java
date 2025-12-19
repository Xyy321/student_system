package com.example.student.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {

    private List<T> records;      // 数据列表
    private Long total;           // 总记录数
    private Integer pages;        // 总页数
    private Integer current;      // 当前页码
    private Integer size;         // 每页大小
    private Boolean hasNext;      // 是否有下一页
    private Boolean hasPrevious;  // 是否有上一页

    /**
     * 从Spring Data Page转换
     */
    public static <T> PageVO<T> fromPage(Page<T> page) {
        return PageVO.<T>builder()
                .records(page.getContent())
                .total(page.getTotalElements())
                .pages(page.getTotalPages())
                .current(page.getNumber() + 1)  // Spring Data页码从0开始
                .size(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * 从Spring Data Page转换（带数据转换）
     */
    public static <T, R> PageVO<R> fromPage(Page<T> page, List<R> records) {
        return PageVO.<R>builder()
                .records(records)
                .total(page.getTotalElements())
                .pages(page.getTotalPages())
                .current(page.getNumber() + 1)
                .size(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}

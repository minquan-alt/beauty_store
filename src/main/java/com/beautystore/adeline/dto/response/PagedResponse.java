package com.beautystore.adeline.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private Meta meta;

    @Data
    @AllArgsConstructor
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }
}

package com.hutech.buiduongtin.api.dto.response;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious,
        Integer nextPage,
        Integer previousPage,
        String sort) {
}

package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.util.List;

public record PageResponseDTO<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) implements Serializable {
}

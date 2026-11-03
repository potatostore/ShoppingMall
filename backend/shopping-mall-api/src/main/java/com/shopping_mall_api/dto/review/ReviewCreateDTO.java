package com.shopping_mall_api.dto.review;

// [뼈대] 구현 시: @NotNull orderItemId, @Min(1) @Max(5) rating, @Size(max = 1000) content
public record ReviewCreateDTO(
        Long orderItemId,
        Integer rating,
        String content
) {
}

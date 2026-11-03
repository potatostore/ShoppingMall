package com.shopping_mall_api.dto.review;

public record ReviewUpdateDTO(
        Integer rating,
        String content
) {
}

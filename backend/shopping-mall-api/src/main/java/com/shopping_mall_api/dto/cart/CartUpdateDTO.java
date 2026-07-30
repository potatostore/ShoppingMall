package com.shopping_mall_api.dto.cart;

import lombok.Getter;

@Getter
public class CartUpdateDTO {
    private Long productId;
    private Integer curProductPrice;
    private Integer quantity;
}
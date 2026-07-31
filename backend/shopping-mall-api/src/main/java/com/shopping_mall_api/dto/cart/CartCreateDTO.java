package com.shopping_mall_api.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateDTO {
    @NotNull(message = "userId must not be null")
    private Long userId;
}
package com.shopping_mall_api.dto.user;

import com.shopping_mall_api.dto.cart.CartResponseDTO;

public record UserCreateResponseDTO(
        UserResponseDTO userResponseDTO,
        CartResponseDTO cartResponseDTO
){}

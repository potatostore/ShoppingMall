package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.repository.cart.CartItemRepository;
import com.shopping_mall_api.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartResponseDTO createCart(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        Cart createCart = Cart.builder()
                .userId(userId)
                .
    }
}

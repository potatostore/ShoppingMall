package com.shopping_mall_api.entity;

import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {
    private Product product(Long productId, Long price){
        Product product = Product.builder()
                .name("상품" + productId)
                .price(price)
                .stock(100L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("detail")))
                .build();
        ReflectionTestUtils.setField(product, "productId", productId);
        return product;
    }

    @Test
    void addSameProductTwice_mergesQuantity(){
        Cart cart = Cart.builder().userId(1L).build();
        Product product = product(1L, 1000L);

        cart.addCartItemInCart(product, 1L);
        cart.addCartItemInCart(product, 2L);

        assertThat(cart.getCartItemList()).hasSize(1);
        assertThat(cart.getCartItemList().getFirst().getQuantity()).isEqualTo(3L);
        assertThat(cart.getTotalCartPrice()).isEqualTo(3000L);
    }

    @Test
    void patchAfterAddingSameProductTwice_doesNotFail(){
        Cart cart = Cart.builder().userId(1L).build();
        Product product = product(1L, 1000L);
        cart.addCartItemInCart(product, 1L);
        cart.addCartItemInCart(product, 1L);

        // 중복 CartItem이 있으면 Collectors.toMap에서 IllegalStateException(Duplicate key) → 500
        cart.patchCart(new CartUpdateDTO(List.of(new CartItemUpdateDTO(1L, 5L))));

        assertThat(cart.getCartItemList().getFirst().getQuantity()).isEqualTo(5L);
    }
}

package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemCreateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.cart.Cart;
import com.shopping_mall_api.entity.cart.CartItem;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.cart.CartItemRepository;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.cart.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks CartService cartService;

    @Test
    void createCart_successTest(){
        User existUser = User.builder()
                .email("qwer1234@google.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartResponseDTO result = cartService.createCart(1L);

        assertThat(result.getCartItemList()).isEqualTo(List.of());
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTotalCartPrice()).isEqualTo(0L);
    }

    @Test
    void addCartItemInCart_successTest(){
        Cart existCart = Cart.builder()
                        .userId(1L)
                        .build();

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                        .name("Test")
                        .price(20260828L)
                        .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                        .build();

        ReflectionTestUtils.setField(existProduct, "productId", 1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(existCart))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartResponseDTO result = cartService.addCartItemInCart(1L, new CartItemCreateDTO(1L, 1L));

        assertThat(result.getTotalCartPrice()).isEqualTo(20260828L);
        assertThat(result.getCartItemList().getFirst().getProductItemId()).isEqualTo(1L);
        assertThat(result.getCartItemList().getFirst().getQuantity()).isEqualTo(1L);
    }

    @Test
    void addCartItemInCart_cartNotFoundExceptionTest(){
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        CartItemCreateDTO cartItemCreateDTO = new CartItemCreateDTO(999L, 1L);

        assertThrows(NotFoundException.class, () -> cartService.addCartItemInCart(999L, cartItemCreateDTO));
    }

    @Test
    void addCartItemInCart_ProductNotFoundExceptionTest(){
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        CartItemCreateDTO cartItemCreateDTO = new CartItemCreateDTO(999L, 1L);

        assertThrows(NotFoundException.class, () -> cartService.addCartItemInCart(1L, cartItemCreateDTO));
    }

    @Test
    void getCarts_successTest() {
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        when(cartRepository.findAll()).thenReturn(List.of(existCart));

        List<CartResponseDTO> result = cartService.getCarts();

        assertThat(result.getFirst().getUserId()).isEqualTo(1L);
        assertThat(result.getFirst().getTotalCartPrice()).isEqualTo(0L);
    }

    @Test
    void getCartByUserId_successTest(){
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        existCart.addCartItemInCart(existProduct, 1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));

        CartResponseDTO result = cartService.getCart(1L);

        assertThat(result.getTotalCartPrice()).isEqualTo(20260828L);
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    void getCartByUserId_cartNotFoundExceptionTest(){
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cartService.getCart(999L));
    }

    @Test
    void patchCart_successTest(){
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        ReflectionTestUtils.setField(existProduct, "productId", 1L);

        existCart.addCartItemInCart(existProduct, 1L);

        CartUpdateDTO cartUpdateDTO = new CartUpdateDTO(List.of(
                new CartItemUpdateDTO(1L, 2L)
        ));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));

        CartResponseDTO result = cartService.patchCart(1L, cartUpdateDTO);

        assertThat(result.getTotalCartPrice()).isEqualTo(20260828L * 2L);
    }

    @Test
    void patchCart_cartNotFoundExceptionTest(){
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        CartUpdateDTO cartUpdateDTO = new CartUpdateDTO(List.of(
                new CartItemUpdateDTO(1L, 2L)
        ));

        assertThrows(NotFoundException.class, () -> cartService.patchCart(999L, cartUpdateDTO));
    }

    @Test
    void deleteCart_successTest(){
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));

        cartService.deleteCart(1L);

        verify(cartRepository).deleteByUserId(1L);
    }

    @Test
    void deleteCart_cartNotFoundExceptionTest(){
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cartService.deleteCart(999L));
    }

    @Test
    void deleteCartItem_successTest(){
        Cart existCart = Cart.builder()
                .userId(1L)
                .build();

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        ReflectionTestUtils.setField(existProduct, "productId", 1L);

        existCart.addCartItemInCart(existProduct, 1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(existCart));

        cartService.deleteCartItem(1L, 1L);

        assertThat(existCart.getCartItemList()).isEmpty();
    }

    @Test
    void deleteCartItem_cartNotFoundExceptionTest(){
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cartService.deleteCartItem(999L, 999L));
    }
}

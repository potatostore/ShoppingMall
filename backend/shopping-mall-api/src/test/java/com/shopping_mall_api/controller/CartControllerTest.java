package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemCreateDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.cart.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CartService cartService;

    @Test
    void addCartItemInCart_successTest() throws Exception{
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                .userId(userId)
                .cartItemList(List.of())
                .totalCartPrice(10000L)
                .build();

        CartItemCreateDTO cartItemCreateDTO = CartItemCreateDTO.builder()
                        .productId(userId)
                        .quantity(20260907L)
                        .build();

        when(cartService.addCartItemInCart(eq(userId), any(CartItemCreateDTO.class))).thenReturn(cartResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post(ApiURLNames.cartURL + ApiURLNames.addCartItemInCartURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : add cartItem in cart"))
                .andExpect(jsonPath("$.data.totalCartPrice").value(10000L));
    }

    @Test
    void getCarts_successTest() throws Exception{
        Long userId = 1L;
        CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                .userId(userId)
                .cartItemList(List.of())
                .totalCartPrice(10000L)
                .build();

        when(cartService.getCarts()).thenReturn(List.of(cartResponseDTO));

        mockMvc.perform(get(ApiURLNames.cartURL + ApiURLNames.findCartsURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all carts"))
                .andExpect(jsonPath("$.data[0].totalCartPrice").value(10000L));
    }

    @Test
    void getCart_successTest() throws Exception{
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                .userId(userId)
                .cartItemList(List.of())
                .totalCartPrice(10000L)
                .build();

        when(cartService.getCart(1L)).thenReturn(cartResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get(ApiURLNames.cartURL + ApiURLNames.findCartURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get cart (1)"))
                .andExpect(jsonPath("$.data.totalCartPrice").value(10000L));
    }

    @Test
    void patchCart_successTest() throws Exception{
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                .userId(userId)
                .cartItemList(List.of())
                .totalCartPrice(10000L)
                .build();

        CartUpdateDTO cartUpdateDTO = CartUpdateDTO.builder()
                        .cartItemUpdateDTOList(List.of())
                        .build();

        when(cartService.patchCart(eq(userId), any(CartUpdateDTO.class))).thenReturn(cartResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(patch(ApiURLNames.cartURL + ApiURLNames.updateCartURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : patch cart (1)"))
                .andExpect(jsonPath("$.data.totalCartPrice").value(10000L));
    }

    @Test
    void deleteCart_successTest() throws Exception{
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        try{
            mockMvc.perform(delete(ApiURLNames.cartURL + ApiURLNames.deleteCartURL))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(cartService).deleteCart(userId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void deleteCartItemInCart_successTest() throws Exception{
        Long userId = 1L;
        Long productId = 1L;

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        try{
            mockMvc.perform(delete(ApiURLNames.cartURL + ApiURLNames.deleteCartItemInCartURL, productId))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(cartService).deleteCartItem(1L, 1L);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}

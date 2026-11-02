package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.likes.LikeStatusResponseDTO;
import com.shopping_mall_api.dto.likes.LikedProductResponseDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.service.likes.LikesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikesController.class)
@AutoConfigureMockMvc(addFilters = false)
class LikesControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private LikesService likesService;

    @BeforeEach
    void login(){
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void like() throws Exception{
        when(likesService.like(1L, 7L)).thenReturn(new LikeStatusResponseDTO(7L, true, 3L));

        mockMvc.perform(post(ApiURLNames.productLikesURL, 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked").value(true))
                .andExpect(jsonPath("$.data.likeCount").value(3));
    }

    @Test
    void unlike() throws Exception{
        when(likesService.unlike(1L, 7L)).thenReturn(new LikeStatusResponseDTO(7L, false, 2L));

        mockMvc.perform(delete(ApiURLNames.productLikesURL, 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked").value(false));
    }

    @Test
    void like_unknownProduct_404() throws Exception{
        when(likesService.like(1L, 999L)).thenThrow(new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(post(ApiURLNames.productLikesURL, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cannot found product"));
    }

    @Test
    void myLikes() throws Exception{
        when(likesService.getMyLikes(1L)).thenReturn(List.of(
                new LikedProductResponseDTO(7L, "감자", 1000L, 10L, LocalDateTime.of(2026, 11, 2, 12, 0))));

        mockMvc.perform(get(ApiURLNames.myLikesURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(7))
                .andExpect(jsonPath("$.data[0].name").value("감자"));
    }

    @Test
    void likeStatus() throws Exception{
        when(likesService.getStatus(1L, 7L)).thenReturn(new LikeStatusResponseDTO(7L, true, 1L));

        mockMvc.perform(get(ApiURLNames.myProductLikeURL, 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.liked").value(true));
    }
}

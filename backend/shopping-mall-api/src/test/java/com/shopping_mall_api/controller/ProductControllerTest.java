package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailResponseDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProductService productService;

    @Test
    void createProduct_successTest() throws Exception{
        Long productId = 1L;
        ProductDetailCreateDTO productDetailCreateDTO = ProductDetailCreateDTO.builder()
                .detail("product-test-detail")
                .build();

        ProductCreateDTO productCreateDTO = ProductCreateDTO.builder()
                .name("water")
                .price(20260907L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail"))
        );

        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productResponseDTO);

        mockMvc.perform(post(ApiURLNames.productURL + ApiURLNames.createProductURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : product create"))
                .andExpect(jsonPath("$.data.name").value("water"))
                .andExpect(jsonPath("$.data.price").value(20260907L))
                .andExpect(jsonPath("$.data.productDetailResponseDTOList[0].detail").value("product-test-detail"));
    }

    @Test
    void getProducts_successTest() throws Exception{
        Long productId = 1L;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail"))
        );

        when(productService.getProducts()).thenReturn(List.of(productResponseDTO));

        mockMvc.perform(get(ApiURLNames.productURL + ApiURLNames.findProductsURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all products"))
                .andExpect(jsonPath("$.data[0].name").value("water"))
                .andExpect(jsonPath("$.data[0].price").value(20260907L))
                .andExpect(jsonPath("$.data[0].productDetailResponseDTOList[0].detail").value("product-test-detail"));
    }

    @Test
    void getProduct_successTest() throws Exception{
        Long productId = 1L;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
                productId, null, null
        );

        when(productService.getProduct(any(Long.class))).thenReturn(productResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get(ApiURLNames.productURL + ApiURLNames.findProductURL, productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get product (1)"))
                .andExpect(jsonPath("$.data.name").value("water"))
                .andExpect(jsonPath("$.data.price").value(20260907L))
                .andExpect(jsonPath("$.data.productDetailResponseDTOList[0].detail").value("product-test-detail"));
    }

    @Test
    void patchProduct_successTest() throws Exception{
        Long productId = 1L;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail"))
        );

        ProductUpdateDTO productUpdateDTO = ProductUpdateDTO.builder()
                .name("water")
                .price(20260907L)
                .productDetailUpdateDTOList(List.of(new ProductDetailUpdateDTO("product-test-detail")))
                .build();

        when(productService.patchProduct(any(Long.class), any(ProductUpdateDTO.class))).thenReturn(productResponseDTO);

        mockMvc.perform(patch(ApiURLNames.productURL + ApiURLNames.updateProductURL, productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : patch product (1)"))
                .andExpect(jsonPath("$.data.name").value("water"))
                .andExpect(jsonPath("$.data.price").value(20260907L))
                .andExpect(jsonPath("$.data.productDetailResponseDTOList[0].detail").value("product-test-detail"));
    }

    @Test
    void deleteProduct_successTest() throws Exception{
        Long productId = 1L;

        mockMvc.perform(delete(ApiURLNames.productURL + ApiURLNames.deleteProductURL, productId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(productService).deleteProduct(productId);
    }
}

package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductPageResponseDTO;
import com.shopping_mall_api.dto.product.ProductSortType;
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
import static org.mockito.Mockito.*;
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
                .stock(100L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail")),
                100L
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
    void createProduct_InvalidNameTest() throws Exception{
        ProductDetailCreateDTO productDetailCreateDTO = ProductDetailCreateDTO.builder()
                .detail("product-test-detail")
                .build();

        ProductCreateDTO productCreateDTO = ProductCreateDTO.builder()
                .name("")
                .price(20260907L)
                .stock(100L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        mockMvc.perform(post(ApiURLNames.productURL + ApiURLNames.createProductURL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("product name must not be blank"));

        verifyNoInteractions(productService);
    }

    @Test
    void getProducts_successTest() throws Exception{
        Long productId = 1L;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail")),
                100L
        );

        ProductPageResponseDTO page = new ProductPageResponseDTO(List.of(productResponseDTO), 0, 20, 1L, 1, false);

        when(productService.getProducts(null, null, null, ProductSortType.LATEST, 0, 20)).thenReturn(page);

        mockMvc.perform(get(ApiURLNames.productURL + ApiURLNames.findProductsURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all products"))
                .andExpect(jsonPath("$.data.content[0].name").value("water"))
                .andExpect(jsonPath("$.data.content[0].price").value(20260907L))
                .andExpect(jsonPath("$.data.content[0].productDetailResponseDTOList[0].detail").value("product-test-detail"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    void getProducts_withSearchParamsTest() throws Exception{
        when(productService.getProducts("감자", 1000L, 5000L, ProductSortType.PRICE_ASC, 2, 10))
                .thenReturn(new ProductPageResponseDTO(List.of(), 2, 10, 0L, 0, false));

        mockMvc.perform(get(ApiURLNames.productURL + ApiURLNames.findProductsURL)
                        .param("keyword", "감자")
                        .param("minPrice", "1000")
                        .param("maxPrice", "5000")
                        .param("sort", "PRICE_ASC")
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(2));

        verify(productService).getProducts("감자", 1000L, 5000L, ProductSortType.PRICE_ASC, 2, 10);
    }

    @Test
    void getProducts_invalidSortTest() throws Exception{
        // 정의되지 않은 정렬 값은 타입 변환 실패로 400
        mockMvc.perform(get(ApiURLNames.productURL + ApiURLNames.findProductsURL).param("sort", "RANDOM"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void getProduct_successTest() throws Exception{
        Long productId = 1L;
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                productId,
                "water",
                20260907L,
                List.of(new ProductDetailResponseDTO("product-test-detail")),
                100L
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
                List.of(new ProductDetailResponseDTO("product-test-detail")),
                100L
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
    void patchProduct_InvalidNameTest() throws Exception{
        ProductUpdateDTO productUpdateDTO = ProductUpdateDTO.builder()
                .name("1029387412093784")
                .price(20260907L)
                .productDetailUpdateDTOList(List.of(new ProductDetailUpdateDTO("product-test-detail")))
                .build();

        mockMvc.perform(patch(ApiURLNames.productURL + ApiURLNames.updateProductURL, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Product name must be 3~20 length"));

        verifyNoInteractions(productService);
    }

    @Test
    void deleteProduct_successTest() throws Exception{
        Long productId = 1L;

        mockMvc.perform(delete(ApiURLNames.productURL + ApiURLNames.deleteProductURL, productId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(productService).deleteProduct(productId);
    }

    @Test
    void createProduct_emptyDetailListTest() throws Exception{
        String body = """
                {"name": "감자", "price": 1000, "stock": 10, "productDetailCreateDTOList": []}
                """;

        mockMvc.perform(post(ApiURLNames.productURL + ApiURLNames.createProductURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("productDetailCreateDTOList must have 1 productDetailCreateDTO at least"));

        verify(productService, never()).createProduct(any());
    }
}

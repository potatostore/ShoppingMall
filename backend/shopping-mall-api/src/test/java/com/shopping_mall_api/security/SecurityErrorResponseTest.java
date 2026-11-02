package com.shopping_mall_api.security;

import com.shopping_mall_api.controller.ProductController;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.global.security.SecurityConfig;
import com.shopping_mall_api.repository.redis.AccessTokenBlacklistRepository;
import com.shopping_mall_api.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 401 / 403 응답 바디 형식 확인용 (전체 인가 규칙 테스트는 Phase 2 범위)
@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class SecurityErrorResponseTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private ProductService productService;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    private static final String BODY = """
            {"name": "감자", "price": 1000, "stock": 10, "productDetailCreateDTOList": [{"detail": "detail"}]}
            """;

    @Test
    void anonymous_returns401ApiResponse() throws Exception{
        mockMvc.perform(post(ApiURLNames.productURL + ApiURLNames.createProductURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid or missing access token"));
    }

    @Test
    void userCallsAdminApi_returns403ApiResponse() throws Exception{
        mockMvc.perform(post(ApiURLNames.productURL + ApiURLNames.createProductURL)
                        .with(user("1").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }
}

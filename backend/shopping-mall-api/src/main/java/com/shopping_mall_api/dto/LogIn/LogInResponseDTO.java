package com.shopping_mall_api.dto.LogIn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogInResponseDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String name;
}
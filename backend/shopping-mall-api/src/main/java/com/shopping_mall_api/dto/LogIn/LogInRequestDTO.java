package com.shopping_mall_api.dto.LogIn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogInRequestDTO {
    private String logInId;
    private String logInPassword;
}

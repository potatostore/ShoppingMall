package com.shopping_mall_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogInRequestDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String logInPassword;
}

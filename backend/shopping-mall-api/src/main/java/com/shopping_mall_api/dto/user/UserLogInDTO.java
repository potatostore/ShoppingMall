package com.shopping_mall_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLogInDTO {

    @NotBlank(message = "email must not be blank")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "password must not be blank")
    private String password;
}
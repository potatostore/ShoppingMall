package com.shopping_mall_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    @NotBlank(message = "name must not be blank")
    private String signUpName;

    @Email
    @NotBlank(message = "email must not be blank")
    private String signUpEmail;

    @NotBlank(message = "logInPassword must not be blank")
    private String signUpPassword;

    @NotBlank
    private String signUpRole;

    @NotBlank(message = "phoneNumber must not be blank")
    private String signUpPhoneNumber;

    @NotNull(message = "birthday must not be null")
    private LocalDate signUpBirthday;
}
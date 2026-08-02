package com.shopping_mall_api.dto.User;

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
    private String name;

    @Email
    @NotBlank(message = "email must not be blank")
    private String email;

    @NotBlank(message = "logInId must not be blank")
    private String logInId;

    @NotBlank(message = "logInPassword must not be blank")
    private String logInPassword;

    @NotBlank(message = "phoneNumber must not be blank")
    private String phoneNumber;

    @NotNull(message = "birthday must not be null")
    private LocalDate birthday;
}

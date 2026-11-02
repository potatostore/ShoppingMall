package com.shopping_mall_api.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    private String name;
    @Email(message = "email format is invalid")
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
}

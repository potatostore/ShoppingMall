package com.shopping_mall_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^$|^[가-힣]{3,5}", message = "Name must be 3~5 length with Korean")
    private String name;

    @Email(message = "email format is invalid")
    private String email;

    @Pattern(regexp = "^$|010[0-9]{4}[0-9]{4}", message = "Phone number must be 010xxxxxxxx format")
    private String phoneNumber;

    private LocalDate birthday;
}

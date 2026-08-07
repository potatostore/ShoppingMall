package com.shopping_mall_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;
    private String email;
    private String logInId;
    private String logInPassword;
    private String phoneNumber;
    private LocalDate birthday;
}

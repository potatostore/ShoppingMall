package com.shopping_mall_api.dto.user;

import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;

    public UserResponseDTO(User user){
        CheckConfig.npeCheck(user, "user");

        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.birthday = user.getBirthday();
    }
}

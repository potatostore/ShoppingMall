package com.shopping_mall_api.dto.User;

import com.shopping_mall_api.entity.user.User;
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
    private String logInId;
    private String phoneNumber;
    private LocalDate birthday;

    public UserResponseDTO(User user){
        if(user == null){
            throw new IllegalArgumentException("user must not be null");
        }

        this.name = user.getName();
        this.email = user.getEmail();
        this.logInId = user.getLogInId();
        this.phoneNumber = user.getPhoneNumber();
        this.birthday = user.getBirthday();
    }
}

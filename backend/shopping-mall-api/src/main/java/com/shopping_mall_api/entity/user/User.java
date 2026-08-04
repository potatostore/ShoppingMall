package com.shopping_mall_api.entity.user;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = TableNames.userTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String logInPassword;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthday;

    @Builder
    public User(String email, String logInPassword, String name, String phoneNumber, LocalDate birthday) {
        CheckConfig.npeCheck(email, "email");
        CheckConfig.npeCheck(logInPassword, "password");
        CheckConfig.npeCheck(name, "name");
        CheckConfig.npeCheck(phoneNumber, "phoneNumber");
        CheckConfig.npeCheck(birthday, "birthday");

        this.email = email;
        this.logInPassword = logInPassword;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public void patchUser(UserUpdateDTO userUpdateDTO, String encodedLogInPassword){
        CheckConfig.npeCheck(userUpdateDTO, "userCreateDTO");

        if(userUpdateDTO.getEmail() != null){
            this.email = userUpdateDTO.getEmail();
        }
        if(encodedLogInPassword != null){
            this.logInPassword = encodedLogInPassword;
        }
        if(userUpdateDTO.getName() != null){
            this.name = userUpdateDTO.getName();
        }
        if(userUpdateDTO.getPhoneNumber() != null){
            this.phoneNumber = userUpdateDTO.getPhoneNumber();;
        }
        if(userUpdateDTO.getBirthday() != null){
            this.birthday = userUpdateDTO.getBirthday();
        }
    }

    public void putUser(UserUpdateDTO userUpdateDTO, String encodedLogInPassword){
        CheckConfig.npeCheck(userUpdateDTO, "userUpdateDTO");
        CheckConfig.npeCheck(userUpdateDTO.getEmail(), "email");
        CheckConfig.npeCheck(encodedLogInPassword, "encodedLogInPassword");
        CheckConfig.npeCheck(userUpdateDTO.getName(), "name");
        CheckConfig.npeCheck(userUpdateDTO.getPhoneNumber(), "phoneNumber");
        CheckConfig.npeCheck(userUpdateDTO.getBirthday(), "birthDay");

        this.email = userUpdateDTO.getEmail();
        this.logInPassword = encodedLogInPassword;
        this.name = userUpdateDTO.getName();
        this.phoneNumber = userUpdateDTO.getPhoneNumber();
        this.birthday = userUpdateDTO.getBirthday();
    }
}
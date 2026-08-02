package com.shopping_mall_api.entity.user;

import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

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
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(logInPassword, "password must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(phoneNumber, "phoneNumber must not be null");
        Objects.requireNonNull(birthday, "birthday must not be null");

        this.email = email;
        this.logInPassword = logInPassword;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }
}
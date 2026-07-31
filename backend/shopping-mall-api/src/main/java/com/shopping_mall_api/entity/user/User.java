package com.shopping_mall_api.entity.user;

import com.shopping_mall_api.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = TableNames.userTableName)
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String logInId;

    @NotBlank
    @Column(nullable = false)
    private String logInPassword;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthday;

    @Builder
    public User(String name, String email, String logInId, String logInPassword,
                String phoneNumber, LocalDate birthday){
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.logInId = Objects.requireNonNull(logInId, "LogIn ID must not be null");
        this.logInPassword = Objects.requireNonNull(logInPassword, "Password must not be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number must not be null");
        this.birthday = Objects.requireNonNull(birthday, "Birthday must not be null");
    }
}

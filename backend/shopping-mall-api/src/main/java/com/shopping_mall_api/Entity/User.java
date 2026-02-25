package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Getter
@Entity(name = TableNames.userTableName)
@Table(name = TableNames.userTableName)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String logInId;
    private String password;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    private String birthday;
    @Column(name = "createdAt")
    private String createdAt;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogInId(String logInId){
        this.logInId = logInId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

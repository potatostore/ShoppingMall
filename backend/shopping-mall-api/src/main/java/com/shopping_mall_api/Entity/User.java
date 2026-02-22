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
    private String password;
    private int age;
    @Column(name = "phone_number")
    private String phoneNumber;
    private Date birthday;
    @Column(name = "created_at")
    private Date createdAt;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

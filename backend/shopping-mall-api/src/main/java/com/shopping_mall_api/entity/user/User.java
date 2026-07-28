package com.shopping_mall_api.entity.user;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = TableNames.userTableName)
@Table(name = TableNames.userTableName)
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String email;
    private String logInId;
    private String logInPassword;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    private String birthday;
    @Column(name = "createdAt")
    private String createdAt;
    private String likeTableId;
    private String cartTableId;
    private String recentWatchingProductId;
}

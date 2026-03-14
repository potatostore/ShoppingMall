package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = TableNames.userTableName)
@Table(name = TableNames.userTableName)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String signInId;
    private String signInPassword;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    private String birthday;
    @Column(name = "createdAt")
    private String createdAt;
    private Integer likeTableId;
    private Integer previousSearchTableId;
    private Integer cartTableId;
    private Integer problemOrderTableId;
    private Integer recentWatchingTableId;
    private boolean locked;
}

package com.shopping_mall_api.entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = TableNames.employeeTableName)
@Table(name = TableNames.employeeTableName)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String logInId;
    private String password;
    private String name;
}


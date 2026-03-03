package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity(name = TableNames.employeeTableName)
@Table(name = TableNames.employeeTableName)
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String logInId;
    private String password;
    private String name;
    private
}


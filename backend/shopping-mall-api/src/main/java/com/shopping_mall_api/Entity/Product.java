package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity(name = TableNames.productTableName)
@Table(name = TableNames.productTableName)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    private String name;
    @Setter
    private int price;

    public void setId(Integer id) {
        this.id = id;
    }
}
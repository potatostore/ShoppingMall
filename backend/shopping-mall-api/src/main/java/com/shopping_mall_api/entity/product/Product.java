package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = TableNames.productTableName)
@Table(name = TableNames.productTableName)
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private int price;
    private String productImagePath;
    private String productQAId;
}
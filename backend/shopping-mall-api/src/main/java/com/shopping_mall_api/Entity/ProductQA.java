package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity(name = TableNames.productQATableName)
@Table(name = TableNames.productQATableName)
public class ProductQA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}

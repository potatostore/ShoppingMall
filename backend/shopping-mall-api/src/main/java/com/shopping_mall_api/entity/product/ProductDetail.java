package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = TableNames.productQATableName)
@Table(name = TableNames.productQATableName)
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String detail;
}

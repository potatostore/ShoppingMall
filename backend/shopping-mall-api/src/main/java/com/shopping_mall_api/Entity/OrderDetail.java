package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;

// Order Product List
@Getter
@Entity(name = TableNames.orderDetailTableName)
@Table(name = TableNames.orderDetailTableName)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}

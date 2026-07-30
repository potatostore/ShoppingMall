package com.shopping_mall_api.entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = TableNames.likeTableName)
@Table(name = TableNames.likeTableName)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}

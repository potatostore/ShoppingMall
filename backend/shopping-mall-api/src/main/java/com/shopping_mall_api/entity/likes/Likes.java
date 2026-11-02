package com.shopping_mall_api.entity.likes;

import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = TableNames.likesTableName)
@Table(name = TableNames.likesTableName)
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}

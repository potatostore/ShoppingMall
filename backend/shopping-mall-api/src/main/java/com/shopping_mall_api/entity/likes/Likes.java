package com.shopping_mall_api.entity.likes;

import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 찜하기: 사용자 1명이 상품 1개를 한 번만 찜할 수 있도록 (user_id, product_id) unique
// PK 컬럼 이름은 기존 likes 테이블과 같은 id 유지 (ddl-auto update가 auto_increment 컬럼을 하나 더 만들려다 실패하지 않도록)
@Entity
@Table(name = TableNames.likesTableName,
       uniqueConstraints = @UniqueConstraint(name = "uk_likes_user_product", columnNames = {"user_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Likes(User user, Product product){
        CheckConfig.npeCheck(user, "user");
        CheckConfig.npeCheck(product, "product");

        this.user = user;
        this.product = product;
    }
}

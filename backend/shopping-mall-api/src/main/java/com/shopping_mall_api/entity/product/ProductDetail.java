package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = TableNames.productDetailTableName)
@NoArgsConstructor
@Getter
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productDetailId;

    @Column(nullable = false)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductDetail(String detail){
        Objects.requireNonNull(detail, "detail must not be null");

        if(detail.isBlank()){
            throw new IllegalArgumentException("detail must not be blank");
        }

        this.detail = detail;
    }
}

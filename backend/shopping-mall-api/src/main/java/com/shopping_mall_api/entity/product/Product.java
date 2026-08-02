package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = TableNames.productTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull(message = "price must not be null")
    @Min(value = 0)
    @Column(nullable = false)
    private Long price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private List<ProductDetail> productDetailList;

    @Builder
    public Product(String name, Long price) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("price must be greater than or equal to 0");
        }

        this.productDetailList = new ArrayList<>();
        this.name = name;
        this.price = price;
    }
}
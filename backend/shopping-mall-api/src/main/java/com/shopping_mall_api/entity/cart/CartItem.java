package com.shopping_mall_api.entity.cart;


import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.dto.cart.cartItem.CartItemResponseDTO;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = TableNames.cartItemTableName)
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @NotNull(message = "productId must not be null")
    @Column(nullable = false)
    private Long productId;

    @NotNull(message = "curProductPrice must not be null")
    @Min(value = 0, message = "curProductPrice must not be negative")
    @Column(nullable = false)
    private Long curProductPrice;

    @NotNull(message = "quantity must not be null")
    @Min(value = 0, message = "quantity must not be negative")
    @Column(nullable = false)
    private Long quantity;

    @Min(value = 0, message = "totalProductPrice must not be negative")
    @Column(nullable = false)
    private Long totalProductPrice;

    @Builder
    public CartItem(Long productId, Long curProductPrice, Long quantity){
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(curProductPrice, "curProductPrice must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");

        if(curProductPrice < 0){
            throw new IllegalArgumentException("curProductPrice must not be negative");
        }
        if(quantity < 0){
            throw new IllegalArgumentException("quantity must not be negative");
        }

        this.productId = productId;
        this.curProductPrice = curProductPrice;
        this.quantity = quantity;

        updateTotalProductPrice();
    }

    public CartItem(CartItemResponseDTO cartItemResponseDTO){
        this(cartItemResponseDTO.getProductId(),
             cartItemResponseDTO.getCurProductPrice(),
             cartItemResponseDTO.getQuantity());
    }

    public void updateQuantity(Long quantity){
        Objects.requireNonNull(quantity, "quantity must not be null");

        if(quantity < 0){
            throw new IllegalArgumentException("quantity must not be negative");
        }

        this.quantity = quantity;

        updateTotalProductPrice();
    }

    public void updateCurProductPrice(Long curProductPrice){
        Objects.requireNonNull(curProductPrice, "curProductPrice must not be null");

        if(curProductPrice < 0){
            throw new IllegalArgumentException("curProductPrice must not be negative");
        }

        this.curProductPrice = curProductPrice;

        updateTotalProductPrice();
    }

    public void updateTotalProductPrice(){
        this.totalProductPrice = this.quantity * this.curProductPrice;
    }
}

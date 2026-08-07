package com.shopping_mall_api.entity.cart;


import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull(message = "quantity must not be null")
    @Min(value = 0, message = "quantity must not be negative")
    @Column(nullable = false)
    private Long quantity;

    @Builder
    public CartItem(Cart cart, Product product, Long quantity){
        CheckConfig.npeCheck(cart, "cart");
        CheckConfig.npeCheck(product, "product");
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public void updateQuantity(Long quantity){
        CheckConfig.npeAndNegativeCheck(quantity, "quantity");

        this.quantity = quantity;
    }

    public Long getTotalCartItemPrice(){
        return product.getPrice() * this.quantity;
    }
}

package com.shopping_mall_api.entity.cart;


import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table
@Entity
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    @Min(value = 0)
    private Integer curProductPrice;

    @Column(nullable = false)
    @Min(value = 0)
    private Integer quantity;

    @Min(value = 0)
    private Integer totalProductPrice;

    @Builder
    public CartItem(Long productId, Integer curProductPrice, Integer quantity){
        this.productId = productId;
        this.curProductPrice = curProductPrice;
        this.quantity = quantity;

        this.totalProductPrice = (curProductPrice != null && quantity != null) ? curProductPrice * quantity : 0;
    }

    public CartItem(CartItemResponseDTO cartItemResponseDTO){
        this.productId = cartItemResponseDTO.getProductId();
        this.curProductPrice = cartItemResponseDTO.getCurProductPrice();;
        this.quantity = cartItemResponseDTO.getQuantity();
        this.totalProductPrice = cartItemResponseDTO.getTotalPrice();
    }

    public void updateQuantity(Integer quantity){
        this.quantity = quantity;
    }

    public void updateCurProductPrice(Integer curProductPrice){
        this.curProductPrice = curProductPrice;
    }

    public void updateTotalProductPrice(){
        this.totalProductPrice = quantity * curProductPrice;
    }

    public Integer checkQuantity() {
        return this.quantity;
    }
}

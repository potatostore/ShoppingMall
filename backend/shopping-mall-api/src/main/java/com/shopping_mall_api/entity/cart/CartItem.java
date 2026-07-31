package com.shopping_mall_api.entity.cart;


import com.shopping_mall_api.TableNames;
import com.shopping_mall_api.dto.cart.cartItem.CartItemResponseDTO;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = TableNames.cartItemTableName)
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @Column(nullable = false)
    private Long productId;

    @Min(value = 0)
    private Integer curProductPrice;

    @Min(value = 0)
    private Integer quantity;

    @Min(value = 0)
    private Integer totalProductPrice;

    @Builder
    public CartItem(Long productId, Integer curProductPrice, Integer quantity){
        if(productId == null){
            throw new IllegalArgumentException("productId는 필수입니다.");
        }
        this.productId = productId;
        this.curProductPrice = (curProductPrice != null) ? curProductPrice : 0;
        this.quantity = (quantity != null) ? quantity : 0;

        this.totalProductPrice = (curProductPrice != null && quantity != null) ? curProductPrice * quantity : 0;
    }

    public CartItem(CartItemResponseDTO cartItemResponseDTO){
        this(cartItemResponseDTO.getProductId(),
             cartItemResponseDTO.getCurProductPrice(),
             cartItemResponseDTO.getQuantity());
    }

    public void updateQuantity(Integer quantity){
        this.quantity = (quantity != null && quantity >= 0) ? quantity : 0;
        updateTotalProductPrice();
    }

    public void updateCurProductPrice(Integer curProductPrice){
        this.curProductPrice = (curProductPrice != null && curProductPrice >= 0) ? curProductPrice : 0;
        updateTotalProductPrice();
    }

    public void updateTotalProductPrice(){
        int price = (this.curProductPrice != null) ? this.curProductPrice : 0;
        int qty = (this.quantity != null) ? this.quantity : 0;
        this.totalProductPrice = qty * price;
    }

    public Integer checkQuantity() {
        return this.quantity;
    }
}

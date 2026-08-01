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
    private Long curProductPrice;

    @Min(value = 0)
    private Long quantity;

    @Min(value = 0)
    private Long totalProductPrice;

    @Builder
    public CartItem(Long productId, Long curProductPrice, Long quantity){
        if(productId == null){
            throw new IllegalArgumentException("productId는 필수입니다.");
        }
        this.productId = productId;
        this.curProductPrice = (curProductPrice != null) ? curProductPrice : 0L;
        this.quantity = (quantity != null) ? quantity : 0L;

        this.totalProductPrice = (curProductPrice != null && quantity != null) ? curProductPrice * quantity : 0L;
    }

    public CartItem(CartItemResponseDTO cartItemResponseDTO){
        this(cartItemResponseDTO.getProductId(),
             cartItemResponseDTO.getCurProductPrice(),
             cartItemResponseDTO.getQuantity());
    }

    public void updateQuantity(Long quantity){
        this.quantity = (quantity != null && quantity >= 0) ? quantity : 0L;
        updateTotalProductPrice();
    }

    public void updateCurProductPrice(Long curProductPrice){
        this.curProductPrice = (curProductPrice != null && curProductPrice >= 0) ? curProductPrice : 0L;
        updateTotalProductPrice();
    }

    public void updateTotalProductPrice(){
        Long price = (this.curProductPrice != null) ? this.curProductPrice : 0L;
        Long qty = (this.quantity != null) ? this.quantity : 0L;
        this.totalProductPrice = qty * price;
    }

    public Long checkQuantity() {
        return this.quantity;
    }
}

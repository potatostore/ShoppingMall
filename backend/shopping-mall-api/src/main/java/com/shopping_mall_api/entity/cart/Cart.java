package com.shopping_mall_api.entity.cart;

import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemCreateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@Table(name = TableNames.cartTableName)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @NotNull
    @Column(nullable = false)
    private Long userId;

    // 목록 조회 시 컬렉션을 엔티티마다 따로 조회하지 않고 IN 쿼리로 묶음 (N+1 → 1+1)
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItemList;

    @Min(value = 0)
    private Long totalCartPrice;

    @Builder
    public Cart(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        this.userId = userId;
        this.cartItemList = new ArrayList<>();
        this.totalCartPrice = 0L;
    }

    public void updateTotalCartPrice(){
        CheckConfig.npeCheck(cartItemList, "cartItemList");

        this.totalCartPrice = cartItemList.stream()
                .mapToLong(CartItem::getTotalCartItemPrice).sum();
    }

    // 이미 담긴 상품이면 수량만 늘림 (같은 상품의 CartItem이 여러 개면 patchCart의 toMap에서 Duplicate key 500)
    public CartItem addCartItemInCart(Product product, Long quantity){
        CheckConfig.npeCheck(product, "product");
        CheckConfig.npeCheck(quantity, "quantity");

        CartItem cartItem = this.cartItemList.stream()
                .filter(item -> item.getProduct().getProductId().equals(product.getProductId()))
                .findFirst()
                .orElse(null);

        if(cartItem != null){
            cartItem.updateQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem(product, quantity);
            this.cartItemList.add(cartItem);
            cartItem.assignCart(this);
        }

        updateTotalCartPrice();

        return cartItem;
    }

    public void patchCart(CartUpdateDTO cartUpdateDTO){
        CheckConfig.npeCheck(cartUpdateDTO, "cartUpdateDTO");

        List<CartItemUpdateDTO> patchCartItem = cartUpdateDTO.getCartItemUpdateDTOList();

        CheckConfig.npeAndEmptyCheck(patchCartItem, "patchCartItem");

        Map<Long, CartItem> existingItemMap = this.cartItemList.stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getProductId(),
                        item -> item
                ));

        for(CartItemUpdateDTO dto : patchCartItem){
            CartItem cartItem = existingItemMap.get(dto.getProductId());

            if(cartItem == null){
                throw new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND);
            }

            cartItem.updateQuantity(dto.getQuantity());
        }

        updateTotalCartPrice();
    }

    public void deleteCartItem(Long productId){
        CheckConfig.npeCheck(productId, "productId");

        this.cartItemList.removeIf(cartItem -> cartItem.getProduct().getProductId().equals(productId));

        updateTotalCartPrice();
    }
}
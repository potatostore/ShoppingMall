package com.shopping_mall_api.entity.cart;

import com.shopping_mall_api.dto.cart.CartUpdateDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemUpdateDTO;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import jakarta.persistence.*;
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
        CheckConfig.npeAndEmptyCheck(cartItemList, "cartItemList");

        this.totalCartPrice = cartItemList.stream()
                .mapToLong(CartItem::getTotalCartItemPrice).sum();
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
                throw new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "cannot found cart item (" + dto.getProductId() + ")");
            }

            cartItem.updateQuantity(dto.getQuantity());
        }
    }

    public void deleteCartItem(Long productId){
        CheckConfig.npeCheck(productId, "productId");

        this.cartItemList.removeIf(cartItem -> cartItem.getProduct().getProductId().equals(productId));
    }
}
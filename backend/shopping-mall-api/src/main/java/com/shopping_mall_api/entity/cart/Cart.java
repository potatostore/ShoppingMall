package com.shopping_mall_api.entity.cart;

import com.shopping_mall_api.global.constant.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = TableNames.cartTableName)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItemList;

    @Min(value = 0)
    private Long totalCartPrice;

    @Builder
    public Cart(Long userId){
        Objects.requireNonNull(userId, "userId must not be null");

        this.userId = userId;
        this.cartItemList = new ArrayList<>();
        this.totalCartPrice = 0L;
    }

    public void addCartItemInList(CartItem cartItem){
        Objects.requireNonNull(cartItem, "cartItem must not be null");

        this.cartItemList.add(cartItem);

        updateTotalCartPrice();
    }

    public void updateTotalCartPrice(){
        Objects.requireNonNull(this.cartItemList, "cartItemList has no any cartItem");

        if(this.cartItemList.isEmpty()){
            this.totalCartPrice = 0L;
            return;
        }

        this.totalCartPrice = cartItemList.stream()
                .mapToLong(CartItem::getTotalProductPrice).sum();
    }
}
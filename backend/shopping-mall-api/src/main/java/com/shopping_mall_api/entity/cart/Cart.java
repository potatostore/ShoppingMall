package com.shopping_mall_api.entity.cart;

import com.shopping_mall_api.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import lombok.Builder;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableNames.cartTableName)
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> cartItemList;

    private Integer totalCartPrice;

    @Builder
    public Cart(Long userId){
        this.userId = userId;

        this.cartItemList = new ArrayList<>();

        this.totalCartPrice = 0;
    }

    public void addCartItemInList(CartItem item){
        this.cartItemList.add(item);
    }

    public void updateTotalCartPrice(){
        if(this.cartItemList.isEmpty() || this.cartItemList == null){
            this.totalCartPrice = 0;
            return;
        }

        this.totalCartPrice = cartItemList.stream().mapToInt(item -> item.getTotalProductPrice()).sum();
    }
}
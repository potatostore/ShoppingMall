package com.shopping_mall_api.entity.cart;

import com.shopping_mall_api.TableNames;
import com.shopping_mall_api.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.ArrayList;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> cartItemList;

    @Min(value = 0)
    private Integer totalCartPrice;

    @Builder
    public Cart(Long userId){
        if(userId == null){
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        this.userId = userId;
        this.cartItemList = new ArrayList<>();
        this.totalCartPrice = 0;
    }

    public void addCartItemInList(CartItem item){
        this.cartItemList.add(item);
    }

    public void updateTotalCartPrice(){
        if(this.cartItemList.isEmpty()){
            this.totalCartPrice = 0;
            return;
        }

        this.totalCartPrice = cartItemList.stream()
                .mapToInt(item -> (item != null && item.getTotalProductPrice() != null) ?
                        item.getTotalProductPrice() : 0).sum();
    }
}
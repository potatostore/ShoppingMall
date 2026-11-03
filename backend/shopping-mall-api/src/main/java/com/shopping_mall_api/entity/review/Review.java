package com.shopping_mall_api.entity.review;

import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;

// [뼈대] 상품 리뷰. 구매확정(CONFIRMED)한 주문 항목에만 작성 가능, 주문 항목당 1개
// 구현 시: @Entity, @Table(name = "review", uniqueConstraints = order_item_id), BaseEntity 상속
public class Review {
    private Long reviewId;
    private User user;
    private Product product;
    private Long orderItemId;
    private Integer rating;     // 1~5
    private String content;

    public void update(Integer rating, String content){
        throw new UnsupportedOperationException("TODO");
    }
}

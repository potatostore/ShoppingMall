package com.shopping_mall_api.dto.product;

import org.springframework.data.domain.Sort;

// 상품 목록 정렬 (프론트 products 페이지의 기존 정렬 옵션과 같은 값). 같은 값끼리는 productId로 순서 고정
public enum ProductSortType {
    LATEST(Sort.by(Sort.Order.desc("productId"))),
    NAME(Sort.by(Sort.Order.asc("name"), Sort.Order.asc("productId"))),
    PRICE_ASC(Sort.by(Sort.Order.asc("price"), Sort.Order.asc("productId"))),
    PRICE_DESC(Sort.by(Sort.Order.desc("price"), Sort.Order.asc("productId")));

    private final Sort sort;

    ProductSortType(Sort sort){
        this.sort = sort;
    }

    public Sort getSort(){
        return sort;
    }
}

package com.shopping_mall_api.dto.product.productDetail;

import com.shopping_mall_api.entity.product.ProductDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetailResponseDTO {
    private String detail;

    @Builder
    public ProductDetailResponseDTO(ProductDetail productDetail){
        if(productDetail == null){
            throw new IllegalArgumentException("product detail must not be null");
        }

        this.detail = productDetail.getDetail();
    }
}

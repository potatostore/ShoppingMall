package com.shopping_mall_api.dto.product.productDetail;

import com.shopping_mall_api.entity.product.ProductDetail;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetailResponseDTO {
    private String detail;

    @Builder
    public ProductDetailResponseDTO(ProductDetail productDetail){
        CheckConfig.npeCheck(productDetail, "productDetail");

        this.detail = productDetail.getDetail();
    }
}

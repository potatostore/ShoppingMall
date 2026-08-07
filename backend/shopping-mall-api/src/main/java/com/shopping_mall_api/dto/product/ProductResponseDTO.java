package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailResponseDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductResponseDTO {
    private Long productId;
    private String name;
    private Long price;
    private List<ProductDetailResponseDTO> productDetailResponseDTOList;

    @Builder
    public ProductResponseDTO(Product product){
        CheckConfig.npeCheck(product, "product");

        this.productId = product.getProductId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.productDetailResponseDTOList = product.getProductDetailList().stream()
                .map(ProductDetailResponseDTO::new).toList();
    }
}

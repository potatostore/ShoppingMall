package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailResponseDTO;
import com.shopping_mall_api.entity.product.Product;
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
        if(product == null){
            throw new IllegalArgumentException("product object must not be null");
        }

        this.productId = product.getProductId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.productDetailResponseDTOList = product.getProductDetailList().stream()
                .map(ProductDetailResponseDTO::new).toList();
    }
}

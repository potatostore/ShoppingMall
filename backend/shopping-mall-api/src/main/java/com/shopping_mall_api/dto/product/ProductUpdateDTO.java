package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductUpdateDTO {
    private String name;
    private Long price;
    private List<ProductDetailUpdateDTO> productDetailUpdateDTOList;
}

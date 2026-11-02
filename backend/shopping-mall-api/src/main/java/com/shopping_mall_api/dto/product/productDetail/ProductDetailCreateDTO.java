package com.shopping_mall_api.dto.product.productDetail;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailCreateDTO {
    @NotBlank(message = "product detail must not be blank")
    private String detail;
}

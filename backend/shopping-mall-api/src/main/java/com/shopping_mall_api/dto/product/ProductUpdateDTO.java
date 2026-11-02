package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDTO {
    @Pattern(regexp = "^$|[a-z]{3,20}|[가-힣]{3,20}", message = "Product name must be 3~20 length")
    private String name;

    @Min(value = 0, message = "product price must not be negative")
    private Long price;

    @Valid
    private List<ProductDetailUpdateDTO> productDetailUpdateDTOList;

    @Min(value = 0, message = "product stock must not be negative")
    private Long stock;
}

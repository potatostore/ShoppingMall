package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
    @NotNull(message = "product name must not be null")
    @NotBlank(message = "product name must not be blank")
    private String name;

    @NotNull(message = "product price must not be negative")
    @Min(value = 0)
    private Long price;

    @Valid
    private List<ProductDetailCreateDTO> productDetailCreateDTOList;
}

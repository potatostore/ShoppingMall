package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDTO {
    @NotNull(message = "product name must not be null")
    @NotBlank(message = "product name must not be blank")
    private String name;

    @NotNull(message = "product price must not be null")
    @Min(value = 0, message = "product price must not be negative")
    private Long price;

    @Valid
    @NotEmpty(message = "productDetailCreateDTOList must have 1 productDetailCreateDTO at least")
    private List<ProductDetailCreateDTO> productDetailCreateDTOList;

    @NotNull(message = "product stock must not be null")
    @Min(value = 0, message = "product stock must not be negative")
    private Long stock;
}

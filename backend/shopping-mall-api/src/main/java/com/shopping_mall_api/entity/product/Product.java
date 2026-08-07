package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = TableNames.productTableName)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull(message = "price must not be null")
    @Min(value = 0)
    @Column(nullable = false)
    private Long price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private List<ProductDetail> productDetailList;

    @Builder
    public Product(String name, Long price) {
        CheckConfig.npeAndBlankCheck(name, "name");
        CheckConfig.npeAndNegativeCheck(price, "price");

        this.productDetailList = new ArrayList<>();
        this.name = name;
        this.price = price;
    }

    public void patchProduct(ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");

        if(productUpdateDTO.getName() != null || !productUpdateDTO.getName().isBlank()){
            this.name = productUpdateDTO.getName();
        }
        if(productUpdateDTO.getPrice() != null || productUpdateDTO.getPrice() >= 0){
            this.price = productUpdateDTO.getPrice();
        }
        if(productUpdateDTO.getProductDetailUpdateDTOList() != null){
            this.productDetailList = productUpdateDTO.getProductDetailUpdateDTOList().stream()
                    .map(ProductDetail::new).toList();
        }
    }

    public void putProduct(ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");
        CheckConfig.npeCheck(productUpdateDTO.getName(), "name");
        CheckConfig.npeCheck(productUpdateDTO.getPrice(), "price");
        CheckConfig.npeCheck(productUpdateDTO.getProductDetailUpdateDTOList(), "productDetailUpdateDTOList");

        this.name = productUpdateDTO.getName();
        this.price = productUpdateDTO.getPrice();
        this.productDetailList = productUpdateDTO.getProductDetailUpdateDTOList().stream()
                .map(ProductDetail::new).toList();
    }
}
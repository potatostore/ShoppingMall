package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = TableNames.productDetailTableName)
@NoArgsConstructor
@Getter
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productDetailId;

    @Column(nullable = false)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductDetail(String detail){
        CheckConfig.npeAndBlankCheck(detail, "detail");

        this.detail = detail;
    }

    public ProductDetail(ProductDetailUpdateDTO productDetailUpdateDTO){
        CheckConfig.npeCheck(productDetailUpdateDTO, "productDetailUpdateDTO");
        CheckConfig.npeAndBlankCheck(productDetailUpdateDTO.getDetail(), "detail");

        this.detail = productDetailUpdateDTO.getDetail();
    }
}

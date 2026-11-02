package com.shopping_mall_api.entity.product;

import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.entity.BaseEntity;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 장바구니 항목(CartItem.product) 등 지연 로딩 프록시도 IN 쿼리로 묶어서 초기화
@BatchSize(size = 100)
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

    // 재고. 주문 생성 시 ProductRepository.decreaseStock(조건부 UPDATE)으로 차감, 결제 실패·주문 삭제 시 복구
    @NotNull(message = "stock must not be null")
    @Min(value = 0)
    @Column(nullable = false)
    private Long stock;

    // 목록 조회 시 컬렉션을 엔티티마다 따로 조회하지 않고 IN 쿼리로 묶음 (N+1 → 1+1)
    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private List<ProductDetail> productDetailList;

    @Builder
    public Product(String name, Long price, Long stock, List<ProductDetailCreateDTO> productDetailCreateDTOList) {
        CheckConfig.npeAndBlankCheck(name, "name");
        CheckConfig.npeAndNegativeCheck(price, "price");
        CheckConfig.npeAndNegativeCheck(stock, "stock");
        CheckConfig.npeAndEmptyCheck(productDetailCreateDTOList, "productDetailCreateDTOList");

        this.productDetailList = new ArrayList<>();

        for(ProductDetailCreateDTO dto : productDetailCreateDTOList){
            ProductDetail productDetail = new ProductDetail(dto);

            this.productDetailList.add(productDetail);
            productDetail.assignProduct(this);
        }

        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void patchProduct(ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");

        if(productUpdateDTO.getName() != null && !productUpdateDTO.getName().isBlank()){
            this.name = productUpdateDTO.getName();
        }
        if(productUpdateDTO.getPrice() != null && productUpdateDTO.getPrice() >= 0){
            this.price = productUpdateDTO.getPrice();
        }
        if(productUpdateDTO.getStock() != null && productUpdateDTO.getStock() >= 0){
            this.stock = productUpdateDTO.getStock();
        }
        if(productUpdateDTO.getProductDetailUpdateDTOList() != null){
            replaceProductDetails(productUpdateDTO.getProductDetailUpdateDTOList());
        }
    }

    public void putProduct(ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");
        CheckConfig.npeCheck(productUpdateDTO.getName(), "name");
        CheckConfig.npeCheck(productUpdateDTO.getPrice(), "price");
        CheckConfig.npeAndNegativeCheck(productUpdateDTO.getStock(), "stock");
        CheckConfig.npeCheck(productUpdateDTO.getProductDetailUpdateDTOList(), "productDetailUpdateDTOList");

        this.name = productUpdateDTO.getName();
        this.price = productUpdateDTO.getPrice();
        this.stock = productUpdateDTO.getStock();
        replaceProductDetails(productUpdateDTO.getProductDetailUpdateDTOList());
    }

    // orphanRemoval 컬렉션은 참조를 바꾸지 않고 내용만 교체해야 함 (새 리스트 대입 시 HibernateException)
    // 새 상세에는 assignProduct로 연관관계 주인(ProductDetail.product)을 채워야 product_id FK가 저장됨
    private void replaceProductDetails(List<ProductDetailUpdateDTO> productDetailUpdateDTOList){
        this.productDetailList.clear();

        for(ProductDetailUpdateDTO dto : productDetailUpdateDTOList){
            ProductDetail productDetail = new ProductDetail(dto);

            productDetail.assignProduct(this);
            this.productDetailList.add(productDetail);
        }
    }
}
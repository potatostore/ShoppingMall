package com.shopping_mall_api.dto.product;

import com.shopping_mall_api.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

// Page 객체를 그대로 직렬화하면 내부 필드(pageable, sort 등)가 노출되고 형식이 Spring 버전에 따라 바뀌므로 필요한 값만 담음
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponseDTO {
    private List<ProductResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;

    public ProductPageResponseDTO(Page<Product> productPage){
        this.content = productPage.getContent().stream().map(ProductResponseDTO::new).toList();
        this.page = productPage.getNumber();
        this.size = productPage.getSize();
        this.totalElements = productPage.getTotalElements();
        this.totalPages = productPage.getTotalPages();
        this.hasNext = productPage.hasNext();
    }
}

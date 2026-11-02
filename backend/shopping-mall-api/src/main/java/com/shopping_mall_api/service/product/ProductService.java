package com.shopping_mall_api.service.product;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductPageResponseDTO;
import com.shopping_mall_api.dto.product.ProductSortType;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO productCreateDTO){
        CheckConfig.npeCheck(productCreateDTO, "productCreateDTO");

        Product createProduct = Product.builder()
                .name(productCreateDTO.getName())
                .price(productCreateDTO.getPrice())
                .stock(productCreateDTO.getStock())
                .productDetailCreateDTOList(productCreateDTO.getProductDetailCreateDTOList())
                .build();

        productRepository.save(createProduct);

        return new ProductResponseDTO(createProduct);
    }

    public static final int MAX_PAGE_SIZE = 100;

    public ProductPageResponseDTO getProducts(String keyword, Long minPrice, Long maxPrice,
                                              ProductSortType sortType, int page, int size){
        CheckConfig.npeCheck(sortType, "sortType");

        // 잘못된 페이지 값은 400 대신 가까운 유효 값으로 보정 (size는 1~100)
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE), sortType.getSort());

        return new ProductPageResponseDTO(productRepository.search(escapeLike(keyword), minPrice, maxPrice, pageRequest));
    }

    // LIKE 와일드카드(%, _)를 문자 그대로 검색하도록 '!'로 이스케이프. 빈 검색어는 조건 없음(null)
    private static String escapeLike(String keyword){
        if(keyword == null || keyword.isBlank()){
            return null;
        }
        return keyword.trim()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }

    public ProductResponseDTO getProduct(Long productId){
        CheckConfig.npeCheck(productId, "productId");

        return new ProductResponseDTO(productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND ,"Cannot Found Product (" + productId + ")")));
    }

    @Transactional
    public ProductResponseDTO patchProduct(Long productId, ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");

        Product patchProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Cannot Found Product (" + productId + ")"));

        patchProduct.patchProduct(productUpdateDTO);

        return new ProductResponseDTO(patchProduct);
    }

    @Transactional
    public ProductResponseDTO putProduct(Long productId, ProductUpdateDTO productUpdateDTO){
        CheckConfig.npeCheck(productId, "productId");
        CheckConfig.npeCheck(productUpdateDTO, "productUpdateDTO");

        Product putProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Cannot Found Product (" + productId + ")"));

        putProduct.putProduct(productUpdateDTO);

        return new ProductResponseDTO(putProduct);
    }

    @Transactional
    public void deleteProduct(Long productId){
        CheckConfig.npeCheck(productId, "productId");

        productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        // likes.product_id FK 때문에 찜이 있는 상품은 삭제가 실패하므로 찜을 먼저 지움
        likesRepository.deleteAllByProductId(productId);
        productRepository.deleteById(productId);
    }
}

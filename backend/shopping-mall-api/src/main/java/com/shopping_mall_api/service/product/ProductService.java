package com.shopping_mall_api.service.product;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO productCreateDTO){
        CheckConfig.npeCheck(productCreateDTO, "productCreateDTO");

        Product createProduct = Product.builder()
                .name(productCreateDTO.getName())
                .price(productCreateDTO.getPrice())
                .build();

        return new ProductResponseDTO(productRepository.save(createProduct));
    }

    public List<ProductResponseDTO> getProducts(){
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::new).toList();
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

        productRepository.deleteById(productId);
    }
}

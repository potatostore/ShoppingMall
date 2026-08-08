package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(ApiURLNames.productURL)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(ApiURLNames.createProductURL)
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(ProductCreateDTO productCreateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : product create",
                productService.createProduct(productCreateDTO)
        ));
    }

    @GetMapping(ApiURLNames.findProductsURL)
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProducts(){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get all products",
                productService.getProducts()
        ));
    }

    @GetMapping(ApiURLNames.findProductURL)
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProduct(@PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get product (" + productId + ")",
                productService.getProduct(productId)
        ));
    }

    @PatchMapping(ApiURLNames.updateProductURL)
    public ResponseEntity<ApiResponse<ProductResponseDTO>> patchProduct(
            @PathVariable Long productId, @Valid @RequestBody ProductUpdateDTO productUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch product (" + productId + ")",
                productService.patchProduct(productId, productUpdateDTO)
        ));
    }

    @DeleteMapping(ApiURLNames.deleteProductURL)
    public void deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
    }
}
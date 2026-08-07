package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.service.product.ProductService;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.productTableName)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(ProductCreateDTO productCreateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : product create",
                productService.createProduct(productCreateDTO)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProducts(){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get all products",
                productService.getProducts()
        ));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProduct(@PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get product (" + productId + ")",
                productService.getProduct(productId)
        ));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> patchProduct(
            @PathVariable Long productId, @Valid @RequestBody ProductUpdateDTO productUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch product (" + productId + ")",
                productService.patchProduct(productId, productUpdateDTO)
        ));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> putProduct(
            @PathVariable Long productId, @Valid @RequestBody ProductUpdateDTO productUpdateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : put product (" + productId + ")",
                productService.putProduct(productId, productUpdateDTO)
        ));
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
    }
}
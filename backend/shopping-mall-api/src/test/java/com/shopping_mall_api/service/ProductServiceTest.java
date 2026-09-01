package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock private ProductRepository productRepository;

    @InjectMocks private ProductService productService;

    @Test
    void createProduct_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
            "Test Product", 20260828L, List.of(productDetailCreateDTO)
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO result = productService.createProduct(productCreateDTO);

        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(20260828L);
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test");
    }

    @Test
    void getProducts_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                        .name("Test Product")
                        .price(20260828L)
                        .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                        .build();

        when(productRepository.findAll()).thenReturn(List.of(existProduct));

        List<ProductResponseDTO> resultList = productService.getProducts();
        ProductResponseDTO result = resultList.getFirst();

        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(20260828L);
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test");
    }

    @Test
    void getProduct_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));

        ProductResponseDTO result = productService.getProduct(1L);

        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(20260828L);
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test");
    }

    @Test
    void getProduct_productNotFoundExceptionTest(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProduct(999L));
    }

    @Test
    void patchProduct_successTest(){
        ProductDetailUpdateDTO productDetailUpdateDTO = new ProductDetailUpdateDTO(
                "Test Detail Update"
        );

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO)
        );

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));

        ProductResponseDTO result = productService.patchProduct(1L, productUpdateDTO);

        assertThat(result.getPrice()).isEqualTo(2026082801L);
        assertThat(result.getName()).isEqualTo("Test Product Update");
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test Detail Update");
    }

    @Test
    void patchProduct_productNotFoundExceptionTest(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ProductDetailUpdateDTO productDetailUpdateDTO = new ProductDetailUpdateDTO(
                "Test Detail Update"
        );

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO)
        );

        assertThrows(NotFoundException.class, () -> productService.patchProduct(999L, productUpdateDTO));
    }

    @Test
    void putProduct_successTest(){
        ProductDetailUpdateDTO productDetailUpdateDTO = new ProductDetailUpdateDTO(
                "Test Detail Update"
        );

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO)
        );

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));

        ProductResponseDTO result = productService.putProduct(1L, productUpdateDTO);

        assertThat(result.getPrice()).isEqualTo(2026082801L);
        assertThat(result.getName()).isEqualTo("Test Product Update");
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test Detail Update");
    }

    @Test
    void putProduct_productNotFoundExceptionTest(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ProductDetailUpdateDTO productDetailUpdateDTO = new ProductDetailUpdateDTO(
                "Test Detail Update"
        );

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO)
        );

        assertThrows(NotFoundException.class, () -> productService.putProduct(999L, productUpdateDTO));
    }

    @Test
    void deleteProduct_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_productNotFoundExceptionTest(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
    }

}

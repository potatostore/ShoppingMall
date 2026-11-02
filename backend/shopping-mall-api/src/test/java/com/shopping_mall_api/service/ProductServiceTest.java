package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.product.ProductCreateDTO;
import com.shopping_mall_api.dto.product.ProductResponseDTO;
import com.shopping_mall_api.dto.product.ProductSortType;
import com.shopping_mall_api.dto.product.ProductPageResponseDTO;
import com.shopping_mall_api.dto.product.ProductUpdateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailUpdateDTO;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
    @Mock private LikesRepository likesRepository;

    @InjectMocks private ProductService productService;

    @Test
    void createProduct_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        ProductCreateDTO productCreateDTO = new ProductCreateDTO(
            "Test Product", 20260828L, List.of(productDetailCreateDTO), 100L
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
                        .stock(100L)
                        .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                        .build();

        when(productRepository.search(eq(null), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(existProduct), PageRequest.of(0, 20), 1));

        ProductPageResponseDTO page = productService.getProducts(null, null, null, ProductSortType.LATEST, 0, 20);
        ProductResponseDTO result = page.getContent().getFirst();

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(20260828L);
        assertThat(result.getProductDetailResponseDTOList().getFirst().getDetail()).isEqualTo("Test");
    }

    @Test
    void getProducts_clampsPagingAndEscapesKeywordTest(){
        when(productRepository.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 100), 0));

        productService.getProducts(" 50%_off! ", 100L, null, ProductSortType.PRICE_DESC, -3, 1000);

        ArgumentCaptor<String> keyword = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).search(keyword.capture(), eq(100L), eq(null), pageable.capture());

        // LIKE 와일드카드는 '!'로 이스케이프, 앞뒤 공백 제거
        assertThat(keyword.getValue()).isEqualTo("50!%!_off!!");
        // 음수 페이지는 0, size는 최대 100으로 보정
        assertThat(pageable.getValue().getPageNumber()).isZero();
        assertThat(pageable.getValue().getPageSize()).isEqualTo(100);
        assertThat(pageable.getValue().getSort()).isEqualTo(ProductSortType.PRICE_DESC.getSort());
    }

    @Test
    void getProducts_blankKeywordIsIgnoredTest(){
        when(productRepository.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        productService.getProducts("   ", null, null, ProductSortType.NAME, 0, 20);

        verify(productRepository).search(eq(null), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    void getProduct_successTest(){
        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .stock(100L)
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
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO), 50L
        );

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .stock(100L)
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
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO), 50L
        );

        assertThrows(NotFoundException.class, () -> productService.patchProduct(999L, productUpdateDTO));
    }

    @Test
    void putProduct_successTest(){
        ProductDetailUpdateDTO productDetailUpdateDTO = new ProductDetailUpdateDTO(
                "Test Detail Update"
        );

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO(
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO), 50L
        );

        ProductDetailCreateDTO productDetailCreateDTO = new ProductDetailCreateDTO(
                "Test"
        );

        Product existProduct = Product.builder()
                .name("Test Product")
                .price(20260828L)
                .stock(100L)
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
                "Test Product Update", 2026082801L, List.of(productDetailUpdateDTO), 50L
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
                .stock(100L)
                .productDetailCreateDTOList(List.of(productDetailCreateDTO))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existProduct));

        productService.deleteProduct(1L);

        // 찜(likes.product_id FK)을 먼저 지운 뒤 상품 삭제
        InOrder inOrder = inOrder(likesRepository, productRepository);
        inOrder.verify(likesRepository).deleteAllByProductId(1L);
        inOrder.verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_productNotFoundExceptionTest(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));
    }

}

package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.likes.LikeStatusResponseDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.likes.Likes;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.likes.LikesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikesServiceTest {
    @Mock private LikesRepository likesRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private LikesService likesService;

    private Product product(){
        return Product.builder().name("감자").price(1000L).stock(10L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("detail"))).build();
    }

    private User user(){
        return User.builder().email("a@gmail.com").logInPassword("pw").name("김민준").role("USER")
                .phoneNumber("01011111111").birthday(LocalDate.of(2026, 8, 27)).build();
    }

    @Test
    void like_savesWhenNotLiked(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product()));
        when(likesRepository.existsByUser_UserIdAndProduct_ProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(likesRepository.countByProduct_ProductId(1L)).thenReturn(1L);

        LikeStatusResponseDTO result = likesService.like(1L, 1L);

        verify(likesRepository).saveAndFlush(any(Likes.class));
        assertThat(result).isEqualTo(new LikeStatusResponseDTO(1L, true, 1L));
    }

    @Test
    void like_alreadyLiked_isIdempotent(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product()));
        when(likesRepository.existsByUser_UserIdAndProduct_ProductId(1L, 1L)).thenReturn(true);
        when(likesRepository.countByProduct_ProductId(1L)).thenReturn(3L);

        LikeStatusResponseDTO result = likesService.like(1L, 1L);

        verify(likesRepository, never()).saveAndFlush(any());
        assertThat(result.liked()).isTrue();
        assertThat(result.likeCount()).isEqualTo(3L);
    }

    @Test
    void like_concurrentDuplicate_treatedAsLiked(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product()));
        when(likesRepository.existsByUser_UserIdAndProduct_ProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
        when(likesRepository.saveAndFlush(any(Likes.class))).thenThrow(new DataIntegrityViolationException("uk_likes_user_product"));
        when(likesRepository.countByProduct_ProductId(1L)).thenReturn(1L);

        LikeStatusResponseDTO result = likesService.like(1L, 1L);

        assertThat(result.liked()).isTrue();
    }

    @Test
    void like_unknownProduct_404(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> likesService.like(1L, 999L));
        verifyNoInteractions(likesRepository);
    }

    @Test
    void unlike_deletesAndReturnsCount(){
        when(productRepository.existsById(1L)).thenReturn(true);
        when(likesRepository.countByProduct_ProductId(1L)).thenReturn(0L);

        LikeStatusResponseDTO result = likesService.unlike(1L, 1L);

        verify(likesRepository).deleteByUserIdAndProductId(1L, 1L);
        assertThat(result).isEqualTo(new LikeStatusResponseDTO(1L, false, 0L));
    }

    @Test
    void getStatus_unknownProduct_404(){
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> likesService.getStatus(1L, 999L));
    }
}

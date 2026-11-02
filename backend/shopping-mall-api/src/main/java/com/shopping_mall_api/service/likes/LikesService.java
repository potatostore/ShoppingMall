package com.shopping_mall_api.service.likes;

import com.shopping_mall_api.dto.likes.LikeStatusResponseDTO;
import com.shopping_mall_api.dto.likes.LikedProductResponseDTO;
import com.shopping_mall_api.entity.likes.Likes;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.product.ProductRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 이미 찜한 상품이면 그대로 성공 (멱등). 동시에 두 번 요청돼도 unique 제약으로 한 행만 남음
    @Transactional
    public LikeStatusResponseDTO like(Long userId, Long productId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(productId, "productId");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if(!likesRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId)){
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
            try {
                likesRepository.saveAndFlush(new Likes(user, product));
            } catch (DataIntegrityViolationException e) {
                // 확인과 저장 사이에 같은 찜이 먼저 저장됨 → 이미 찜한 상태와 같으므로 성공으로 처리
            }
        }

        return new LikeStatusResponseDTO(productId, true, likesRepository.countByProduct_ProductId(productId));
    }

    // 찜하지 않은 상품을 취소해도 성공 (멱등)
    @Transactional
    public LikeStatusResponseDTO unlike(Long userId, Long productId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(productId, "productId");

        if(!productRepository.existsById(productId)){
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        likesRepository.deleteByUserIdAndProductId(userId, productId);

        return new LikeStatusResponseDTO(productId, false, likesRepository.countByProduct_ProductId(productId));
    }

    public LikeStatusResponseDTO getStatus(Long userId, Long productId){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(productId, "productId");

        if(!productRepository.existsById(productId)){
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return new LikeStatusResponseDTO(
                productId,
                likesRepository.existsByUser_UserIdAndProduct_ProductId(userId, productId),
                likesRepository.countByProduct_ProductId(productId));
    }

    public List<LikedProductResponseDTO> getMyLikes(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        return likesRepository.findByUser_UserIdOrderByIdDesc(userId).stream()
                .map(LikedProductResponseDTO::new)
                .toList();
    }
}

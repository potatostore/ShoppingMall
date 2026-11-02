package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.likes.LikeStatusResponseDTO;
import com.shopping_mall_api.dto.likes.LikedProductResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.likes.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @PostMapping(ApiURLNames.productLikesURL)
    public ResponseEntity<ApiResponse<LikeStatusResponseDTO>> like(
            @AuthenticationPrincipal Long userId, @PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : like product (" + productId + ")",
                likesService.like(userId, productId)
        ));
    }

    @DeleteMapping(ApiURLNames.productLikesURL)
    public ResponseEntity<ApiResponse<LikeStatusResponseDTO>> unlike(
            @AuthenticationPrincipal Long userId, @PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : unlike product (" + productId + ")",
                likesService.unlike(userId, productId)
        ));
    }

    @GetMapping(ApiURLNames.myProductLikeURL)
    public ResponseEntity<ApiResponse<LikeStatusResponseDTO>> getStatus(
            @AuthenticationPrincipal Long userId, @PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get like status (" + productId + ")",
                likesService.getStatus(userId, productId)
        ));
    }

    @GetMapping(ApiURLNames.myLikesURL)
    public ResponseEntity<ApiResponse<List<LikedProductResponseDTO>>> getMyLikes(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get my likes",
                likesService.getMyLikes(userId)
        ));
    }
}

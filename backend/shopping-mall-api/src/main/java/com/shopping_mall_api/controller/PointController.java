package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.point.PointBalanceResponseDTO;
import com.shopping_mall_api.dto.point.PointHistoryResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

// [뼈대] 구현 시: @RestController, @RequiredArgsConstructor, @CrossOrigin, 로그인 필요
public class PointController {

    // GET /users/me/points
    public ResponseEntity<ApiResponse<PointBalanceResponseDTO>> getBalance(Long userId){
        throw new UnsupportedOperationException("TODO");
    }

    // GET /users/me/points/histories?page=&size=
    public ResponseEntity<ApiResponse<List<PointHistoryResponseDTO>>> getHistories(Long userId, int page, int size){
        throw new UnsupportedOperationException("TODO");
    }
}

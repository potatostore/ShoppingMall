package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(ApiURLNames.userURL)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(ApiURLNames.createUserURL)
    public ResponseEntity<ApiResponse<UserCreateResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO userCreateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : create user & cart",
                userService.createUser(userCreateDTO)
        ));
    }

    // user정보 조회 시 관리자 권한만 조회 가능하도록 기능 추가
    @GetMapping(ApiURLNames.findUsersURL)
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers(){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get all users",
                userService.getUsers()
        ));
    }

    // "
    @GetMapping(ApiURLNames.findUserURL)
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get user (" + userId + ")",
                userService.getUser(userId)
        ));
    }

    @PatchMapping(ApiURLNames.updateUserURL)
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(
            @PathVariable Long userId, @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch user info (" + userId + ")",
                userService.patchUserInfo(userId, userUpdateDTO)
        ));
    }

    // 관리자 레벨의 실행 권한 부여 기능 추가
    @DeleteMapping(ApiURLNames.deleteUserURL)
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }
}

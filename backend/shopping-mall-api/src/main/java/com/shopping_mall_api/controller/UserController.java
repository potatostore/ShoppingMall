package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.service.UserService;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.userTableName)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserCreateResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO userCreateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : create User & Cart",
                userService.createUser(userCreateDTO)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers(){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : Get All Users",
                userService.getUsers()
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get user (" + userId + ")",
                userService.getUser(userId)
        ));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(
            @PathVariable Long userId, @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch user info (" + userId + ")",
                userService.patchUserInfo(userId, userUpdateDTO)
        ));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> putUser(
            @PathVariable Long userId, @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : put user info (" + userId + ")",
                userService.putUserInfo(userId, userUpdateDTO)
        ));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : delete user info (" + userId + ")",
                userService.deleteUser(userId)
        ));
    }
}

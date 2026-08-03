package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.service.UserService;
import com.shopping_mall_api.global.constant.TableNames;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.userTableName)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserController {
    private final UserService userService;

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserCreateDTO userCreateDTO){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : Create User",
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
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@Valid @PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : get user (" + userId + ")",
                userService.getUser(userId)
        ));
    }

    @PatchMapping("/{userId}")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDTO>> patchUser(
            @Valid @PathVariable Long userId, @RequestBody UserUpdateDTO userUpdateDTO
    ){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : patch user info (" + userId + ")",
                userService.patchUserInfo(userId, userUpdateDTO)
        ));
    }

    @PutMapping("/{userId}")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDTO>> putUser(
            @Valid @PathVariable Long userId, @Valid @RequestBody UserUpdateDTO userUpdateDTO
    ){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : put user info (" + userId + ")",
                userService.putUser(userId, userUpdateDTO)
        ));
    }

    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteUser(@Valid @PathVariable Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Success : delete user info (" + userId + ")",
                userService.deleteUser(userId)
        ));
    }
}

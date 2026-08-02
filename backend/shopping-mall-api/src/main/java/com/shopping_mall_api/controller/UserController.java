package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.service.UserService;
import com.shopping_mall_api.global.constant.TableNames;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/" + TableNames.userTableName)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Value UserCreateDTO userCreateDTO){
        return
    }
}

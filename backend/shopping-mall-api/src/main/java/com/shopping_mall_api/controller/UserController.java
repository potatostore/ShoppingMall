package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.FindIdData;
import com.shopping_mall_api.dto.FindPasswordData;
import com.shopping_mall_api.dto.SignInData;
import com.shopping_mall_api.dto.SignUpData;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.UserService;
import com.shopping_mall_api.status.SignInStatus;
import com.shopping_mall_api.status.SignUpStatus;
import com.shopping_mall_api.global.constant.TableNames;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Value UserCreateDTO userCreateDTO){
        return
    }
}

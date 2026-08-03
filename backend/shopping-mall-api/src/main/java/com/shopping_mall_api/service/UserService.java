package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.cart.CartRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createCart(UserCreateDTO userCreateDTO){
        Objects.requireNonNull(userCreateDTO, "userCreateDTO must not be null");


    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new).toList();
    }

    public UserResponseDTO getUser(Long userId){
        Objects.requireNonNull(userId, "userId must not be null");

        return userRepository.findById(userId)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));
    }

    public UserResponseDTO patchUserInfo(Long userId, UserUpdateDTO userUpdateDTO){
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(userUpdateDTO, "userUpdateDTO must not be null");

        userRepository.
    }

    public UserResponseDTO putUserInfo(Long userId, UserUpdateDTO userUpdateDTO){
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(userUpdateDTO, "userUpdateDTO must not be null");


    }

    public void deleteUser(Long userId){


        userRepository.deleteById(userId);
    }
}

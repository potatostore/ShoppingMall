package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreateResponseDTO createUser(@Valid UserCreateDTO userCreateDTO){
        CheckConfig.npeCheck(userCreateDTO, "userCreateDTO");
        
        User createUser = User.builder()
                .email(userCreateDTO.getEmail())
                .logInPassword(passwordEncoder.encode(userCreateDTO.getLogInPassword()))
                .name(userCreateDTO.getName())
                .phoneNumber(userCreateDTO.getPhoneNumber())
                .birthday(userCreateDTO.getBirthday())
                .build();

        UserResponseDTO userResponseDTO = new UserResponseDTO(userRepository.save(createUser));

        CartResponseDTO cartResponseDTO = cartService.createCart(userResponseDTO.getUserId());
        
        return new UserCreateResponseDTO(
                userResponseDTO,
                cartResponseDTO
        );
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new).toList();
    }

    public UserResponseDTO getUser(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        return userRepository.findById(userId)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));
    }

    @Transactional
    public UserResponseDTO patchUserInfo(Long userId, UserUpdateDTO userUpdateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(userUpdateDTO, "userUpdateDTO");

        String encodedLogInPassword = null;
        if(userUpdateDTO.getLogInPassword() != null){
            encodedLogInPassword = passwordEncoder.encode(userUpdateDTO.getLogInPassword());
        }

        User patchUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        patchUser.patchUser(userUpdateDTO, encodedLogInPassword);

        return new UserResponseDTO(patchUser);
    }

    @Transactional
    public UserResponseDTO putUserInfo(Long userId, UserUpdateDTO userUpdateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(userUpdateDTO, "userUpdateDTO");
        CheckConfig.npeCheck(userUpdateDTO.getLogInPassword(), "userUpdateDTO-logInPassword must not be null");

        String encodedLogInPassword = passwordEncoder.encode(userUpdateDTO.getLogInPassword());

        User putUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        putUser.putUser(userUpdateDTO, encodedLogInPassword);

        return new UserResponseDTO(putUser);
    }

    @Transactional
    public UserResponseDTO deleteUser(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User  (" + userId + ")"));

        userRepository.deleteById(userId);

        return new UserResponseDTO(deleteUser);
    }
}

package com.shopping_mall_api.service.user;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.constant.RoleNames;
import com.shopping_mall_api.global.exception.AlreadyExistException;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        checkDuplicateEmail(userCreateDTO.getSignUpEmail());

        User createUser = User.builder()
                .email(userCreateDTO.getSignUpEmail())
                .logInPassword(passwordEncoder.encode(userCreateDTO.getSignUpPassword()))
                .name(userCreateDTO.getSignUpName())
                .role(RoleNames.USER)
                .phoneNumber(userCreateDTO.getSignUpPhoneNumber())
                .birthday(userCreateDTO.getSignUpBirthday())
                .build();

        UserResponseDTO userResponseDTO;
        try {
            userResponseDTO = new UserResponseDTO(userRepository.save(createUser));
        } catch (DataIntegrityViolationException e) {
            // existsByEmail 확인과 insert 사이에 같은 이메일 가입이 끼어든 경우 (users.email unique 제약)
            throw new AlreadyExistException(ErrorCode.USER_ALREADY_EXIST);
        }

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

        User patchUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        checkEmailChangeable(patchUser, userUpdateDTO.getEmail());

        patchUser.patchUser(userUpdateDTO);

        return new UserResponseDTO(patchUser);
    }

    @Transactional
    public UserResponseDTO putUserInfo(Long userId, UserUpdateDTO userUpdateDTO){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeCheck(userUpdateDTO, "userUpdateDTO");

        User putUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User (" + userId + ")"));

        checkEmailChangeable(putUser, userUpdateDTO.getEmail());

        putUser.putUser(userUpdateDTO);

        return new UserResponseDTO(putUser);
    }

    @Transactional
    public void deleteUser(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User  (" + userId + ")"));

        userRepository.deleteById(userId);
    }

    private void checkDuplicateEmail(String email){
        if(userRepository.existsByEmail(email)){
            throw new AlreadyExistException(ErrorCode.USER_ALREADY_EXIST);
        }
    }

    private void checkEmailChangeable(User user, String newEmail){
        if(newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())){
            checkDuplicateEmail(newEmail);
        }
    }
}

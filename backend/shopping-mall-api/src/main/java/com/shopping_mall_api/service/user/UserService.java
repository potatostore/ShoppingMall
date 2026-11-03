package com.shopping_mall_api.service.user;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.user.PasswordChangeDTO;
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
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;
    private final LikesRepository likesRepository;

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

    // 아이디(이메일) 찾기: 이름 + 전화번호가 일치하는 계정의 이메일을 마스킹해서 반환 (전체 이메일 노출 방지)
    public String findMaskedEmail(String name, String phoneNumber){
        CheckConfig.npeAndBlankCheck(name, "name");
        CheckConfig.npeAndBlankCheck(phoneNumber, "phoneNumber");

        List<User> userList = userRepository.findByNameAndPhoneNumber(name, phoneNumber);

        if(userList.isEmpty()){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        return userList.stream()
                .map(User::getEmail)
                .map(UserService::maskEmail)
                .collect(Collectors.joining(", "));
    }

    // qwer1234@gmail.com → qw******@gmail.com (로컬 파트 앞 2글자만 노출, 2글자 이하면 1글자)
    private static String maskEmail(String email){
        int at = email.indexOf('@');
        if(at <= 0){
            return "***";
        }

        String local = email.substring(0, at);
        int visible = local.length() <= 2 ? 1 : 2;

        return local.substring(0, visible) + "*".repeat(Math.max(3, local.length() - visible)) + email.substring(at);
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

    // [뼈대] 비밀번호 변경 (로그인 상태)
    // 구현 시: @Transactional, 현재 비밀번호 BCrypt 확인(불일치 401), 새 비밀번호 encode 후 저장(User에 changePassword 메서드 추가)
    //         변경 후 refresh token 삭제(RefreshTokenRepository.delete) → 다른 기기 로그인 무효화
    //         컨트롤러: PATCH /users/me/password, SecurityConfig 로그인 필요
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO){
        throw new UnsupportedOperationException("TODO");
    }

    @Transactional
    public void deleteUser(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND, "Cannot Found User  (" + userId + ")"));

        // likes.user_id FK 때문에 찜이 있는 회원은 탈퇴가 실패하므로 찜을 먼저 지움 (주문 FK 문제는 탈퇴 정책 결정 후 처리)
        likesRepository.deleteAllByUserId(userId);
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

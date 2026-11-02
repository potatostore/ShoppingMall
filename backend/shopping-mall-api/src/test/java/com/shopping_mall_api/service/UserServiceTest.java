package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.AlreadyExistException;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.repository.likes.LikesRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.cart.CartService;
import com.shopping_mall_api.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock private CartService cartService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private LikesRepository likesRepository;

    @InjectMocks private UserService userService;

    @Test
    void userCreateTest(){
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .signUpName("김민준")
                .signUpEmail("qwer1234@gmail.com")
                .signUpPassword("qwer1234")
                .signUpPhoneNumber("01011111111")
                .signUpBirthday(LocalDate.of(2026, 8, 27))
                .build();

        when(passwordEncoder.encode("qwer1234")).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(cartService.createCart(any()))
                .thenReturn(CartResponseDTO.builder()
                                .userId(1L)
                                .cartItemList(List.of())
                                .totalCartPrice(0L)
                                .build());

        UserCreateResponseDTO result = userService.createUser(userCreateDTO);

        assertThat(result.userResponseDTO().getName()).isEqualTo("김민준");
        assertThat(result.userResponseDTO().getEmail()).isEqualTo("qwer1234@gmail.com");
        assertThat(result.cartResponseDTO()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLogInPassword()).isEqualTo("encodedPassword");
        assertThat(userCaptor.getValue().getRole()).isEqualTo("USER");

        verify(cartService).createCart(any());
    }

    @Test
    void userCreateDuplicateEmailTest(){
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .signUpName("김민준")
                .signUpEmail("qwer1234@gmail.com")
                .signUpPassword("qwer1234")
                .signUpPhoneNumber("01011111111")
                .signUpBirthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.existsByEmail("qwer1234@gmail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> userService.createUser(userCreateDTO));

        verify(userRepository, never()).save(any(User.class));
        verify(cartService, never()).createCart(any());
    }

    @Test
    void patchUserDuplicateEmailTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .email("other@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));
        when(userRepository.existsByEmail("other@gmail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> userService.patchUserInfo(1L, updateUserDTO));
        assertThat(existUser.getEmail()).isEqualTo("qwer1234@gmail.com");
    }

    @Test
    void findMaskedEmailTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findByNameAndPhoneNumber("김민준", "01011111111")).thenReturn(List.of(existUser));

        assertThat(userService.findMaskedEmail("김민준", "01011111111")).isEqualTo("qw******@gmail.com");
    }

    @Test
    void findMaskedEmailNotFoundTest(){
        when(userRepository.findByNameAndPhoneNumber("김민준", "01099999999")).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> userService.findMaskedEmail("김민준", "01099999999"));
    }

    @Test
    void findMaskedEmail_multipleAccountsAndShortLocalPartTest(){
        User shortUser = User.builder()
                .email("ab@gmail.com").logInPassword("pw").name("김민준").role("USER")
                .phoneNumber("01011111111").birthday(LocalDate.of(2026, 8, 27)).build();
        User threeUser = User.builder()
                .email("abc@naver.com").logInPassword("pw").name("김민준").role("USER")
                .phoneNumber("01011111111").birthday(LocalDate.of(2026, 8, 27)).build();

        when(userRepository.findByNameAndPhoneNumber("김민준", "01011111111")).thenReturn(List.of(shortUser, threeUser));

        // 로컬 파트가 2글자 이하면 1글자만 노출, 별표는 최소 3개 (길이 추측 방지)
        assertThat(userService.findMaskedEmail("김민준", "01011111111"))
                .isEqualTo("a***@gmail.com, ab***@naver.com");
    }

    @Test
    void getUserTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        UserResponseDTO result = userService.getUser(1L);

        assertThat(result.getName()).isEqualTo("김민준");
        assertThat(result.getEmail()).isEqualTo("qwer1234@gmail.com");
    }

    @Test
    void getUserNotFountExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void patchUserTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@gmail.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        UserResponseDTO result = userService.patchUserInfo(1L, updateUserDTO);

        assertThat(result.getName()).isEqualTo("이민준");
        assertThat(result.getPhoneNumber()).isEqualTo("01022222222");
    }

    @Test
    void patchUserBlankValueIgnoredTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name(" ")
                .email("")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        UserResponseDTO result = userService.patchUserInfo(1L, updateUserDTO);

        assertThat(result.getName()).isEqualTo("김민준");
        assertThat(result.getEmail()).isEqualTo("qwer1234@gmail.com");
    }

    @Test
    void patchUserNotFoundExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@gmail.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        assertThrows(NotFoundException.class, () -> userService.patchUserInfo(999L, updateUserDTO));
    }

    @Test
    void putUserTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@gmail.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        UserResponseDTO result = userService.putUserInfo(1L, updateUserDTO);

        assertThat(result.getName()).isEqualTo("이민준");
        assertThat(result.getPhoneNumber()).isEqualTo("01022222222");
    }

    @Test
    void putUserNotFountTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@gmail.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        assertThrows(NotFoundException.class, () -> userService.putUserInfo(999L, updateUserDTO));
    }

    @Test
    void deleteUserTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        userService.deleteUser(1L);

        verify(likesRepository).deleteAllByUserId(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserNotFoundExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}

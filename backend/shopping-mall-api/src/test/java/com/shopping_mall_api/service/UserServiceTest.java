package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.NotFoundException;
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

    @InjectMocks private UserService userService;

    // when create user, cart must create together
    @Test
    void userCreateTest(){
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .signUpName("김민준")
                .signUpEmail("qwer1234@google.com")
                .signUpPassword("qwer1234")
                .signUpRole("USER")
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
        assertThat(result.userResponseDTO().getEmail()).isEqualTo("qwer1234@google.com");
        assertThat(result.cartResponseDTO()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLogInPassword()).isEqualTo("encodedPassword");

        verify(cartService).createCart(any());
    }

    @Test
    void getUserTest(){
        User existingUser = User.builder()
                .email("qwer1234@google.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponseDTO result = userService.getUser(1L);

        assertThat(result.getName()).isEqualTo("김민준");
        assertThat(result.getEmail()).isEqualTo("qwer1234@google.com");
    }

    @Test
    void getUserNotFountExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void patchUserTest(){
        User existUser = User.builder()
                .email("qwer1234@google.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@google.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        UserResponseDTO result = userService.patchUserInfo(1L, updateUserDTO);

        assertThat(result.getName()).isEqualTo("이민준");
        assertThat(result.getPhoneNumber()).isEqualTo("01022222222");
    }

    @Test
    void patchUserNotFoundExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@google.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        assertThrows(NotFoundException.class, () -> userService.patchUserInfo(999L, updateUserDTO));
    }

    @Test
    void putUserTest(){
        User existUser = User.builder()
                .email("qwer1234@google.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        UserUpdateDTO updateUserDTO = UserUpdateDTO.builder()
                .name("이민준")
                .email("qwer1234@google.com")
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
                .email("qwer1234@google.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        assertThrows(NotFoundException.class, () -> userService.putUserInfo(999L, updateUserDTO));
    }

    @Test
    void deleteUserTest(){
        User existUser = User.builder()
                .email("qwer1234@google.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existUser));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserNotFoundExceptionTest(){
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}

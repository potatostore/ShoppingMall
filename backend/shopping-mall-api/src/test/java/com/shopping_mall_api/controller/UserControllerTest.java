package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.cart.CartResponseDTO;
import com.shopping_mall_api.dto.cart.cartItem.CartItemResponseDTO;
import com.shopping_mall_api.dto.user.UserCreateDTO;
import com.shopping_mall_api.dto.user.UserCreateResponseDTO;
import com.shopping_mall_api.dto.user.UserResponseDTO;
import com.shopping_mall_api.dto.user.UserUpdateDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;

    @Test
    void createUser_successTest() throws Exception{
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .signUpName("김민준")
                .signUpEmail("1111@gmail.com")
                .signUpPassword("qwer1234")
                .signUpRole("USER")
                .signUpPhoneNumber("01011111111")
                .signUpBirthday(LocalDate.of(2026,9,7))
                .build();;

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .userId(1L)
                .name("김민준")
                .email("1111@gmail.com")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026,9,7))
                .build();

        CartResponseDTO cartResponseDTO = CartResponseDTO.builder()
                .userId(1L)
                .cartItemList(List.of(
                        new CartItemResponseDTO(1L, 1L, 1L)
                ))
                .totalCartPrice(20260907L)
                .build();

        UserCreateResponseDTO userCreateResponseDTO = new UserCreateResponseDTO(
                userResponseDTO,
                cartResponseDTO
        );

        when(userService.createUser(any(UserCreateDTO.class)))
                .thenReturn(userCreateResponseDTO);

        mockMvc.perform(post(ApiURLNames.userURL + ApiURLNames.createUserURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : create user & cart"))
                .andExpect(jsonPath("$.data.userResponseDTO.name").value("김민준"))
                .andExpect(jsonPath("$.data.userResponseDTO.email").value("1111@gmail.com"))
                .andExpect(jsonPath("$.data.userResponseDTO.phoneNumber").value("01011111111"))
                .andExpect(jsonPath("$.data.userResponseDTO.birthday").value("2026-09-07"))
                .andExpect(jsonPath("$.data.cartResponseDTO.totalCartPrice").value(20260907L));
    }

    @Test
    void createUser_inValidTest(){
        String body = ""
    }

    @Test
    void getUsers_successTest() throws Exception{
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .userId(1L)
                .name("김민준")
                .email("1111@gmail.com")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026,9,7))
                .build();

        when(userService.getUsers()).thenReturn(List.of(userResponseDTO));

        mockMvc.perform(get(ApiURLNames.userURL + ApiURLNames.findUsersURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : get all users"))
                .andExpect(jsonPath("$.data[0].name").value("김민준"))
                .andExpect(jsonPath("$.data[0].email").value("1111@gmail.com"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("01011111111"))
                .andExpect(jsonPath("$.data[0].birthday").value("2026-09-07"));
    }

    @Test
    void getUser_successTest() throws Exception{
        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .name("김민준")
                .email("1111@gmail.com")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026,9,7))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        when(userService.getUser(any(Long.class))).thenReturn(userResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            mockMvc.perform(get(ApiURLNames.userURL + ApiURLNames.findUserURL))
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.message").value("Success : get user (1)"))
                    .andExpect(jsonPath("$.data.name").value("김민준"))
                    .andExpect(jsonPath("$.data.email").value("1111@gmail.com"))
                    .andExpect(jsonPath("$.data.phoneNumber").value("01011111111"))
                    .andExpect(jsonPath("$.data.birthday").value("2026-09-07"));
        } finally{
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void patchUser_successTest() throws Exception {
        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .userId(userId)
                .name("김민준")
                .email("1234@gmail.com")
                .role("USER")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026, 9, 7))
                .build();

        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .name("김민준")
                .email("1234@gmail.com")
                .phoneNumber("01022222222")
                .birthday(LocalDate.of(2026,9,7))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        when(userService.patchUserInfo(eq(1L), any(UserUpdateDTO.class))).thenReturn(userResponseDTO);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(patch(ApiURLNames.userURL + ApiURLNames.updateUserURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Success : patch user info (1)"))
                .andExpect(jsonPath("$.data.name").value("김민준"))
                .andExpect(jsonPath("$.data.email").value("1234@gmail.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("01022222222"))
                .andExpect(jsonPath("$.data.birthday").value("2026-09-07"));
    }

    @Test
    void deleteUser_successTest() throws Exception {
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            mockMvc.perform(delete(ApiURLNames.userURL + ApiURLNames.deleteUserURL).with(authentication(auth)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            verify(userService).deleteUser(userId);
        } finally{
            SecurityContextHolder.clearContext();
        }
    }
}

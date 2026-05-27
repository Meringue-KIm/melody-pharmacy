package com.melodypharmacy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melodypharmacy.dto.LoginRequest;
import com.melodypharmacy.dto.SignupRequest;
import com.melodypharmacy.dto.TokenResponse;
import com.melodypharmacy.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.melodypharmacy.config.SecurityConfig;
import com.melodypharmacy.security.JwtTokenProvider;
import com.melodypharmacy.security.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthService authService;
    @MockBean JwtTokenProvider jwtTokenProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/auth/signup - 회원가입 성공 200")
    void signup_success() throws Exception {
        SignupRequest request = new SignupRequest("test@test.com", "password123", "테스터");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/signup - 중복 이메일 400")
    void signup_duplicateEmail() throws Exception {
        SignupRequest request = new SignupRequest("dup@test.com", "password123", "중복");
        doThrow(new IllegalArgumentException("이미 사용 중인 이메일입니다."))
                .when(authService).signup(any());

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - 로그인 성공 토큰 반환")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        given(authService.login(any())).willReturn(new TokenResponse("jwt-token", "테스터"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.nickname").value("테스터"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 잘못된 정보 400")
    void login_fail() throws Exception {
        LoginRequest request = new LoginRequest("wrong@test.com", "wrongPw");
        given(authService.login(any()))
                .willThrow(new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

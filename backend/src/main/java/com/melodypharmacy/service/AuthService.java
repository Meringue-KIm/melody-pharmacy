package com.melodypharmacy.service;

import com.melodypharmacy.dto.ChangePasswordRequest;
import com.melodypharmacy.dto.LoginRequest;
import com.melodypharmacy.dto.SignupRequest;
import com.melodypharmacy.dto.TokenResponse;
import com.melodypharmacy.entity.User;
import com.melodypharmacy.repository.UserRepository;
import com.melodypharmacy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 합니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build());
    }

    @Transactional
    public void updateNickname(String email, String nickname) {
        if (nickname == null || nickname.trim().length() < 2 || nickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 2~20자로 입력해주세요.");
        }
        userRepository.updateNicknameByEmail(email, nickname.trim());
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 합니다.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없어요."));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않아요.");
        }
        userRepository.updatePasswordByEmail(email, passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        return new TokenResponse(token, user.getNickname());
    }
}

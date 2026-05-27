package com.melodypharmacy.service;

import com.melodypharmacy.dto.TokenResponse;
import com.melodypharmacy.entity.User;
import com.melodypharmacy.repository.UserRepository;
import com.melodypharmacy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenResponse loginWithCode(String code) {
        String accessToken = getKakaoAccessToken(code);
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        String kakaoId = String.valueOf(userInfo.get("id"));
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        String nickname = profile != null ? (String) profile.get("nickname") : "멜로디유저";
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        User user = userRepository.findByKakaoId(kakaoId).orElseGet(() -> {
            String resolvedEmail = (email != null) ? email : "kakao_" + kakaoId + "@kakao.local";
            // 이메일 중복 시 카카오ID 기반 이메일 사용
            if (email != null && userRepository.existsByEmail(email)) {
                resolvedEmail = "kakao_" + kakaoId + "@kakao.local";
            }
            return userRepository.save(User.builder()
                    .kakaoId(kakaoId)
                    .email(resolvedEmail)
                    .password(UUID.randomUUID().toString())
                    .nickname(nickname)
                    .build());
        });

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        return new TokenResponse(token, user.getNickname());
    }

    @SuppressWarnings("unchecked")
    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        ResponseEntity<Map> res = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                new HttpEntity<>(body, headers),
                Map.class);

        return (String) res.getBody().get("access_token");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<Map> res = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class);

        return res.getBody();
    }
}

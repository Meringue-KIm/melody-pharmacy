package com.melodypharmacy.controller;

import com.melodypharmacy.dto.TokenResponse;
import com.melodypharmacy.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam String code) {
        return ResponseEntity.ok(kakaoAuthService.loginWithCode(code));
    }
}

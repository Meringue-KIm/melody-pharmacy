package com.melodypharmacy.controller;

import com.melodypharmacy.config.SongPoolScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SongPoolScheduler songPoolScheduler;

    @PostMapping("/pool/fill")
    public ResponseEntity<String> fillPool() {
        log.info("[Admin] 노래 풀 채우기 수동 트리거");
        new Thread(() -> {
            try {
                songPoolScheduler.nightly();
            } catch (Exception e) {
                log.error("[Admin] 채우기 실패: {}", e.getMessage());
            }
        }, "admin-fill-thread").start();
        return ResponseEntity.ok("노래 풀 채우기 시작됨 (백그라운드 실행)");
    }
}

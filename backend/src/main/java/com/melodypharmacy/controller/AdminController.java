package com.melodypharmacy.controller;

import com.melodypharmacy.config.SongPoolScheduler;
import com.melodypharmacy.repository.ConceptRepository;
import com.melodypharmacy.repository.SituationRepository;
import com.melodypharmacy.service.SongPoolService;
import com.melodypharmacy.service.SongPoolService.YoutubeValidationResult;
import com.melodypharmacy.service.SongPoolService.RemoveInvalidResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SongPoolScheduler songPoolScheduler;
    private final SongPoolService songPoolService;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin token required");
    }

    private boolean isAuthorized(String token) {
        return adminSecretKey != null && adminSecretKey.equals(token);
    }

    @PostMapping("/pool/fill")
    public ResponseEntity<String> fillPool(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!isAuthorized(token)) return unauthorized();
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

    @PostMapping("/pool/fill-full")
    public ResponseEntity<String> fillFull(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!isAuthorized(token)) return unauthorized();
        log.info("[Admin] 전체 풀 완전 채우기 시작");
        new Thread(() -> {
            var situations = situationRepository.findAll();
            var concepts   = conceptRepository.findAll();
            int total = situations.size() * concepts.size();
            int done  = 0;
            for (var sit : situations) {
                for (var con : concepts) {
                    done++;
                    log.info("[Admin] ({}/{}) {}/{} 채우는 중...", done, total, sit.getName(), con.getName());
                    try {
                        songPoolService.fillCombinationToTarget(sit, con);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("[Admin] 전체 채우기 인터럽트됨");
                        return;
                    } catch (Exception e) {
                        log.error("[Admin] [{}/{}] 실패: {}", sit.getName(), con.getName(), e.getMessage());
                    }
                }
            }
            log.info("[Admin] === 전체 풀 완전 채우기 완료 ===");
        }, "full-fill-thread").start();
        return ResponseEntity.ok("전체 풀 완전 채우기 시작됨 (백그라운드, 서버 로그에서 진행 상황 확인)");
    }

    @PostMapping("/pool/remove-invalid")
    public ResponseEntity<RemoveInvalidResult> removeInvalid(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!isAuthorized(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("[Admin] 무효 YouTube ID 곡 제거 시작");
        return ResponseEntity.ok(songPoolService.removeInvalidSongs());
    }

    @PostMapping("/pool/validate-youtube")
    public ResponseEntity<YoutubeValidationResult> validateYoutube(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestParam(defaultValue = "80") int maxFixes) {
        if (!isAuthorized(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("[Admin] YouTube ID 검증 시작 (maxFixes={})", maxFixes);
        return ResponseEntity.ok(songPoolService.validateAndFixYoutubeIds(maxFixes));
    }
}

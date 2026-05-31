package com.melodypharmacy.controller;

import com.melodypharmacy.config.SongPoolScheduler;
import com.melodypharmacy.repository.ConceptRepository;
import com.melodypharmacy.repository.SituationRepository;
import com.melodypharmacy.service.SongPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.melodypharmacy.service.SongPoolService.YoutubeValidationResult;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SongPoolScheduler songPoolScheduler;
    private final SongPoolService songPoolService;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;

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

    /** 모든 조합을 목표(100곡)까지 완전히 채운다. nightly 1회로 부족할 때 사용. */
    @PostMapping("/pool/fill-full")
    public ResponseEntity<String> fillFull() {
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

    /**
     * YouTube ID 검증 및 교정.
     * 1단계: videos.list 배치 검증 (1 unit/50곡, 저렴)
     * 2단계: 잘못된 ID → search.list 교정 (100 unit/곡)
     * @param maxFixes 교정 최대 횟수 (기본 80 = 8,000 unit, 일일 한도 이내)
     */
    @PostMapping("/pool/validate-youtube")
    public ResponseEntity<YoutubeValidationResult> validateYoutube(
            @RequestParam(defaultValue = "80") int maxFixes) {
        log.info("[Admin] YouTube ID 검증 시작 (maxFixes={})", maxFixes);
        YoutubeValidationResult result = songPoolService.validateAndFixYoutubeIds(maxFixes);
        return ResponseEntity.ok(result);
    }
}

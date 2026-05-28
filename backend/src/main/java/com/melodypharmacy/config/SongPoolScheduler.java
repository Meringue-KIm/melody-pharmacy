package com.melodypharmacy.config;

import com.melodypharmacy.repository.ConceptRepository;
import com.melodypharmacy.repository.SituationRepository;
import com.melodypharmacy.service.SongPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongPoolScheduler {

    private final SongPoolService songPoolService;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;

    /**
     * 매일 새벽 2시 - 100곡 미만인 조합을 AI로 보충
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void nightly() {
        log.info("=== [스케줄러] 노래 풀 채우기 시작 ===");
        var situations = situationRepository.findAll();
        var concepts   = conceptRepository.findAll();

        for (var sit : situations) {
            for (var con : concepts) {
                try {
                    songPoolService.fillCombination(sit, con);
                    // Gemini 무료 15 RPM 제한 준수
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception e) {
                    log.error("[{}/{}] 채우기 실패: {}", sit.getName(), con.getName(), e.getMessage());
                }
            }
        }
        log.info("=== [스케줄러] 노래 풀 채우기 완료 ===");
    }

    /**
     * 매주 월요일 새벽 3시 - 조회수 갱신 → 인기 없는 곡 제거 → 다시 채우기
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    public void weekly() {
        log.info("=== [스케줄러] 주간 정리 시작 ===");

        // 1. 조회수 갱신 (배치 조회, API 비용 최소)
        try {
            songPoolService.updateAllViewCounts();
        } catch (Exception e) {
            log.error("조회수 갱신 실패: {}", e.getMessage());
        }

        var situations = situationRepository.findAll();
        var concepts   = conceptRepository.findAll();
        int totalRemoved = 0;

        // 2. 인기 없는 곡 제거
        for (var sit : situations) {
            for (var con : concepts) {
                try {
                    totalRemoved += songPoolService.cleanCombination(sit, con);
                } catch (Exception e) {
                    log.error("[{}/{}] 정리 실패: {}", sit.getName(), con.getName(), e.getMessage());
                }
            }
        }

        log.info("주간 정리 - 총 {}곡 제거", totalRemoved);

        // 3. 빠진 자리 AI로 채우기 (nightly와 동일 로직)
        if (totalRemoved > 0) {
            log.info("=== [스케줄러] 제거 후 재보충 시작 ===");
            for (var sit : situations) {
                for (var con : concepts) {
                    try {
                        songPoolService.fillCombination(sit, con);
                        Thread.sleep(4500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    } catch (Exception e) {
                        log.error("[{}/{}] 재보충 실패: {}", sit.getName(), con.getName(), e.getMessage());
                    }
                }
            }
        }

        log.info("=== [스케줄러] 주간 정리 완료 ===");
    }
}

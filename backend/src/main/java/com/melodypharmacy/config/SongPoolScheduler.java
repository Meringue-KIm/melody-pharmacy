package com.melodypharmacy.config;

import com.melodypharmacy.entity.Concept;
import com.melodypharmacy.entity.Situation;
import com.melodypharmacy.repository.ConceptRepository;
import com.melodypharmacy.repository.SituationRepository;
import com.melodypharmacy.repository.SongTagRepository;
import com.melodypharmacy.service.GeminiService;
import com.melodypharmacy.service.SongPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongPoolScheduler {

    private final SongPoolService songPoolService;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;
    private final SongTagRepository songTagRepository;

    @Value("${song-pool.target-size:100}")
    private int targetSize;

    private static final int DAILY_GEMINI_LIMIT = 20;

    /**
     * 매일 새벽 2시 - 곡 적은 조합 먼저, 일일 한도(20회) 내에서 보충
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void nightly() {
        log.info("=== [스케줄러] 노래 풀 채우기 시작 ===");
        int called = fillSorted(DAILY_GEMINI_LIMIT);
        log.info("=== [스케줄러] 노래 풀 채우기 완료 ({}회 호출) ===", called);
    }

    /**
     * 매주 월요일 새벽 3시 - 조회수 갱신 → 인기 없는 곡 제거 → 다시 채우기
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    public void weekly() {
        log.info("=== [스케줄러] 주간 정리 시작 ===");

        try {
            songPoolService.updateAllViewCounts();
        } catch (Exception e) {
            log.error("조회수 갱신 실패: {}", e.getMessage());
        }

        var situations = situationRepository.findAll();
        var concepts   = conceptRepository.findAll();
        int totalRemoved = 0;

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

        if (totalRemoved > 0) {
            log.info("=== [스케줄러] 제거 후 재보충 시작 ===");
            fillSorted(DAILY_GEMINI_LIMIT);
        }

        log.info("=== [스케줄러] 주간 정리 완료 ===");
    }

    /**
     * 매 호출마다 DB에서 현재 곡 수가 가장 적은 조합을 선택해 채운다.
     * 이전 fill로 바뀐 곡 수를 즉시 반영하므로 항상 최솟값 조합을 우선 처리.
     */
    public int fillSorted(int maxCalls) {
        var situations = situationRepository.findAll();
        var concepts   = conceptRepository.findAll();

        int calls = 0;
        while (calls < maxCalls) {
            // 매 회 DB 재조회 → 현재 곡이 가장 적고 목표 미달인 조합 선택
            Situation minSit = null;
            Concept   minCon = null;
            long      minCnt = Long.MAX_VALUE;

            for (var sit : situations) {
                for (var con : concepts) {
                    long cnt = songTagRepository.countBySituationIdAndConceptId(sit.getId(), con.getId());
                    if (cnt < targetSize && cnt < minCnt) {
                        minCnt = cnt;
                        minSit = sit;
                        minCon = con;
                    }
                }
            }

            if (minSit == null) {
                log.info("[스케줄러] 모든 조합이 목표치({})에 도달했습니다.", targetSize);
                break;
            }

            log.info("[스케줄러] ({}/{}) [{}/{}] {}곡 → 채우기 시작",
                    calls + 1, maxCalls, minSit.getName(), minCon.getName(), minCnt);
            try {
                songPoolService.fillCombination(minSit, minCon);
                calls++;
                Thread.sleep(4500);
            } catch (GeminiService.QuotaExceededException e) {
                log.warn("[스케줄러] Gemini 한도 초과 — 오늘 채우기 중단 ({}회 완료)", calls);
                return calls;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return calls;
            } catch (Exception e) {
                log.error("[{}/{}] 채우기 실패: {}", minSit.getName(), minCon.getName(), e.getMessage());
            }
        }

        if (calls >= maxCalls) {
            log.info("[스케줄러] 일일 한도 {}회 도달, 오늘 종료.", maxCalls);
        }
        return calls;
    }
}

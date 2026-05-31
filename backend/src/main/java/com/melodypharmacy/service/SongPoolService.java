package com.melodypharmacy.service;

import com.melodypharmacy.dto.GeminiSongDto;
import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongPoolService {

    private final GeminiService geminiService;
    private final YouTubeService youTubeService;
    private final SongRepository songRepository;
    private final SongTagRepository songTagRepository;

    @Value("${song-pool.target-size:100}")
    private int targetSize;

    @Value("${song-pool.min-views-to-keep:1000000}")
    private long minViewsToKeep;

    @Value("${song-pool.grace-period-days:30}")
    private int gracePeriodDays;

    /**
     * 조합이 targetSize에 도달할 때까지 반복 보충 (Gemini RPM 준수).
     * fill-full 엔드포인트용.
     */
    public void fillCombinationToTarget(Situation situation, Concept concept) throws InterruptedException {
        if (!geminiService.isConfigured()) return;
        final int maxAttempts = 20;
        int attempts = 0;
        long prev = -1;
        while (songTagRepository.countBySituationIdAndConceptId(situation.getId(), concept.getId()) < targetSize) {
            long current = songTagRepository.countBySituationIdAndConceptId(situation.getId(), concept.getId());
            if (current == prev) {
                attempts++;
                if (attempts >= maxAttempts) {
                    log.warn("[{}/{}] {}회 연속 추가 없음, 중단", situation.getName(), concept.getName(), maxAttempts);
                    break;
                }
            } else {
                attempts = 0;
            }
            prev = current;
            fillCombination(situation, concept);
            Thread.sleep(4500);
        }
    }

    /**
     * 조합의 풀이 targetSize 미만이면 AI로 보충한다.
     */
    @Transactional
    public void fillCombination(Situation situation, Concept concept) {
        if (!geminiService.isConfigured()) return;

        long current = songTagRepository.countBySituationIdAndConceptId(
                situation.getId(), concept.getId());
        if (current >= targetSize) return;

        int needed = (int) (targetSize - current);
        int toRequest = Math.min(needed + 5, 15);

        List<GeminiSongDto> recommendations =
                geminiService.recommend(situation.getName(), concept.getName(), toRequest);
        if (recommendations.isEmpty()) return;

        // Gemini가 준 ID를 배치 검증 (videos API: 1 unit/50개)
        List<String> ytIds = recommendations.stream()
                .map(GeminiSongDto::getYoutubeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Long> viewCounts = youTubeService.batchGetViewCounts(ytIds);

        int added = 0;
        for (GeminiSongDto dto : recommendations) {
            if (added >= needed) break;
            if (dto.getTitle() == null || dto.getArtist() == null) continue;

            String videoId = dto.getYoutubeId();

            // Gemini ID가 유효하지 않으면 search API로 폴백
            if (videoId == null || !viewCounts.containsKey(videoId)) {
                videoId = youTubeService.searchVideoId(dto.getTitle(), dto.getArtist())
                        .orElse(null);
                if (videoId == null) continue;
                Long vc = youTubeService.batchGetViewCounts(List.of(videoId)).get(videoId);
                if (vc != null) viewCounts.put(videoId, vc);
            }

            final String finalVideoId = videoId;
            Long viewCount = viewCounts.get(finalVideoId);

            Song song = songRepository.findByTitleAndArtist(dto.getTitle(), dto.getArtist())
                    .orElseGet(() -> songRepository.save(Song.builder()
                            .title(dto.getTitle())
                            .artist(dto.getArtist())
                            .youtubeUrl("https://www.youtube.com/watch?v=" + finalVideoId)
                            .thumbnailUrl("https://i.ytimg.com/vi/" + finalVideoId + "/hqdefault.jpg")
                            .youtubeViewCount(viewCount)
                            .statsUpdatedAt(LocalDateTime.now())
                            .build()));

            if (!songTagRepository.existsBySongAndSituationAndConcept(song, situation, concept)) {
                songTagRepository.save(SongTag.builder()
                        .song(song).situation(situation).concept(concept)
                        .addedAt(LocalDateTime.now())
                        .build());
                added++;
            }
        }

        if (added > 0) {
            log.info("[{}/{}] {}곡 추가 → 현재 {}곡",
                    situation.getName(), concept.getName(), added, current + added);
        }
    }

    /**
     * 인기 없는 곡을 song_tags에서 제거한다 (songs 테이블은 유지).
     * 조건: AI 추가 + 유예 기간 경과 + 조회수 미달 + 아무도 저장 안 함
     */
    @Transactional
    public int cleanCombination(Situation situation, Concept concept) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(gracePeriodDays);

        List<SongTag> removable = songTagRepository.findRemovableTags(
                situation.getId(), concept.getId(), cutoff, minViewsToKeep);
        if (removable.isEmpty()) return 0;

        // 한 번에 최대 20%만 제거 (급격한 변화 방지)
        long total = songTagRepository.countBySituationIdAndConceptId(
                situation.getId(), concept.getId());
        int maxRemove = Math.max(1, (int) (total * 0.20));

        List<SongTag> toRemove = removable.stream().limit(maxRemove).collect(Collectors.toList());
        songTagRepository.deleteAll(toRemove);

        log.info("[{}/{}] {}곡 제거",
                situation.getName(), concept.getName(), toRemove.size());
        return toRemove.size();
    }

    /**
     * 전체 곡의 YouTube 조회수를 갱신한다 (50개씩 배치).
     */
    @Transactional
    public void updateAllViewCounts() {
        if (!youTubeService.isConfigured()) return;

        List<Song> songs = songRepository.findAll();
        List<String> videoIds = songs.stream()
                .map(s -> extractVideoId(s.getYoutubeUrl()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Long> viewCounts = youTubeService.batchGetViewCounts(videoIds);

        for (Song song : songs) {
            String vid = extractVideoId(song.getYoutubeUrl());
            if (vid != null && viewCounts.containsKey(vid)) {
                songRepository.updateViewCount(song.getId(), viewCounts.get(vid), LocalDateTime.now());
            }
        }
        log.info("조회수 갱신 완료: {}곡", viewCounts.size());
    }

    /**
     * 전체 곡의 YouTube ID를 검증하고 잘못된 ID를 search.list로 교정한다.
     * videos.list: 1 unit/50곡 (저렴)
     * search.list: 100 unit/곡 (비쌈) → maxFixes로 일일 한도 초과 방지
     */
    public YoutubeValidationResult validateAndFixYoutubeIds(int maxFixes) {
        if (!youTubeService.isConfigured()) {
            return new YoutubeValidationResult(0, 0, 0, 0, List.of("YouTube API key not configured"));
        }

        List<Song> songs = songRepository.findAll();
        int total = songs.size();

        // 1단계: 배치 검증 (videos.list, 1 unit per 50)
        List<String> allIds = songs.stream()
                .map(s -> extractVideoId(s.getYoutubeUrl()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        log.info("[검증] 총 {}곡 / {}개 고유 ID 배치 검증 시작 (~{}회 API 호출)",
                total, allIds.size(), (allIds.size() + 49) / 50);

        Set<String> validIds = youTubeService.batchGetViewCounts(allIds).keySet();

        // 2단계: 유효하지 않은 ID 수집
        List<Song> invalids = songs.stream()
                .filter(s -> {
                    String vid = extractVideoId(s.getYoutubeUrl());
                    return vid == null || !validIds.contains(vid);
                })
                .collect(Collectors.toList());

        log.info("[검증] 유효: {}곡 / 무효: {}곡 → 최대 {}곡 교정 시작",
                total - invalids.size(), invalids.size(), maxFixes);

        // 3단계: search.list로 교정 (maxFixes 제한)
        int fixed = 0, failed = 0;
        List<String> failedList = new ArrayList<>();

        for (Song song : invalids) {
            if (fixed + failed >= maxFixes) {
                log.warn("[검증] maxFixes({}) 도달, 나머지 {}곡은 다음 실행에서 처리",
                        maxFixes, invalids.size() - fixed - failed);
                break;
            }

            Optional<String> newVid = youTubeService.searchVideoId(song.getTitle(), song.getArtist());
            if (newVid.isPresent()) {
                String vid = newVid.get();
                songRepository.updateYoutubeInfo(song.getId(),
                        "https://www.youtube.com/watch?v=" + vid,
                        "https://i.ytimg.com/vi/" + vid + "/hqdefault.jpg");
                log.info("[검증] 교정 ✓ {} - {} : {} → {}", song.getTitle(), song.getArtist(),
                        extractVideoId(song.getYoutubeUrl()), vid);
                fixed++;
            } else {
                log.warn("[검증] 교정 ✗ {} - {}", song.getTitle(), song.getArtist());
                failedList.add(song.getTitle() + " - " + song.getArtist());
                failed++;
            }

            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        log.info("[검증] 완료 — 전체: {} / 유효: {} / 교정: {} / 실패: {}",
                total, total - invalids.size(), fixed, failed);

        return new YoutubeValidationResult(total, total - invalids.size(), fixed, failed, failedList);
    }

    public record YoutubeValidationResult(
            int total, int valid, int fixed, int failed, List<String> failedSongs) {}

    private String extractVideoId(String url) {
        if (url == null) return null;
        int idx = url.indexOf("v=");
        if (idx < 0) return null;
        String id = url.substring(idx + 2);
        int amp = id.indexOf('&');
        return amp >= 0 ? id.substring(0, amp) : id;
    }
}

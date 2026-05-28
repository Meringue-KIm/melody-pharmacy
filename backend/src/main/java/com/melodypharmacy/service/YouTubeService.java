package com.melodypharmacy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeService {

    private static final String VIDEOS_URL = "https://www.googleapis.com/youtube/v3/videos";
    private static final String SEARCH_URL  = "https://www.googleapis.com/youtube/v3/search";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${youtube.api-key}")
    private String apiKey;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.startsWith("YOUR_");
    }

    /**
     * 영상 ID 목록을 50개씩 묶어 조회수를 일괄 조회한다.
     * videos API: 1 unit per request (최대 50 ID)
     */
    public Map<String, Long> batchGetViewCounts(List<String> videoIds) {
        Map<String, Long> result = new HashMap<>();
        if (!isConfigured() || videoIds.isEmpty()) return result;

        for (int i = 0; i < videoIds.size(); i += 50) {
            List<String> batch = videoIds.subList(i, Math.min(i + 50, videoIds.size()));
            String ids = String.join(",", batch);
            try {
                String url = UriComponentsBuilder.fromHttpUrl(VIDEOS_URL)
                        .queryParam("id", ids)
                        .queryParam("part", "statistics")
                        .queryParam("key", apiKey)
                        .toUriString();

                String body = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(body);
                for (JsonNode item : root.path("items")) {
                    String id = item.path("id").asText();
                    long views = item.path("statistics").path("viewCount").asLong(0L);
                    result.put(id, views);
                }
            } catch (Exception e) {
                log.error("YouTube 조회수 조회 실패: {}", e.getMessage());
            }
        }
        return result;
    }

    /**
     * 제목 + 아티스트로 검색해 영상 ID를 반환한다.
     * search API: 100 units per request (Gemini ID 검증 실패 시 폴백으로만 사용)
     */
    public Optional<String> searchVideoId(String title, String artist) {
        if (!isConfigured()) return Optional.empty();
        try {
            String q = title + " " + artist + " official";
            String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("q", q)
                    .queryParam("part", "snippet")
                    .queryParam("type", "video")
                    .queryParam("maxResults", "1")
                    .queryParam("key", apiKey)
                    .toUriString();

            String body = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(body);
            JsonNode items = root.path("items");
            if (items.isEmpty()) return Optional.empty();

            String videoId = items.get(0).path("id").path("videoId").asText();
            return videoId.isEmpty() ? Optional.empty() : Optional.of(videoId);
        } catch (Exception e) {
            log.error("YouTube 검색 실패 [{} - {}]: {}", title, artist, e.getMessage());
            return Optional.empty();
        }
    }
}

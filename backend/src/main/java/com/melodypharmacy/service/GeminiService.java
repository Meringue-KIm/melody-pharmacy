package com.melodypharmacy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melodypharmacy.dto.GeminiSongDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    public static class QuotaExceededException extends RuntimeException {
        public QuotaExceededException() { super("Gemini API 일일 한도 초과"); }
    }

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.startsWith("YOUR_");
    }

    private static final int MAX_RETRY = 3;
    private static final long RETRY_WAIT_MS = 8000;

    /** @throws QuotaExceededException Gemini 일일 한도 초과 시 */
    public List<GeminiSongDto> recommend(String situation, String concept, int count, List<String> existingSongs) {
        if (!isConfigured()) {
            log.warn("Gemini API key not configured");
            return List.of();
        }

        String prompt = buildPrompt(situation, concept, count, existingSongs);
        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
            "generationConfig", Map.of(
                "responseMimeType", "application/json",
                "temperature", 0.9
            )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                ResponseEntity<String> res = restTemplate.postForEntity(
                    URL + apiKey, new HttpEntity<>(body, headers), String.class);
                return parseResponse(res.getBody());
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    log.warn("[Gemini] 429 한도 초과 — 오늘 채우기 중단");
                    throw new QuotaExceededException();
                }
                if (e.getStatusCode().value() == 503 && attempt < MAX_RETRY) {
                    log.warn("[Gemini] 503 서버 과부하 [{}/{}] — {}초 후 재시도 ({}/{})",
                            situation, concept, RETRY_WAIT_MS / 1000, attempt, MAX_RETRY);
                    try { Thread.sleep(RETRY_WAIT_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    continue;
                }
                log.error("Gemini HTTP 오류 [{}/{}]: {}", situation, concept, e.getMessage());
                return List.of();
            } catch (Exception e) {
                log.error("Gemini 호출 실패 [{}/{}]: {}", situation, concept, e.getMessage());
                return List.of();
            }
        }
        return List.of();
    }

    private List<GeminiSongDto> parseResponse(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            log.warn("[Gemini] candidates 비어있음. 응답: {}", body);
            return List.of();
        }
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            log.warn("[Gemini] parts 비어있음");
            return List.of();
        }
        String text = parts.get(0).path("text").asText();
        text = text.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();
        return objectMapper.readValue(text, new TypeReference<List<GeminiSongDto>>() {});
    }

    private String buildPrompt(String situation, String concept, int count, List<String> existingSongs) {
        String sitDesc = situationDesc(situation);
        String conDesc = conceptDesc(concept);

        String excludeSection = "";
        if (existingSongs != null && !existingSongs.isEmpty()) {
            excludeSection = "\n아래 곡들은 이미 있으니 절대 추천하지 마세요:\n"
                    + existingSongs.stream().map(s -> "- " + s).collect(java.util.stream.Collectors.joining("\n"))
                    + "\n";
        }

        return String.format("""
                상황: %s (%s)
                분위기: %s (%s)
                %s
                위 상황과 분위기에 딱 어울리는 새로운 노래 %d곡을 추천해주세요.

                조건:
                - 실제로 존재하는 유명한 곡 (스트리밍 서비스에서 들을 수 있는 곡)
                - 한국 노래와 해외 노래를 적절히 섞기
                - 같은 아티스트는 최대 1곡만
                - YouTube 공식 MV의 영상 ID(11자리)를 최대한 정확하게 포함

                반드시 아래 JSON 배열 형식으로만 응답:
                [{"title":"곡명","artist":"아티스트명","youtube_id":"영상ID"}]
                """,
                situation, sitDesc, concept, conDesc, excludeSection, count);
    }

    private String situationDesc(String s) {
        return switch (s) {
            case "출근길"    -> "아침에 활기차게 하루를 시작하는";
            case "비 오는 날" -> "비가 내리는 날 창가에서 감성에 젖는";
            case "운동할 때"  -> "헬스장이나 야외 운동 중 에너지를 높이는";
            case "드라이브"  -> "드라이브하며 창밖 풍경을 즐기는";
            case "잠들기 전" -> "밤에 잠들기 전 차분하게 마음을 정리하는";
            case "카페에서"  -> "카페에서 여유롭게 커피를 마시며 분위기를 즐기는";
            case "공부할 때" -> "집중력을 높이고 공부 흐름을 방해하지 않는";
            case "청소할 때" -> "집 청소나 설거지 등 집안일을 하며 흥을 돋우는";
            default -> s;
        };
    }

    private String conceptDesc(String c) {
        return switch (c) {
            case "신나게"   -> "신나고 흥겨운 업템포, 기분이 절로 업되는";
            case "새로운"   -> "신선하고 트렌디한, 처음 듣는 느낌의";
            case "슬프게"   -> "슬프고 감성적인, 눈물이 날 것 같은";
            case "추억돋는" -> "옛날 생각이 나는 레트로하고 향수를 자극하는";
            case "잔잔하게" -> "차분하고 평온한, 마음이 고요해지는";
            case "위로받고" -> "따뜻하고 포근한, 지친 마음을 위로해주는";
            default -> c;
        };
    }
}

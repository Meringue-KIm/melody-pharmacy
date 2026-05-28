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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.startsWith("YOUR_");
    }

    public List<GeminiSongDto> recommend(String situation, String concept, int count) {
        if (!isConfigured()) {
            log.warn("Gemini API key not configured");
            return List.of();
        }
        try {
            String prompt = buildPrompt(situation, concept, count);
            Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                    "responseMimeType", "application/json",
                    "temperature", 0.9
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> res = restTemplate.postForEntity(
                URL + apiKey, new HttpEntity<>(body, headers), String.class);

            return parseResponse(res.getBody());
        } catch (Exception e) {
            log.error("Gemini 호출 실패 [{}/{}]: {}", situation, concept, e.getMessage());
            return List.of();
        }
    }

    private List<GeminiSongDto> parseResponse(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        String text = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        // 마크다운 코드블록 제거
        text = text.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();

        return objectMapper.readValue(text, new TypeReference<List<GeminiSongDto>>() {});
    }

    private String buildPrompt(String situation, String concept, int count) {
        String sitDesc = situationDesc(situation);
        String conDesc = conceptDesc(concept);
        return String.format("""
                상황: %s (%s)
                분위기: %s (%s)

                위 상황과 분위기에 딱 어울리는 노래 %d곡을 추천해주세요.

                조건:
                - 실제로 존재하는 유명한 곡 (스트리밍 서비스에서 들을 수 있는 곡)
                - 한국 노래와 해외 노래를 적절히 섞기
                - 같은 아티스트는 최대 1곡만
                - YouTube 공식 MV의 영상 ID(11자리)를 최대한 정확하게 포함

                반드시 아래 JSON 배열 형식으로만 응답:
                [{"title":"곡명","artist":"아티스트명","youtube_id":"영상ID"}]
                """,
                situation, sitDesc, concept, conDesc, count);
    }

    private String situationDesc(String s) {
        return switch (s) {
            case "출근길"   -> "아침에 활기차게 하루를 시작하는";
            case "퇴근길"   -> "하루를 마무리하며 집으로 돌아가는";
            case "운동"     -> "고강도 운동 중 에너지를 높이는";
            case "드라이브" -> "드라이브하며 창밖 풍경을 즐기는";
            case "자기 전"  -> "밤에 잠들기 전 차분해지는";
            case "집에서"   -> "집에서 편안하게 쉬는";
            case "공부할 때"-> "집중력을 높이고 방해가 안 되는";
            default -> s;
        };
    }

    private String conceptDesc(String c) {
        return switch (c) {
            case "파워"   -> "강렬하고 에너지 넘치는";
            case "산뜻"   -> "밝고 경쾌한 기분 좋은";
            case "화남"   -> "분노와 강한 에너지를 표출하는";
            case "발라드" -> "서정적이고 감성적인";
            case "힙합"   -> "리듬감 있는 랩과 비트";
            case "EDM"    -> "전자음악으로 흥을 돋우는";
            case "잔잔한" -> "차분하고 평온한";
            case "신남"   -> "신나고 흥겨운 업템포";
            case "로맨틱" -> "달달하고 감미로운 사랑 노래";
            default -> c;
        };
    }
}

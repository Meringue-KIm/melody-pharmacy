package com.melodypharmacy.controller;

import com.melodypharmacy.dto.PlaylistVideoResponse;
import com.melodypharmacy.entity.PlaylistVideo;
import com.melodypharmacy.entity.Situation;
import com.melodypharmacy.entity.Concept;
import com.melodypharmacy.repository.PlaylistVideoRepository;
import com.melodypharmacy.repository.SituationRepository;
import com.melodypharmacy.repository.ConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistVideoRepository playlistVideoRepository;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    private boolean isAuthorized(String token) {
        return adminSecretKey != null && adminSecretKey.equals(token);
    }

    /** 일반 사용자: 조합별 플레이리스트 조회 */
    @GetMapping
    public ResponseEntity<List<PlaylistVideoResponse>> getPlaylists(
            @RequestParam Long situationId,
            @RequestParam Long conceptId) {
        return ResponseEntity.ok(
            playlistVideoRepository.findBySituationIdAndConceptId(situationId, conceptId)
                .stream().map(PlaylistVideoResponse::new).toList()
        );
    }

    /** Admin: 플레이리스트 추가 */
    @PostMapping
    public ResponseEntity<?> addPlaylist(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestBody Map<String, Object> body) {
        if (!isAuthorized(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin token required");

        Long situationId = Long.parseLong(body.get("situationId").toString());
        Long conceptId   = Long.parseLong(body.get("conceptId").toString());
        String url       = body.get("youtubeUrl").toString().trim();
        String title     = body.get("title").toString().trim();

        String videoId = extractVideoId(url);
        if (videoId == null) return ResponseEntity.badRequest().body("유효하지 않은 YouTube URL이에요.");

        if (playlistVideoRepository.existsBySituationIdAndConceptIdAndYoutubeVideoId(situationId, conceptId, videoId))
            return ResponseEntity.badRequest().body("이미 등록된 플레이리스트예요.");

        Situation situation = situationRepository.findById(situationId)
            .orElseThrow(() -> new IllegalArgumentException("situation not found"));
        Concept concept = conceptRepository.findById(conceptId)
            .orElseThrow(() -> new IllegalArgumentException("concept not found"));

        String thumbnail = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";

        PlaylistVideo saved = playlistVideoRepository.save(
            PlaylistVideo.builder()
                .situation(situation)
                .concept(concept)
                .youtubeVideoId(videoId)
                .title(title)
                .thumbnailUrl(thumbnail)
                .build()
        );
        return ResponseEntity.ok(new PlaylistVideoResponse(saved));
    }

    /** Admin: 플레이리스트 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @PathVariable Long id) {
        if (!isAuthorized(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin token required");
        playlistVideoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /** Admin: 전체 조합별 플레이리스트 현황 */
    @GetMapping("/all")
    public ResponseEntity<?> getAllPlaylists(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (!isAuthorized(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin token required");
        return ResponseEntity.ok(
            playlistVideoRepository.findAll().stream().map(PlaylistVideoResponse::new).toList()
        );
    }

    private String extractVideoId(String url) {
        if (url == null) return null;
        // https://www.youtube.com/watch?v=VIDEO_ID
        int vIdx = url.indexOf("v=");
        if (vIdx >= 0) {
            String id = url.substring(vIdx + 2);
            int amp = id.indexOf('&');
            return amp >= 0 ? id.substring(0, amp) : id;
        }
        // https://youtu.be/VIDEO_ID
        if (url.contains("youtu.be/")) {
            String id = url.substring(url.lastIndexOf('/') + 1);
            int q = id.indexOf('?');
            return q >= 0 ? id.substring(0, q) : id;
        }
        return null;
    }
}

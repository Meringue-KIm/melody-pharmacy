package com.melodypharmacy.controller;

import com.melodypharmacy.dto.SongResponse;
import com.melodypharmacy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping("/recommend")
    public ResponseEntity<List<SongResponse>> recommend(
            @RequestParam Long situationId,
            @RequestParam Long conceptId,
            @RequestParam(defaultValue = "false") boolean excludePlayed,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(songService.recommend(situationId, conceptId, userId, excludePlayed));
    }

    @PostMapping("/{songId}/save")
    public ResponseEntity<Void> save(
            @PathVariable Long songId,
            @RequestParam(required = false) Long situationId,
            @RequestParam(required = false) Long conceptId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        songService.save(songId, situationId, conceptId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}/save")
    public ResponseEntity<Void> unsave(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        songService.unsave(songId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saved")
    public ResponseEntity<List<SongResponse>> getSaved(
            @RequestParam(required = false) Long situationId,
            @RequestParam(required = false) Long conceptId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(songService.getSaved(situationId, conceptId, userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SongResponse>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(songService.getHistory(userId));
    }

    @PostMapping("/{songId}/play")
    public ResponseEntity<Void> recordPlay(
            @PathVariable Long songId,
            @RequestParam(required = false) Long situationId,
            @RequestParam(required = false) Long conceptId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        songService.recordPlay(songId, situationId, conceptId, userId);
        return ResponseEntity.ok().build();
    }
}

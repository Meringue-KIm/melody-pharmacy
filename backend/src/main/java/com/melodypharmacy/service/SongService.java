package com.melodypharmacy.service;

import com.melodypharmacy.dto.SongResponse;
import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;
    private final UserSongRepository userSongRepository;
    private final PlayHistoryRepository playHistoryRepository;

    @Transactional(readOnly = true)
    public List<SongResponse> recommend(Long situationId, Long conceptId, Long userId, boolean excludePlayed) {
        List<Song> songs = excludePlayed
                ? songRepository.findRandomExcludingPlayed(situationId, conceptId, userId)
                : songRepository.findRandomBySituationAndConcept(situationId, conceptId);

        Set<Long> savedIds = userSongRepository.findSongIdsByUserId(userId);

        List<Song> ordered;
        if (excludePlayed) {
            ordered = diversifyByArtist(songs);
        } else {
            Set<Long> recentIds = playHistoryRepository.findRecentSongIdsByUserId(
                    userId, LocalDateTime.now().minusDays(7));
            List<Song> fresh  = songs.stream().filter(s -> !recentIds.contains(s.getId())).toList();
            List<Song> recent = songs.stream().filter(s ->  recentIds.contains(s.getId())).toList();
            ordered = new ArrayList<>(diversifyByArtist(fresh));
            ordered.addAll(diversifyByArtist(recent));
        }

        return ordered.stream()
                .map(song -> new SongResponse(song, savedIds.contains(song.getId())))
                .toList();
    }

    private List<Song> diversifyByArtist(List<Song> songs) {
        if (songs.size() <= 1) return new ArrayList<>(songs);
        Map<String, List<Song>> byArtist = new LinkedHashMap<>();
        for (Song song : songs) {
            byArtist.computeIfAbsent(song.getArtist(), k -> new ArrayList<>()).add(song);
        }
        List<List<Song>> groups = new ArrayList<>(byArtist.values());
        Collections.shuffle(groups);
        List<Song> result = new ArrayList<>();
        int maxSize = groups.stream().mapToInt(List::size).max().orElse(0);
        for (int i = 0; i < maxSize; i++) {
            for (List<Song> group : groups) {
                if (i < group.size()) result.add(group.get(i));
            }
        }
        return result;
    }

    @Transactional
    public void save(Long songId, Long situationId, Long conceptId, Long userId) {
        if (userSongRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new IllegalArgumentException("이미 저장된 노래입니다.");
        }

        User user = userRepository.getReferenceById(userId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노래입니다."));
        Situation situation = situationId != null ? situationRepository.getReferenceById(situationId) : null;
        Concept concept = conceptId != null ? conceptRepository.getReferenceById(conceptId) : null;

        userSongRepository.save(UserSong.builder()
                .user(user).song(song)
                .situation(situation).concept(concept)
                .build());
    }

    @Transactional
    public void unsave(Long songId, Long userId) {
        UserSong userSong = userSongRepository.findByUserIdAndSongId(userId, songId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 노래가 없습니다."));
        userSongRepository.delete(userSong);
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getSaved(Long situationId, Long conceptId, Long userId) {
        List<UserSong> userSongs = (situationId != null && conceptId != null)
                ? userSongRepository.findByUserIdAndSituationIdAndConceptId(userId, situationId, conceptId)
                : userSongRepository.findByUserId(userId);

        return userSongs.stream()
                .map(us -> {
                    SongResponse resp = new SongResponse(us.getSong(), true);
                    if (us.getSituation() != null && us.getConcept() != null) {
                        return resp.withSavedContext(
                                us.getSituation().getId(),   us.getSituation().getIcon(), us.getSituation().getName(),
                                us.getConcept().getId(),     us.getConcept().getIcon(),   us.getConcept().getName());
                    }
                    return resp;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SongResponse> getHistory(Long userId) {
        Set<Long> savedIds = userSongRepository.findSongIdsByUserId(userId);

        // 곡별 가장 최근 재생 기록 유지 (순서: 최신순)
        Map<Long, PlayHistory> latestBySong = new LinkedHashMap<>();
        for (PlayHistory h : playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId)) {
            latestBySong.putIfAbsent(h.getSong().getId(), h);
            if (latestBySong.size() >= 20) break;
        }

        return latestBySong.values().stream()
                .map(h -> {
                    SongResponse resp = new SongResponse(h.getSong(), savedIds.contains(h.getSong().getId()));
                    if (h.getSituation() != null && h.getConcept() != null) {
                        resp.withSavedContext(
                                h.getSituation().getId(),  h.getSituation().getIcon(), h.getSituation().getName(),
                                h.getConcept().getId(),    h.getConcept().getIcon(),   h.getConcept().getName());
                    }
                    return resp;
                })
                .toList();
    }

    @Transactional
    public void recordPlay(Long songId, Long situationId, Long conceptId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        Song song = songRepository.getReferenceById(songId);
        Situation situation = situationId != null ? situationRepository.getReferenceById(situationId) : null;
        Concept concept = conceptId != null ? conceptRepository.getReferenceById(conceptId) : null;

        playHistoryRepository.save(PlayHistory.builder()
                .user(user).song(song)
                .situation(situation).concept(concept)
                .build());
    }
}

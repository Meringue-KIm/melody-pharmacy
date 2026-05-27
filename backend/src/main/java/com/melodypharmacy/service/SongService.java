package com.melodypharmacy.service;

import com.melodypharmacy.dto.SongResponse;
import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<SongResponse> recommend(Long situationId, Long conceptId, Long userId) {
        List<Song> songs = songRepository.findRandomBySituationAndConcept(situationId, conceptId);

        return songs.stream()
                .limit(5)
                .map(song -> {
                    boolean saved = userSongRepository.existsByUserIdAndSongId(userId, song.getId());
                    return new SongResponse(song, saved);
                })
                .toList();
    }

    @Transactional
    public void save(Long songId, Long situationId, Long conceptId, Long userId) {
        if (userSongRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new IllegalArgumentException("이미 저장된 노래입니다.");
        }

        User user = userRepository.getReferenceById(userId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노래입니다."));
        Situation situation = situationRepository.getReferenceById(situationId);
        Concept concept = conceptRepository.getReferenceById(conceptId);

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
                .map(us -> new SongResponse(us.getSong(), true))
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

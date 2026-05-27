package com.melodypharmacy.integration;

import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class EntityIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private SongRepository songRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private ConceptRepository conceptRepository;
    @Autowired private SongTagRepository songTagRepository;
    @Autowired private UserSongRepository userSongRepository;
    @Autowired private PlayHistoryRepository playHistoryRepository;

    @Test
    @DisplayName("전체 플로우 통합 테스트: 상황+컨셉 선택 → 추천 → 저장 → 히스토리")
    void fullFlow() {
        // 1. 회원 생성
        User user = userRepository.save(User.builder()
                .email("integration@test.com")
                .password("encodedPw")
                .nickname("통합테스터")
                .build());

        // 2. 상황/컨셉 생성
        Situation situation = situationRepository.save(
                Situation.builder().name("퇴근길").icon("🌆").build());
        Concept concept = conceptRepository.save(
                Concept.builder().name("발라드").icon("🎵").build());

        // 3. 노래 등록 + 태그
        Song song = songRepository.save(Song.builder()
                .title("밤편지").artist("아이유")
                .youtubeUrl("https://youtube.com/watch?v=iu")
                .thumbnailUrl("https://thumbnail.url/iu.jpg")
                .build());
        songTagRepository.save(SongTag.builder()
                .song(song).situation(situation).concept(concept).build());

        // 4. 추천 조회
        List<Song> recommended = songRepository
                .findRandomBySituationAndConcept(situation.getId(), concept.getId());
        assertThat(recommended).hasSize(1);
        assertThat(recommended.get(0).getTitle()).isEqualTo("밤편지");

        // 5. 저장소에 저장
        UserSong userSong = userSongRepository.save(UserSong.builder()
                .user(user).song(song)
                .situation(situation).concept(concept)
                .build());
        assertThat(userSong.getId()).isNotNull();

        // 6. 저장소 조회
        List<UserSong> saved = userSongRepository.findByUserId(user.getId());
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getSong().getTitle()).isEqualTo("밤편지");

        // 7. 재생 히스토리 저장
        PlayHistory history = playHistoryRepository.save(PlayHistory.builder()
                .user(user).song(song)
                .situation(situation).concept(concept)
                .build());
        assertThat(history.getId()).isNotNull();
        assertThat(history.getPlayedAt()).isNotNull();

        // 8. 히스토리 조회
        List<PlayHistory> histories = playHistoryRepository
                .findByUserIdOrderByPlayedAtDesc(user.getId());
        assertThat(histories).hasSize(1);
    }

    @Test
    @DisplayName("DB 연결 및 테이블 생성 확인")
    void dbConnectionTest() {
        long userCount = userRepository.count();
        long songCount = songRepository.count();
        long situationCount = situationRepository.count();
        long conceptCount = conceptRepository.count();

        assertThat(userCount).isGreaterThanOrEqualTo(0);
        assertThat(songCount).isGreaterThanOrEqualTo(0);
        assertThat(situationCount).isGreaterThanOrEqualTo(0);
        assertThat(conceptCount).isGreaterThanOrEqualTo(0);
    }
}

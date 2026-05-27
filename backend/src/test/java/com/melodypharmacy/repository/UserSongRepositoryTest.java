package com.melodypharmacy.repository;

import com.melodypharmacy.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserSongRepositoryTest {

    @Autowired private UserSongRepository userSongRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SongRepository songRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private ConceptRepository conceptRepository;

    private User user;
    private Song song;
    private Situation situation;
    private Concept concept;

    @BeforeEach
    void setUp() {
        userSongRepository.deleteAll();
        songRepository.deleteAll();
        userRepository.deleteAll();
        situationRepository.deleteAll();
        conceptRepository.deleteAll();

        user = userRepository.save(User.builder()
                .email("user@test.com").password("pw").nickname("유저").build());
        song = songRepository.save(Song.builder()
                .title("Test Song").artist("Test Artist")
                .youtubeUrl("https://youtube.com/watch?v=test").build());
        situation = situationRepository.save(
                Situation.builder().name("운동").icon("🏋️").build());
        concept = conceptRepository.save(
                Concept.builder().name("파워").icon("💪").build());
    }

    @Test
    @DisplayName("노래 저장소에 저장 성공")
    void saveUserSong() {
        UserSong userSong = UserSong.builder()
                .user(user).song(song)
                .situation(situation).concept(concept)
                .build();

        UserSong saved = userSongRepository.save(userSong);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("유저 저장소 전체 조회")
    void findByUserId() {
        userSongRepository.save(UserSong.builder()
                .user(user).song(song).situation(situation).concept(concept).build());

        List<UserSong> result = userSongRepository.findByUserId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSong().getTitle()).isEqualTo("Test Song");
    }

    @Test
    @DisplayName("상황+컨셉으로 저장소 조회")
    void findByUserIdAndSituationIdAndConceptId() {
        userSongRepository.save(UserSong.builder()
                .user(user).song(song).situation(situation).concept(concept).build());

        List<UserSong> result = userSongRepository
                .findByUserIdAndSituationIdAndConceptId(user.getId(), situation.getId(), concept.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("이미 저장된 노래 존재 여부 확인")
    void existsByUserIdAndSongId() {
        userSongRepository.save(UserSong.builder()
                .user(user).song(song).situation(situation).concept(concept).build());

        boolean exists = userSongRepository.existsByUserIdAndSongId(user.getId(), song.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("저장된 노래 삭제")
    void deleteUserSong() {
        UserSong saved = userSongRepository.save(UserSong.builder()
                .user(user).song(song).situation(situation).concept(concept).build());

        userSongRepository.delete(saved);

        Optional<UserSong> result = userSongRepository.findByUserIdAndSongId(user.getId(), song.getId());
        assertThat(result).isEmpty();
    }
}

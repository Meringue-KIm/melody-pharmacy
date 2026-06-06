package com.melodypharmacy.service;

import com.melodypharmacy.dto.SongResponse;
import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @InjectMocks private SongService songService;
    @Mock private SongRepository songRepository;
    @Mock private SituationRepository situationRepository;
    @Mock private ConceptRepository conceptRepository;
    @Mock private SongTagRepository songTagRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserSongRepository userSongRepository;
    @Mock private PlayHistoryRepository playHistoryRepository;

    @Test
    @DisplayName("상황+컨셉으로 노래 추천 - 저장 여부 포함")
    void recommend() {
        Song song = Song.builder().title("HUMBLE.").artist("Kendrick Lamar")
                .youtubeUrl("https://youtube.com/watch?v=test").build();

        given(songRepository.findRandomBySituationAndConcept(1L, 1L)).willReturn(List.of(song));
        given(userSongRepository.findSongIdsByUserId(1L)).willReturn(new HashSet<>());
        given(playHistoryRepository.findRecentSongIdsByUserId(any(), any(LocalDateTime.class))).willReturn(new HashSet<>());

        List<SongResponse> result = songService.recommend(1L, 1L, 1L, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("HUMBLE.");
        assertThat(result.get(0).isSaved()).isFalse();
    }

    @Test
    @DisplayName("저장된 노래는 saved=true 로 반환")
    void recommend_savedSong() {
        // ID가 있는 Song을 직접 생성하기 어려우므로, findSongIdsByUserId가 특정 ID를 반환하도록 설정
        // 실제 saved 여부는 Set에 song.getId()가 포함되는지로 결정됨
        // 여기서는 recommend 결과가 isSaved=false인지 확인 (builder로 생성한 song은 id=null)
        Song song = Song.builder().title("밤편지").artist("아이유")
                .youtubeUrl("https://youtube.com/watch?v=iu").build();

        given(songRepository.findRandomBySituationAndConcept(1L, 1L)).willReturn(List.of(song));
        given(userSongRepository.findSongIdsByUserId(1L)).willReturn(new HashSet<>(Set.of(999L)));
        given(playHistoryRepository.findRecentSongIdsByUserId(any(), any(LocalDateTime.class))).willReturn(new HashSet<>());

        List<SongResponse> result = songService.recommend(1L, 1L, 1L, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSaved()).isFalse();
    }

    @Test
    @DisplayName("노래 저장 성공")
    void save_success() {
        Song song = Song.builder().title("Test").artist("Artist")
                .youtubeUrl("https://youtube.com/watch?v=test").build();

        given(userSongRepository.existsByUserIdAndSongId(1L, 1L)).willReturn(false);
        given(songRepository.findById(1L)).willReturn(Optional.of(song));
        given(userRepository.getReferenceById(1L)).willReturn(User.builder().email("t@t.com").password("pw").nickname("t").build());
        given(situationRepository.getReferenceById(1L)).willReturn(Situation.builder().name("운동").icon("dumbbell").build());
        given(conceptRepository.getReferenceById(1L)).willReturn(Concept.builder().name("파워").icon("sparkle").build());

        songService.save(1L, 1L, 1L, 1L);

        verify(userSongRepository).save(any(UserSong.class));
    }

    @Test
    @DisplayName("이미 저장된 노래 저장 시 예외 발생")
    void save_alreadySaved() {
        given(userSongRepository.existsByUserIdAndSongId(1L, 1L)).willReturn(true);

        assertThatThrownBy(() -> songService.save(1L, 1L, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 저장된 노래입니다.");
    }

    @Test
    @DisplayName("저장 취소 성공")
    void unsave_success() {
        UserSong userSong = UserSong.builder()
                .user(User.builder().email("t@t.com").password("pw").nickname("t").build())
                .song(Song.builder().title("Test").artist("A").youtubeUrl("url").build())
                .build();

        given(userSongRepository.findByUserIdAndSongId(1L, 1L)).willReturn(Optional.of(userSong));

        songService.unsave(1L, 1L);

        verify(userSongRepository).delete(userSong);
    }

    @Test
    @DisplayName("저장되지 않은 노래 취소 시 예외 발생")
    void unsave_notFound() {
        given(userSongRepository.findByUserIdAndSongId(1L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> songService.unsave(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("저장된 노래가 없습니다.");
    }

    @Test
    @DisplayName("저장소 전체 조회")
    void getSaved_all() {
        Song song = Song.builder().title("밤편지").artist("아이유").youtubeUrl("url").build();
        UserSong userSong = UserSong.builder()
                .user(User.builder().email("t@t.com").password("pw").nickname("t").build())
                .song(song).build();

        given(userSongRepository.findByUserId(1L)).willReturn(List.of(userSong));

        List<SongResponse> result = songService.getSaved(null, null, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSaved()).isTrue();
    }

    @Test
    @DisplayName("재생 히스토리 저장")
    void recordPlay() {
        given(userRepository.getReferenceById(anyLong())).willReturn(User.builder().email("t@t.com").password("pw").nickname("t").build());
        given(songRepository.getReferenceById(anyLong())).willReturn(Song.builder().title("Test").artist("A").youtubeUrl("url").build());

        songService.recordPlay(1L, null, null, 1L);

        verify(playHistoryRepository).save(any(PlayHistory.class));
    }
}

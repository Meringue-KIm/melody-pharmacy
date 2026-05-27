package com.melodypharmacy.repository;

import com.melodypharmacy.entity.Concept;
import com.melodypharmacy.entity.Situation;
import com.melodypharmacy.entity.Song;
import com.melodypharmacy.entity.SongTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SongRepositoryTest {

    @Autowired private SongRepository songRepository;
    @Autowired private SituationRepository situationRepository;
    @Autowired private ConceptRepository conceptRepository;
    @Autowired private SongTagRepository songTagRepository;

    private Situation situation;
    private Concept concept;

    @BeforeEach
    void setUp() {
        songTagRepository.deleteAll();
        songRepository.deleteAll();
        situationRepository.deleteAll();
        conceptRepository.deleteAll();

        situation = situationRepository.save(
                Situation.builder().name("운동").icon("🏋️").build());
        concept = conceptRepository.save(
                Concept.builder().name("파워").icon("💪").build());
    }

    @Test
    @DisplayName("상황+컨셉으로 노래 추천 - 결과 반환")
    void findRandomBySituationAndConcept() {
        Song song1 = songRepository.save(Song.builder()
                .title("Stronger").artist("Kanye West")
                .youtubeUrl("https://youtube.com/watch?v=test1")
                .build());
        Song song2 = songRepository.save(Song.builder()
                .title("HUMBLE.").artist("Kendrick Lamar")
                .youtubeUrl("https://youtube.com/watch?v=test2")
                .build());

        songTagRepository.save(SongTag.builder().song(song1).situation(situation).concept(concept).build());
        songTagRepository.save(SongTag.builder().song(song2).situation(situation).concept(concept).build());

        List<Song> result = songRepository.findRandomBySituationAndConcept(situation.getId(), concept.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting("title").containsExactlyInAnyOrder("Stronger", "HUMBLE.");
    }

    @Test
    @DisplayName("상황+컨셉으로 노래 추천 - 태그 없으면 빈 리스트")
    void findRandomBySituationAndConcept_empty() {
        List<Song> result = songRepository.findRandomBySituationAndConcept(situation.getId(), concept.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("다른 상황+컨셉의 노래는 추천에서 제외")
    void findRandomBySituationAndConcept_excludeOther() {
        Situation otherSituation = situationRepository.save(
                Situation.builder().name("출근길").icon("🚌").build());
        Concept otherConcept = conceptRepository.save(
                Concept.builder().name("산뜻").icon("🌸").build());

        Song powerSong = songRepository.save(Song.builder()
                .title("Power Song").artist("Artist A")
                .youtubeUrl("https://youtube.com/watch?v=power")
                .build());
        Song morningSOng = songRepository.save(Song.builder()
                .title("Morning Song").artist("Artist B")
                .youtubeUrl("https://youtube.com/watch?v=morning")
                .build());

        songTagRepository.save(SongTag.builder().song(powerSong).situation(situation).concept(concept).build());
        songTagRepository.save(SongTag.builder().song(morningSOng).situation(otherSituation).concept(otherConcept).build());

        List<Song> result = songRepository.findRandomBySituationAndConcept(situation.getId(), concept.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Power Song");
    }
}

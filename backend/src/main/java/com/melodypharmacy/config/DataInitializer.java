package com.melodypharmacy.config;

import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;
    private final SongRepository songRepository;
    private final SongTagRepository songTagRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (situationRepository.count() > 0) return;

        // 상황 등록
        Situation commuteTo = situationRepository.save(Situation.builder().name("출근길").icon("🚌").build());
        Situation commuteFrom = situationRepository.save(Situation.builder().name("퇴근길").icon("🌆").build());
        Situation workout = situationRepository.save(Situation.builder().name("운동").icon("🏋️").build());
        Situation drive = situationRepository.save(Situation.builder().name("드라이브").icon("🚗").build());

        // 컨셉 등록
        Concept power = conceptRepository.save(Concept.builder().name("파워").icon("💪").build());
        Concept fresh = conceptRepository.save(Concept.builder().name("산뜻").icon("🌸").build());
        Concept angry = conceptRepository.save(Concept.builder().name("화남").icon("🔥").build());
        Concept ballad = conceptRepository.save(Concept.builder().name("발라드").icon("🎵").build());
        Concept hiphop = conceptRepository.save(Concept.builder().name("힙합").icon("🎤").build());
        Concept edm = conceptRepository.save(Concept.builder().name("EDM").icon("🎧").build());

        // 노래 등록 + 태그
        saveSong("HUMBLE.", "Kendrick Lamar", "https://www.youtube.com/watch?v=tvTRZJ-4EyI",
                "https://i.ytimg.com/vi/tvTRZJ-4EyI/hqdefault.jpg",
                List.of(workout, commuteTo), List.of(hiphop, power));

        saveSong("Stronger", "Kanye West", "https://www.youtube.com/watch?v=PsO6ZnUZI0g",
                "https://i.ytimg.com/vi/PsO6ZnUZI0g/hqdefault.jpg",
                List.of(workout, commuteTo), List.of(power, hiphop));

        saveSong("밤편지", "아이유", "https://www.youtube.com/watch?v=BzYnNdJhZQw",
                "https://i.ytimg.com/vi/BzYnNdJhZQw/hqdefault.jpg",
                List.of(commuteFrom, drive), List.of(ballad));

        saveSong("에잇", "아이유", "https://www.youtube.com/watch?v=PkjmrgWyDNY",
                "https://i.ytimg.com/vi/PkjmrgWyDNY/hqdefault.jpg",
                List.of(commuteFrom), List.of(ballad, fresh));

        saveSong("Dynamite", "BTS", "https://www.youtube.com/watch?v=gdZLi9oWNZg",
                "https://i.ytimg.com/vi/gdZLi9oWNZg/hqdefault.jpg",
                List.of(commuteTo, workout, drive), List.of(fresh, power));

        saveSong("GODS", "NewJeans", "https://www.youtube.com/watch?v=ArmDp-zijuc",
                "https://i.ytimg.com/vi/ArmDp-zijuc/hqdefault.jpg",
                List.of(commuteTo, drive), List.of(fresh, edm));

        saveSong("Blinding Lights", "The Weeknd", "https://www.youtube.com/watch?v=4NRXx6U8ABQ",
                "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg",
                List.of(drive, workout), List.of(edm, power));

        saveSong("좋아좋아", "BIGBANG", "https://www.youtube.com/watch?v=bS2jFKKvKqI",
                "https://i.ytimg.com/vi/bS2jFKKvKqI/hqdefault.jpg",
                List.of(commuteTo, drive), List.of(fresh, angry));
    }

    private void saveSong(String title, String artist, String youtubeUrl, String thumbnailUrl,
                          List<Situation> situations, List<Concept> concepts) {
        Song song = songRepository.save(Song.builder()
                .title(title).artist(artist)
                .youtubeUrl(youtubeUrl).thumbnailUrl(thumbnailUrl)
                .build());

        for (Situation situation : situations) {
            for (Concept concept : concepts) {
                songTagRepository.save(SongTag.builder()
                        .song(song).situation(situation).concept(concept)
                        .build());
            }
        }
    }
}

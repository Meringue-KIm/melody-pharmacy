package com.melodypharmacy.config;

import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        initSituationsAndConcepts();
        initSongs();
    }

    private void initSituationsAndConcepts() {
        if (situationRepository.count() > 0) return;
        situationRepository.save(Situation.builder().name("출근길").icon("🚌").build());
        situationRepository.save(Situation.builder().name("퇴근길").icon("🌆").build());
        situationRepository.save(Situation.builder().name("운동").icon("🏋️").build());
        situationRepository.save(Situation.builder().name("드라이브").icon("🚗").build());

        conceptRepository.save(Concept.builder().name("파워").icon("💪").build());
        conceptRepository.save(Concept.builder().name("산뜻").icon("🌸").build());
        conceptRepository.save(Concept.builder().name("화남").icon("🔥").build());
        conceptRepository.save(Concept.builder().name("발라드").icon("🎵").build());
        conceptRepository.save(Concept.builder().name("힙합").icon("🎤").build());
        conceptRepository.save(Concept.builder().name("EDM").icon("🎧").build());
    }

    private void initSongs() {
        Situation sit1 = situationRepository.findByName("출근길").orElseThrow();
        Situation sit2 = situationRepository.findByName("퇴근길").orElseThrow();
        Situation sit3 = situationRepository.findByName("운동").orElseThrow();
        Situation sit4 = situationRepository.findByName("드라이브").orElseThrow();

        Concept power  = conceptRepository.findByName("파워").orElseThrow();
        Concept fresh  = conceptRepository.findByName("산뜻").orElseThrow();
        Concept angry  = conceptRepository.findByName("화남").orElseThrow();
        Concept ballad = conceptRepository.findByName("발라드").orElseThrow();
        Concept hiphop = conceptRepository.findByName("힙합").orElseThrow();
        Concept edm    = conceptRepository.findByName("EDM").orElseThrow();

        // ── 파워 ────────────────────────────────────────────────────────────────
        tag("HUMBLE.",           "Kendrick Lamar",    "tvTRZJ-4EyI",   new Situation[]{sit1,sit3},            new Concept[]{power,hiphop});
        tag("Stronger",          "Kanye West",        "PsO6ZnUZI0g",   new Situation[]{sit1,sit2,sit3},       new Concept[]{power});
        tag("Eye of the Tiger",  "Survivor",          "btPJPFnesV4",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{power});
        tag("Lose Yourself",     "Eminem",            "_Yhyp-_hX2s",   new Situation[]{sit1,sit3},            new Concept[]{power,hiphop});
        tag("Believer",          "Imagine Dragons",   "7wtfhZwyrcc",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{power});
        tag("Hall of Fame",      "The Script",        "mk48xbyxHKA",   new Situation[]{sit1,sit2,sit3},       new Concept[]{power});
        tag("Centuries",         "Fall Out Boy",      "_nSmkyDNulk",   new Situation[]{sit1,sit3},            new Concept[]{power});
        tag("Radioactive",       "Imagine Dragons",   "ktvTqknDobU",   new Situation[]{sit1,sit3,sit4},       new Concept[]{power});
        tag("Thunderstruck",     "AC/DC",             "v2AC41dglnM",   new Situation[]{sit3,sit4},            new Concept[]{power});
        tag("ANTIFRAGILE",       "LE SSERAFIM",       "pyf8cbqyfPs",   new Situation[]{sit1,sit3},            new Concept[]{power,fresh});

        // ── 산뜻 ────────────────────────────────────────────────────────────────
        tag("Dynamite",          "BTS",               "gdZLi9oWNZg",   new Situation[]{sit1,sit2,sit3},       new Concept[]{fresh,power});
        tag("Happy",             "Pharrell Williams", "ZbZSe6N_BXs",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{fresh});
        tag("Uptown Funk",       "Mark Ronson",       "OPf0YbXqDm0",   new Situation[]{sit1,sit2,sit4},       new Concept[]{fresh});
        tag("Shape of You",      "Ed Sheeran",        "JGwWNGJdvx8",   new Situation[]{sit1,sit3,sit4},       new Concept[]{fresh});
        tag("Can't Stop the Feeling","Justin Timberlake","ru0K8uYEZWw", new Situation[]{sit1,sit2,sit3},       new Concept[]{fresh});
        tag("Boy With Luv",      "BTS",               "XsX3ATc3FbA",   new Situation[]{sit1,sit2},            new Concept[]{fresh});
        tag("Butter",            "BTS",               "WMweEpGlu_U",   new Situation[]{sit1,sit2},            new Concept[]{fresh});
        tag("좋은 날",            "아이유",            "jeqdYqsrsA0",   new Situation[]{sit1,sit2},            new Concept[]{fresh});
        tag("Shake It Off",      "Taylor Swift",      "nfWlot6h_JM",   new Situation[]{sit1,sit2,sit3},       new Concept[]{fresh});
        tag("After LIKE",        "IVE",               "F0B7HDiY-10",   new Situation[]{sit1,sit2},            new Concept[]{fresh,edm});
        tag("Levitating",        "Dua Lipa",          "TUVcZfQe-Kw",   new Situation[]{sit1,sit4},            new Concept[]{fresh,edm});

        // ── 화남 ────────────────────────────────────────────────────────────────
        tag("In The End",        "Linkin Park",       "eVTXPUF4Oz4",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{angry});
        tag("Numb",              "Linkin Park",       "kXYiU_JCYtU",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{angry});
        tag("Without Me",        "Eminem",            "YVkUvmDQ3HY",   new Situation[]{sit1,sit2,sit3},       new Concept[]{angry,hiphop});
        tag("Breaking the Habit","Linkin Park",       "OB6RDIFaQWM",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{angry});
        tag("Rap God",           "Eminem",            "XbGs_qK2PQA",   new Situation[]{sit1,sit3},            new Concept[]{angry,hiphop});
        tag("좋아좋아",           "BIGBANG",           "bS2jFKKvKqI",   new Situation[]{sit1,sit2,sit4},       new Concept[]{angry,fresh});
        tag("FAKE LOVE",         "BTS",               "7C2z4GqqS5E",   new Situation[]{sit2,sit4},            new Concept[]{angry,hiphop});

        // ── 발라드 ──────────────────────────────────────────────────────────────
        tag("밤편지",             "아이유",            "BzYnNdJhZQw",   new Situation[]{sit1,sit2,sit4},       new Concept[]{ballad});
        tag("에잇",               "아이유",            "PkjmrgWyDNY",   new Situation[]{sit1,sit2,sit4},       new Concept[]{ballad});
        tag("Someone Like You",  "Adele",             "hLQl3WQQoQ0",   new Situation[]{sit1,sit2,sit4},       new Concept[]{ballad});
        tag("Let Her Go",        "Passenger",         "RBumgq5yVrA",   new Situation[]{sit1,sit2,sit4},       new Concept[]{ballad});
        tag("All of Me",         "John Legend",       "450p2Tg1cY0",   new Situation[]{sit2,sit4},            new Concept[]{ballad});
        tag("Thinking Out Loud", "Ed Sheeran",        "lp-EBzKhaW8",   new Situation[]{sit2,sit4},            new Concept[]{ballad});
        tag("모든 날 모든 순간",   "폴킴",              "AQvzqrI1mS4",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{ballad});
        tag("봄날",               "BTS",               "xEeFrLSkMm8",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{ballad});
        tag("Ditto",             "NewJeans",          "pSudEWBAYRE",   new Situation[]{sit2,sit4},            new Concept[]{ballad,fresh});
        tag("밤이 되니까",        "부활",              "0mVEq0t_s2c",   new Situation[]{sit2,sit4},            new Concept[]{ballad});

        // ── 힙합 ────────────────────────────────────────────────────────────────
        tag("God's Plan",        "Drake",             "xpVfcZ0ZcFM",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{hiphop});
        tag("Old Town Road",     "Lil Nas X",         "w2Ov5jzm3j8",   new Situation[]{sit1,sit4},            new Concept[]{hiphop,fresh});
        tag("SICKO MODE",        "Travis Scott",      "6ONRf7h3Mdk",   new Situation[]{sit3,sit4},            new Concept[]{hiphop,power});
        tag("아무노래",           "ZICO",              "U3IHb5pErtQ",   new Situation[]{sit2,sit4},            new Concept[]{hiphop,fresh});
        tag("GODS",              "NewJeans",          "ArmDp-zijuc",   new Situation[]{sit1,sit3,sit4},       new Concept[]{hiphop,edm});

        // ── EDM ─────────────────────────────────────────────────────────────────
        tag("Blinding Lights",   "The Weeknd",        "4NRXx6U8ABQ",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{edm,power});
        tag("Levels",            "Avicii",            "_ovdm2yX4MA",   new Situation[]{sit1,sit3,sit4},       new Concept[]{edm});
        tag("Wake Me Up",        "Avicii",            "IcrbM1l_BoI",   new Situation[]{sit1,sit2,sit3,sit4},  new Concept[]{edm,fresh});
        tag("Animals",           "Martin Garrix",     "gCYcHz2k5x0",   new Situation[]{sit3,sit4},            new Concept[]{edm,power});
        tag("Alone",             "Marshmello",        "ALZHF5UqnU4",   new Situation[]{sit2,sit4},            new Concept[]{edm});
        tag("Titanium",          "David Guetta",      "JRfuAukYTKg",   new Situation[]{sit3,sit4},            new Concept[]{edm,power});
        tag("The Middle",        "Zedd",              "BQ5bxmKJFuM",   new Situation[]{sit1,sit2,sit4},       new Concept[]{edm,fresh});
    }

    private void tag(String title, String artist, String videoId,
                     Situation[] situations, Concept[] concepts) {
        Song song = songRepository.findByTitleAndArtist(title, artist)
                .orElseGet(() -> songRepository.save(Song.builder()
                        .title(title).artist(artist)
                        .youtubeUrl("https://www.youtube.com/watch?v=" + videoId)
                        .thumbnailUrl("https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg")
                        .build()));

        for (Situation sit : situations) {
            for (Concept con : concepts) {
                if (!songTagRepository.existsBySongAndSituationAndConcept(song, sit, con)) {
                    songTagRepository.save(SongTag.builder()
                            .song(song).situation(sit).concept(con).build());
                }
            }
        }
    }
}

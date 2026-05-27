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

        // ── 파워 ───────────────────────────────────────────────
        tag("HUMBLE.",           "Kendrick Lamar",    "tvTRZJ-4EyI",   sit1, sit3,       power, hiphop);
        tag("Stronger",          "Kanye West",        "PsO6ZnUZI0g",   sit1, sit3,       power);
        tag("Eye of the Tiger",  "Survivor",          "btPJPFnesV4",   sit1, sit3, sit4, power);
        tag("Lose Yourself",     "Eminem",            "_Yhyp-_hX2s",   sit1, sit3,       power, hiphop);
        tag("Believer",          "Imagine Dragons",   "7wtfhZwyrcc",   sit1, sit3, sit4, power);
        tag("Hall of Fame",      "The Script",        "mk48xbyxHKA",   sit1, sit2, sit3, power);
        tag("Centuries",         "Fall Out Boy",      "_nSmkyDNulk",   sit1, sit3,       power);
        tag("Radioactive",       "Imagine Dragons",   "ktvTqknDobU",   sit1, sit3, sit4, power);
        tag("Thunderstruck",     "AC/DC",             "v2AC41dglnM",   sit3, sit4,       power);
        tag("ANTIFRAGILE",       "LE SSERAFIM",       "pyf8cbqyfPs",   sit1, sit3,       power, fresh);

        // ── 산뜻 ───────────────────────────────────────────────
        tag("Dynamite",          "BTS",               "gdZLi9oWNZg",   sit1, sit2, sit3, fresh, power);
        tag("Happy",             "Pharrell Williams", "ZbZSe6N_BXs",   sit1, sit2, sit4, fresh);
        tag("Uptown Funk",       "Mark Ronson",       "OPf0YbXqDm0",   sit1, sit2, sit4, fresh);
        tag("Shape of You",      "Ed Sheeran",        "JGwWNGJdvx8",   sit1, sit4,       fresh);
        tag("Can't Stop the Feeling", "Justin Timberlake", "ru0K8uYEZWw", sit1, sit2, sit3, fresh);
        tag("Boy With Luv",      "BTS",               "XsX3ATc3FbA",   sit1, sit2,       fresh);
        tag("Butter",            "BTS",               "WMweEpGlu_U",   sit1, sit2,       fresh);
        tag("좋은 날",            "아이유",            "jeqdYqsrsA0",   sit1, sit2,       fresh);
        tag("Shake It Off",      "Taylor Swift",      "nfWlot6h_JM",   sit1, sit2,       fresh);
        tag("After LIKE",        "IVE",               "F0B7HDiY-10",   sit1, sit2,       fresh, edm);
        tag("Levitating",        "Dua Lipa",          "TUVcZfQe-Kw",   sit1, sit4,       fresh, edm);

        // ── 화남 ───────────────────────────────────────────────
        tag("In The End",        "Linkin Park",       "eVTXPUF4Oz4",   sit1, sit2, sit3, angry);
        tag("Numb",              "Linkin Park",       "kXYiU_JCYtU",   sit1, sit2, sit3, angry);
        tag("Without Me",        "Eminem",            "YVkUvmDQ3HY",   sit1, sit2, sit3, angry, hiphop);
        tag("Breaking the Habit","Linkin Park",       "OB6RDIFaQWM",   sit1, sit2, sit3, angry);
        tag("Rap God",           "Eminem",            "XbGs_qK2PQA",   sit1, sit3,       angry, hiphop);
        tag("좋아좋아",           "BIGBANG",           "bS2jFKKvKqI",   sit1, sit2, sit4, angry, fresh);

        // ── 발라드 ─────────────────────────────────────────────
        tag("밤편지",             "아이유",            "BzYnNdJhZQw",   sit2, sit4,       ballad);
        tag("에잇",               "아이유",            "PkjmrgWyDNY",   sit2, sit4,       ballad);
        tag("Someone Like You",  "Adele",             "hLQl3WQQoQ0",   sit2, sit4,       ballad);
        tag("Let Her Go",        "Passenger",         "RBumgq5yVrA",   sit1, sit2, sit4, ballad);
        tag("All of Me",         "John Legend",       "450p2Tg1cY0",   sit2, sit4,       ballad);
        tag("Thinking Out Loud", "Ed Sheeran",        "lp-EBzKhaW8",   sit2, sit4,       ballad);
        tag("모든 날 모든 순간",   "폴킴",              "AQvzqrI1mS4",   sit1, sit2, sit4, ballad);
        tag("봄날",               "BTS",               "xEeFrLSkMm8",   sit1, sit2, sit4, ballad);
        tag("Ditto",             "NewJeans",          "pSudEWBAYRE",   sit2, sit4,       ballad, fresh);
        tag("밤이 되니까",        "부활",              "0mVEq0t_s2c",   sit2, sit4,       ballad);

        // ── 힙합 ───────────────────────────────────────────────
        tag("God's Plan",        "Drake",             "xpVfcZ0ZcFM",   sit1, sit2, sit3, sit4, hiphop);
        tag("Old Town Road",     "Lil Nas X",         "w2Ov5jzm3j8",   sit1, sit4,       hiphop, fresh);
        tag("SICKO MODE",        "Travis Scott",      "6ONRf7h3Mdk",   sit3, sit4,       hiphop, power);
        tag("FAKE LOVE",         "BTS",               "7C2z4GqqS5E",   sit2, sit4,       hiphop, angry);
        tag("아무노래",           "ZICO",              "U3IHb5pErtQ",   sit2, sit4,       hiphop, fresh);
        tag("GODS",              "NewJeans",          "ArmDp-zijuc",   sit1, sit3, sit4, hiphop, edm);

        // ── EDM ────────────────────────────────────────────────
        tag("Blinding Lights",   "The Weeknd",        "4NRXx6U8ABQ",   sit2, sit3, sit4, edm, power);
        tag("Levels",            "Avicii",            "_ovdm2yX4MA",   sit1, sit3, sit4, edm);
        tag("Wake Me Up",        "Avicii",            "IcrbM1l_BoI",   sit1, sit2, sit4, edm, fresh);
        tag("Animals",           "Martin Garrix",     "gCYcHz2k5x0",   sit3, sit4,       edm, power);
        tag("Alone",             "Marshmello",        "ALZHF5UqnU4",   sit2, sit4,       edm);
        tag("Titanium",          "David Guetta",      "JRfuAukYTKg",   sit3, sit4,       edm, power);
        tag("The Middle",        "Zedd",              "BQ5bxmKJFuM",   sit1, sit4,       edm, fresh);
    }

    private void tag(String title, String artist, String videoId,
                     Object... tagsAndSituations) {
        if (songRepository.existsByTitleAndArtist(title, artist)) return;

        Song song = songRepository.save(Song.builder()
                .title(title).artist(artist)
                .youtubeUrl("https://www.youtube.com/watch?v=" + videoId)
                .thumbnailUrl("https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg")
                .build());

        java.util.List<Situation> sits = new java.util.ArrayList<>();
        java.util.List<Concept> cons = new java.util.ArrayList<>();
        for (Object o : tagsAndSituations) {
            if (o instanceof Situation s) sits.add(s);
            else if (o instanceof Concept c) cons.add(c);
        }
        for (Situation sit : sits) {
            for (Concept con : cons) {
                songTagRepository.save(SongTag.builder()
                        .song(song).situation(sit).concept(con).build());
            }
        }
    }
}

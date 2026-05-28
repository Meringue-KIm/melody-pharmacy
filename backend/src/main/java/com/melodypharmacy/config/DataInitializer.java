package com.melodypharmacy.config;

import com.melodypharmacy.entity.*;
import com.melodypharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SituationRepository situationRepository;
    private final ConceptRepository conceptRepository;
    private final SongRepository songRepository;
    private final SongTagRepository songTagRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdminUser();
        initSituationsAndConcepts();
        initSongs();
    }

    private void initAdminUser() {
        if (userRepository.findByEmail("admin@melody.dev").isPresent()) return;
        userRepository.save(User.builder()
                .email("admin@melody.dev")
                .password(passwordEncoder.encode("admin1234"))
                .nickname("관리자")
                .build());
    }

    private Situation findOrCreateSituation(String name, String icon) {
        return situationRepository.findByName(name)
                .orElseGet(() -> situationRepository.save(Situation.builder().name(name).icon(icon).build()));
    }

    private Concept findOrCreateConcept(String name, String icon) {
        return conceptRepository.findByName(name)
                .orElseGet(() -> conceptRepository.save(Concept.builder().name(name).icon(icon).build()));
    }

    private void initSituationsAndConcepts() {
        findOrCreateSituation("출근길",    "🚌");
        findOrCreateSituation("퇴근길",    "🌆");
        findOrCreateSituation("운동",      "🏋️");
        findOrCreateSituation("드라이브",  "🚗");
        findOrCreateSituation("자기 전",   "🌙");
        findOrCreateSituation("집에서",    "🏠");
        findOrCreateSituation("공부할 때", "📚");

        findOrCreateConcept("파워",   "💪");
        findOrCreateConcept("산뜻",   "🌸");
        findOrCreateConcept("화남",   "🔥");
        findOrCreateConcept("발라드", "🎵");
        findOrCreateConcept("힙합",   "🎤");
        findOrCreateConcept("EDM",    "🎧");
        findOrCreateConcept("잔잔한", "🌊");
        findOrCreateConcept("신남",   "🎉");
        findOrCreateConcept("로맨틱", "💕");
    }

    private void initSongs() {
        Situation sit1 = situationRepository.findByName("출근길").orElseThrow();
        Situation sit2 = situationRepository.findByName("퇴근길").orElseThrow();
        Situation sit3 = situationRepository.findByName("운동").orElseThrow();
        Situation sit4 = situationRepository.findByName("드라이브").orElseThrow();
        Situation sit5 = situationRepository.findByName("자기 전").orElseThrow();
        Situation sit6 = situationRepository.findByName("집에서").orElseThrow();
        Situation sit7 = situationRepository.findByName("공부할 때").orElseThrow();

        Concept power    = conceptRepository.findByName("파워").orElseThrow();
        Concept fresh    = conceptRepository.findByName("산뜻").orElseThrow();
        Concept angry    = conceptRepository.findByName("화남").orElseThrow();
        Concept ballad   = conceptRepository.findByName("발라드").orElseThrow();
        Concept hiphop   = conceptRepository.findByName("힙합").orElseThrow();
        Concept edm      = conceptRepository.findByName("EDM").orElseThrow();
        Concept chill    = conceptRepository.findByName("잔잔한").orElseThrow();
        Concept exciting = conceptRepository.findByName("신남").orElseThrow();
        Concept romantic = conceptRepository.findByName("로맨틱").orElseThrow();

        // ── 파워 ────────────────────────────────────────────────────────────────
        tag("HUMBLE.",           "Kendrick Lamar",    "tvTRZJ-4EyI",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
        tag("Stronger",          "Kanye West",        "PsO6ZnUZI0g",   new Situation[]{sit1,sit2,sit3},                 new Concept[]{power});
        tag("Eye of the Tiger",  "Survivor",          "btPJPFnesV4",   new Situation[]{sit1,sit2,sit3,sit4},            new Concept[]{power});
        tag("Lose Yourself",     "Eminem",            "_Yhyp-_hX2s",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
        tag("Believer",          "Imagine Dragons",   "7wtfhZwyrcc",   new Situation[]{sit1,sit2,sit3,sit4},            new Concept[]{power});
        tag("Hall of Fame",      "The Script",        "mk48xbyxHKA",   new Situation[]{sit1,sit2,sit3},                 new Concept[]{power});
        tag("Centuries",         "Fall Out Boy",      "_nSmkyDNulk",   new Situation[]{sit1,sit3},                      new Concept[]{power});
        tag("Radioactive",       "Imagine Dragons",   "ktvTqknDobU",   new Situation[]{sit1,sit3,sit4},                 new Concept[]{power});
        tag("Thunderstruck",     "AC/DC",             "v2AC41dglnM",   new Situation[]{sit3,sit4},                      new Concept[]{power});
        tag("ANTIFRAGILE",       "LE SSERAFIM",       "pyf8cbqyfPs",   new Situation[]{sit1,sit3},                      new Concept[]{power,fresh});

        // ── 산뜻 ────────────────────────────────────────────────────────────────
        tag("Dynamite",          "BTS",               "gdZLi9oWNZg",   new Situation[]{sit1,sit2,sit3,sit6},            new Concept[]{fresh,power,exciting});
        tag("Happy",             "Pharrell Williams", "ZbZSe6N_BXs",   new Situation[]{sit1,sit2,sit3,sit4,sit6},       new Concept[]{fresh,exciting});
        tag("Uptown Funk",       "Mark Ronson",       "OPf0YbXqDm0",   new Situation[]{sit1,sit2,sit4,sit6},            new Concept[]{fresh,exciting});
        tag("Shape of You",      "Ed Sheeran",        "JGwWNGJdvx8",   new Situation[]{sit1,sit3,sit4},                 new Concept[]{fresh});
        tag("Can't Stop the Feeling","Justin Timberlake","ru0K8uYEZWw", new Situation[]{sit1,sit2,sit3,sit6},            new Concept[]{fresh,exciting});
        tag("Boy With Luv",      "BTS",               "XsX3ATc3FbA",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh});
        tag("Butter",            "BTS",               "WMweEpGlu_U",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("좋은 날",            "아이유",            "jeqdYqsrsA0",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("Shake It Off",      "Taylor Swift",      "nfWlot6h_JM",   new Situation[]{sit1,sit2,sit3,sit6},            new Concept[]{fresh,exciting});
        tag("After LIKE",        "IVE",               "F0B7HDiY-10",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,edm,exciting});
        tag("Levitating",        "Dua Lipa",          "TUVcZfQe-Kw",   new Situation[]{sit1,sit4,sit6},                 new Concept[]{fresh,edm});

        // ── 화남 ────────────────────────────────────────────────────────────────
        tag("In The End",        "Linkin Park",       "eVTXPUF4Oz4",   new Situation[]{sit1,sit2,sit3,sit4},            new Concept[]{angry});
        tag("Numb",              "Linkin Park",       "kXYiU_JCYtU",   new Situation[]{sit1,sit2,sit3,sit4},            new Concept[]{angry});
        tag("Without Me",        "Eminem",            "YVkUvmDQ3HY",   new Situation[]{sit1,sit2,sit3},                 new Concept[]{angry,hiphop});
        tag("Breaking the Habit","Linkin Park",       "OB6RDIFaQWM",   new Situation[]{sit1,sit2,sit3,sit4},            new Concept[]{angry});
        tag("Rap God",           "Eminem",            "XbGs_qK2PQA",   new Situation[]{sit1,sit3},                      new Concept[]{angry,hiphop});
        tag("좋아좋아",           "BIGBANG",           "bS2jFKKvKqI",   new Situation[]{sit1,sit2,sit4},                 new Concept[]{angry,fresh});
        tag("FAKE LOVE",         "BTS",               "7C2z4GqqS5E",   new Situation[]{sit2,sit4},                      new Concept[]{angry,hiphop});

        // ── 발라드 ──────────────────────────────────────────────────────────────
        tag("밤편지",             "아이유",            "BzYnNdJhZQw",   new Situation[]{sit1,sit2,sit4,sit5,sit6},       new Concept[]{ballad,chill,romantic});
        tag("에잇",               "아이유",            "PkjmrgWyDNY",   new Situation[]{sit1,sit2,sit4,sit5,sit6},       new Concept[]{ballad,chill});
        tag("Someone Like You",  "Adele",             "hLQl3WQQoQ0",   new Situation[]{sit1,sit2,sit4,sit5},            new Concept[]{ballad,chill});
        tag("Let Her Go",        "Passenger",         "RBumgq5yVrA",   new Situation[]{sit1,sit2,sit4,sit5},            new Concept[]{ballad,chill});
        tag("All of Me",         "John Legend",       "450p2Tg1cY0",   new Situation[]{sit2,sit4,sit5,sit6},            new Concept[]{ballad,romantic,chill});
        tag("Thinking Out Loud", "Ed Sheeran",        "lp-EBzKhaW8",   new Situation[]{sit2,sit4,sit5,sit6},            new Concept[]{ballad,romantic,chill});
        tag("모든 날 모든 순간",   "폴킴",              "AQvzqrI1mS4",   new Situation[]{sit1,sit2,sit3,sit4,sit5,sit6},  new Concept[]{ballad,romantic,chill});
        tag("봄날",               "BTS",               "xEeFrLSkMm8",   new Situation[]{sit1,sit2,sit3,sit4,sit5},       new Concept[]{ballad,chill,romantic});
        tag("Ditto",             "NewJeans",          "pSudEWBAYRE",   new Situation[]{sit2,sit4,sit5,sit6,sit7},       new Concept[]{ballad,fresh,chill});
        tag("밤이 되니까",        "부활",              "0mVEq0t_s2c",   new Situation[]{sit2,sit4,sit5},                 new Concept[]{ballad,chill});

        // ── 힙합 ────────────────────────────────────────────────────────────────
        tag("God's Plan",        "Drake",             "xpVfcZ0ZcFM",   new Situation[]{sit1,sit2,sit3,sit4,sit6},       new Concept[]{hiphop});
        tag("Old Town Road",     "Lil Nas X",         "w2Ov5jzm3j8",   new Situation[]{sit1,sit4,sit6},                 new Concept[]{hiphop,fresh,exciting});
        tag("SICKO MODE",        "Travis Scott",      "6ONRf7h3Mdk",   new Situation[]{sit3,sit4},                      new Concept[]{hiphop,power});
        tag("아무노래",           "ZICO",              "U3IHb5pErtQ",   new Situation[]{sit2,sit4,sit6},                 new Concept[]{hiphop,fresh,exciting});
        tag("GODS",              "NewJeans",          "ArmDp-zijuc",   new Situation[]{sit1,sit3,sit4},                 new Concept[]{hiphop,edm});

        // ── EDM ─────────────────────────────────────────────────────────────────
        tag("Blinding Lights",   "The Weeknd",        "4NRXx6U8ABQ",   new Situation[]{sit1,sit2,sit3,sit4,sit7},       new Concept[]{edm,power});
        tag("Levels",            "Avicii",            "_ovdm2yX4MA",   new Situation[]{sit1,sit3,sit4,sit7},            new Concept[]{edm,chill});
        tag("Wake Me Up",        "Avicii",            "IcrbM1l_BoI",   new Situation[]{sit1,sit2,sit3,sit4,sit7},       new Concept[]{edm,fresh});
        tag("Animals",           "Martin Garrix",     "gCYcHz2k5x0",   new Situation[]{sit3,sit4},                      new Concept[]{edm,power});
        tag("Alone",             "Marshmello",        "ALZHF5UqnU4",   new Situation[]{sit2,sit4,sit5,sit7},            new Concept[]{edm,chill});
        tag("Titanium",          "David Guetta",      "JRfuAukYTKg",   new Situation[]{sit3,sit4,sit7},                 new Concept[]{edm,power});
        tag("The Middle",        "Zedd",              "BQ5bxmKJFuM",   new Situation[]{sit1,sit2,sit4,sit7},            new Concept[]{edm,fresh,chill});

        // ── Coldplay ─────────────────────────────────────────────────────────────
        tag("Yellow",             "Coldplay",            "yKNxeF4KMsY",   new Situation[]{sit5,sit6,sit7},                 new Concept[]{chill,ballad,romantic});
        tag("The Scientist",      "Coldplay",            "RB-RcX5DS5A",   new Situation[]{sit2,sit5,sit6,sit7},            new Concept[]{ballad,chill});
        tag("Fix You",            "Coldplay",            "k4V3Mo61fJM",   new Situation[]{sit2,sit5,sit6,sit7},            new Concept[]{ballad,chill});

        // ── Adele ────────────────────────────────────────────────────────────────
        tag("Rolling in the Deep","Adele",               "rYEDA3JcQqw",   new Situation[]{sit2,sit4},                      new Concept[]{angry,ballad,power});
        tag("Hello",              "Adele",               "YQHsXMglC9A",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{ballad,chill});

        // ── Queen ────────────────────────────────────────────────────────────────
        tag("Bohemian Rhapsody",  "Queen",               "fJ9rUzIMcZQ",   new Situation[]{sit4,sit6},                      new Concept[]{power,exciting});
        tag("Don't Stop Me Now",  "Queen",               "HgzGwKwLmgM",   new Situation[]{sit3,sit4,sit6},                 new Concept[]{power,exciting});

        // ── 팝/R&B 추가 ──────────────────────────────────────────────────────────
        tag("Shallow",            "Lady Gaga",           "bo_efYSyWq4",   new Situation[]{sit2,sit4,sit5},                 new Concept[]{ballad,romantic,power});
        tag("Heat Waves",         "Glass Animals",       "mRD0-GxqHVo",   new Situation[]{sit4,sit5,sit6,sit7},            new Concept[]{chill,romantic});
        tag("As It Was",          "Harry Styles",        "H5v3kku2yFQ",   new Situation[]{sit1,sit2,sit4,sit6},            new Concept[]{fresh,chill});
        tag("good 4 u",           "Olivia Rodrigo",      "gNi_6U5Pm_o",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{angry,exciting});
        tag("Demons",             "Imagine Dragons",     "mWRsgZuwf_8",   new Situation[]{sit2,sit5,sit7},                 new Concept[]{ballad,chill});
        tag("Natural",            "Imagine Dragons",     "e2X4hHf39ZA",   new Situation[]{sit1,sit3},                      new Concept[]{power});
        tag("Photograph",         "Ed Sheeran",          "Mt9i_7aRbcI",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{ballad,romantic,chill});
        tag("Stay",               "The Kid LAROI",       "kTJczUoc26U",   new Situation[]{sit2,sit4,sit5},                 new Concept[]{ballad,romantic});

        // ── K-POP 추가 ───────────────────────────────────────────────────────────
        tag("Celebrity",          "아이유",              "d3eIjQhCx7Q",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,ballad});
        tag("LILAC",              "아이유",              "Mt3RycMHqcA",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("Permission to Dance","BTS",                 "CuklIb9d3fI",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("DNA",                "BTS",                 "MBdVXkSdhwU",   new Situation[]{sit1,sit6},                      new Concept[]{power,exciting,hiphop});
        tag("Hype Boy",           "NewJeans",            "H4iT1sCFhMQ",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("LOVE DIVE",          "IVE",                 "Y6GHtcaQF00",   new Situation[]{sit1,sit2,sit4,sit6},            new Concept[]{fresh,romantic});
        tag("TOMBOY",             "(G)I-DLE",            "Tm5NDtDnpMY",   new Situation[]{sit1,sit3,sit6},                 new Concept[]{power,angry});
        tag("Psycho",             "Red Velvet",          "eBGIQ7ZuuiU",   new Situation[]{sit2,sit4,sit6},                 new Concept[]{fresh,exciting});

        // ── 신규 곡 ──────────────────────────────────────────────────────────────
        tag("Perfect",           "Ed Sheeran",        "2Vv-BfVoq4g",   new Situation[]{sit2,sit4,sit5,sit6},            new Concept[]{romantic,ballad,chill});
        tag("Stay With Me",      "Sam Smith",         "pB-5XG-DbAA",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{romantic,ballad,chill});
        tag("Bad Guy",           "Billie Eilish",     "DyDfgMOUjCI",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{exciting,fresh});
        tag("Watermelon Sugar",  "Harry Styles",      "E07s5ZYygMg",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,fresh});
        tag("Closer",            "The Chainsmokers",  "PT2_F-1esPk",   new Situation[]{sit4,sit6},                      new Concept[]{edm,romantic});
        tag("Love Story",        "Taylor Swift",      "8xg3vE8Ia_k",   new Situation[]{sit2,sit4,sit5,sit6},            new Concept[]{romantic,ballad});
        tag("Starboy",           "The Weeknd",        "34Nkl2obW_s",   new Situation[]{sit4,sit6},                      new Concept[]{edm,hiphop});
        tag("Peaches",           "Justin Bieber",     "tQ0yjYMFYko",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,exciting});
        tag("drivers license",   "Olivia Rodrigo",    "ZmDBbnmKpqQ",   new Situation[]{sit4,sit5},                      new Concept[]{ballad,chill,romantic});
        tag("Die For You",       "The Weeknd",        "mLDNLBHoSjA",   new Situation[]{sit2,sit4,sit5},                 new Concept[]{romantic,ballad});
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

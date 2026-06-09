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
        addNewSongs();
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
        if (situationRepository.count() > 0) return;
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
        if (songTagRepository.count() > 0) return;
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

        // ── BTS 추가 ────────────────────────────────────────────────────────────────
        tag("Fire",                "BTS",               "ALj5MKGZCeA",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting,hiphop});
        tag("MIC Drop",            "BTS",               "kNSFMI3f5R4",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
        tag("ON",                  "BTS",               "ylzrPFBHkEk",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("Blood Sweat & Tears", "BTS",               "hmE9f-TEutc",   new Situation[]{sit2,sit4},                      new Concept[]{romantic,exciting});
        tag("Black Swan",          "BTS",               "0tMNepKHOQU",   new Situation[]{sit5,sit7},                      new Concept[]{ballad,chill});
        tag("Not Today",           "BTS",               "VDkFKBhFBfI",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});

        // ── BLACKPINK ────────────────────────────────────────────────────────────────
        tag("How You Like That",   "BLACKPINK",         "KLpWa7SvHgs",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("Kill This Love",      "BLACKPINK",         "2S7mZa5oODM",   new Situation[]{sit1,sit3},                      new Concept[]{power,angry});
        tag("DDU-DU DDU-DU",       "BLACKPINK",         "IHNzOHi8sJs",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting,hiphop});
        tag("Lovesick Girls",      "BLACKPINK",         "dyHhTBiPe3U",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{ballad,chill});
        tag("Pink Venom",          "BLACKPINK",         "gQlMMsDqBAs",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});

        // ── TWICE ────────────────────────────────────────────────────────────────────
        tag("CHEER UP",            "TWICE",             "c7rCyll5AeY",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("TT",                  "TWICE",             "ePpPVE-GGJw",   new Situation[]{sit2,sit6},                      new Concept[]{fresh,romantic});
        tag("Feel Special",        "TWICE",             "3ymwOvzhwHs",   new Situation[]{sit2,sit6},                      new Concept[]{ballad,fresh});
        tag("What is Love?",       "TWICE",             "HGCMtNKhIKQ",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,romantic,exciting});
        tag("Dance the Night Away","TWICE",             "8t9BqiSsQf0",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,exciting,edm});
        tag("Fancy",               "TWICE",             "kOZeHFRSBsU",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting,romantic});

        // ── Stray Kids ───────────────────────────────────────────────────────────────
        tag("God's Menu",          "Stray Kids",        "TQTlCHxyuu8",   new Situation[]{sit1,sit3},                      new Concept[]{power,edm});
        tag("MIROH",               "Stray Kids",        "P5IkqD2VsiQ",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
        tag("MANIAC",              "Stray Kids",        "xh2VNdHRV2w",   new Situation[]{sit1,sit3},                      new Concept[]{power,edm});
        tag("Back Door",           "Stray Kids",        "s7OnKSs84z4",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,edm});
        tag("Thunderous",          "Stray Kids",        "Lx_QXkG5bF4",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});

        // ── aespa ────────────────────────────────────────────────────────────────────
        tag("Next Level",          "aespa",             "4TWR90KJl84",   new Situation[]{sit1,sit3},                      new Concept[]{power,edm});
        tag("Black Mamba",         "aespa",             "bCCJtGqRHk0",   new Situation[]{sit1,sit3},                      new Concept[]{power,edm});
        tag("Savage",              "aespa",             "iynq7nS7_OQ",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});

        // ── NCT ──────────────────────────────────────────────────────────────────────
        tag("Regular",             "NCT 127",           "wS9GlOqFRy8",   new Situation[]{sit1,sit3},                      new Concept[]{hiphop,exciting});
        tag("Kick It",             "NCT 127",           "MlBaRPHFJCM",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});

        // ── SHINee ───────────────────────────────────────────────────────────────────
        tag("View",                "SHINee",            "H7mhHCBXpFI",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,chill});
        tag("Ring Ding Dong",      "SHINee",            "uN8gYHr7dEg",   new Situation[]{sit1,sit3,sit6},                 new Concept[]{exciting,edm});

        // ── IVE 추가 ─────────────────────────────────────────────────────────────────
        tag("I AM",                "IVE",               "6ZfJJxeTjFM",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("Kitsch",              "IVE",               "gKGiUFVy4xk",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});

        // ── LE SSERAFIM 추가 ──────────────────────────────────────────────────────────
        tag("FEARLESS",            "LE SSERAFIM",       "oE4fHDg1dVQ",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("UNFORGIVEN",          "LE SSERAFIM",       "G0rzkue3Ahc",   new Situation[]{sit1,sit3},                      new Concept[]{power,edm});

        // ── Red Velvet 추가 ───────────────────────────────────────────────────────────
        tag("Bad Boy",             "Red Velvet",        "bvxTH0KlHEI",   new Situation[]{sit4,sit6},                      new Concept[]{hiphop,fresh});
        tag("Power Up",            "Red Velvet",        "5bguUMHmT6w",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,fresh});

        // ── (G)I-DLE 추가 ────────────────────────────────────────────────────────────
        tag("Queencard",           "(G)I-DLE",          "DBKo_K7MOiM",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,fresh});
        tag("Nxde",                "(G)I-DLE",          "DMEGwOhyGnA",   new Situation[]{sit6},                           new Concept[]{exciting,angry});

        // ── ENHYPEN ──────────────────────────────────────────────────────────────────
        tag("Drunk-Dazed",         "ENHYPEN",           "ELbMhcU2-RA",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,edm});
        tag("Future Perfect",      "ENHYPEN",           "Yp4cHe3gA7A",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});

        // ── TXT ──────────────────────────────────────────────────────────────────────
        tag("Crown",               "TXT",               "T0GRdmMM4gc",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});
        tag("0X1=LOVESONG",        "TXT",               "2_5IAXK7M7k",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});

        // ── SEVENTEEN ────────────────────────────────────────────────────────────────
        tag("Left & Right",        "SEVENTEEN",         "g16k2cQj7XM",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,fresh});
        tag("HIT",                 "SEVENTEEN",         "S5cJPBaVeAM",   new Situation[]{sit1,sit3},                      new Concept[]{exciting,power});
        tag("Rock with you",       "SEVENTEEN",         "EtGmQOJMjLY",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,romantic});

        // ── ITZY ─────────────────────────────────────────────────────────────────────
        tag("DALLA DALLA",         "ITZY",              "pBWGclB3Wt0",   new Situation[]{sit1,sit3,sit6},                 new Concept[]{power,exciting});
        tag("ICY",                 "ITZY",              "OX6aqF5FZaY",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});

        // ── BIGBANG 추가 ──────────────────────────────────────────────────────────────
        tag("Fantastic Baby",      "BIGBANG",           "AjrKFKqfA3I",   new Situation[]{sit1,sit3,sit4},                 new Concept[]{edm,exciting,hiphop});
        tag("Bang Bang Bang",      "BIGBANG",           "EvDEExNqQsA",   new Situation[]{sit1,sit3},                      new Concept[]{edm,power,hiphop});
        tag("Fxxk It",             "BIGBANG",           "sgVy4H5pGpY",   new Situation[]{sit2,sit4,sit6},                 new Concept[]{hiphop,fresh,exciting});

        // ── IU 추가 ──────────────────────────────────────────────────────────────────
        tag("Coin",                "아이유",             "VJi1sYPVFEA",   new Situation[]{sit1,sit2},                      new Concept[]{fresh,exciting});
        tag("Strawberry Moon",     "아이유",             "oU_wPTjFbOw",   new Situation[]{sit5,sit6},                      new Concept[]{chill,romantic});
        tag("Palette",             "아이유",             "HEuPl0sZhAU",   new Situation[]{sit2,sit6},                      new Concept[]{ballad,fresh});
        tag("소격동",               "아이유",             "Fm4sPSfS8SM",   new Situation[]{sit2,sit5,sit7},                 new Concept[]{ballad,chill});
        tag("Blueming",            "아이유",             "d7UcRnvJAQ0",   new Situation[]{sit1,sit5,sit6},                 new Concept[]{fresh,romantic});

        // ── AKMU ─────────────────────────────────────────────────────────────────────
        tag("DINOSAUR",            "AKMU",              "rODw6NF-ZlE",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});
        tag("Melted",              "AKMU",              "0L95UZlFtVo",   new Situation[]{sit2,sit5,sit7},                 new Concept[]{ballad,chill});

        // ── 마마무 MAMAMOO ────────────────────────────────────────────────────────────
        tag("HIP",                 "마마무",             "P8X7oPPPuvM",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
        tag("별이 빛나는 밤",        "마마무",             "1YfovqSB_Vs",   new Situation[]{sit5,sit6},                      new Concept[]{ballad,romantic,chill});

        // ── 선미 SUNMI ────────────────────────────────────────────────────────────────
        tag("Gashina",             "선미",               "kPNDFfGT4Mo",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,power});
        tag("주인공",               "선미",               "1eAWXApW59Y",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{exciting,fresh});

        // ── 자이언티 Zion.T ───────────────────────────────────────────────────────────
        tag("양화대교",             "자이언티",           "1e7bF2_EJGU",   new Situation[]{sit2,sit4,sit5},                 new Concept[]{ballad,hiphop,chill});
        tag("눈",                   "자이언티",           "zmqZGVfPGFo",   new Situation[]{sit5,sit6,sit7},                 new Concept[]{chill,ballad});

        // ── 멜로망스 ──────────────────────────────────────────────────────────────────
        tag("Gift",                "멜로망스",           "sONwRK3vN0k",   new Situation[]{sit5,sit6},                      new Concept[]{ballad,romantic});
        tag("동네",                 "멜로망스",           "oCfSIGkX3HQ",   new Situation[]{sit2,sit6},                      new Concept[]{ballad,chill});
        tag("비가 오는 날엔",        "멜로망스",           "5PNT6hGNM5o",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{ballad,chill});

        // ── 볼빨간사춘기 BOL4 ─────────────────────────────────────────────────────────
        tag("Some",                "볼빨간사춘기",        "zVMND2HkExk",   new Situation[]{sit5,sit6},                      new Concept[]{romantic,ballad});
        tag("우주를 줄게",           "볼빨간사춘기",        "ULpL3KIRuLc",   new Situation[]{sit5,sit6},                      new Concept[]{romantic,ballad,chill});

        // ── 기리보이 Giriboy ──────────────────────────────────────────────────────────
        tag("새벽에",               "기리보이",           "2ZhXQ1PL3MA",   new Situation[]{sit5,sit7},                      new Concept[]{hiphop,chill});
        tag("Bonsai",               "기리보이",           "4FnRYMFE7DU",   new Situation[]{sit5,sit7},                      new Concept[]{hiphop,chill});

        // ── NewJeans 추가 ─────────────────────────────────────────────────────────────
        tag("OMG",                 "NewJeans",          "kNHqj5CaDSc",   new Situation[]{sit1,sit2,sit6},                 new Concept[]{fresh,exciting});
        tag("Super Shy",           "NewJeans",          "AniVFBE2MIk",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});

        // ── Post Malone ───────────────────────────────────────────────────────────────
        tag("Rockstar",            "Post Malone",       "UceaB4D0jpo",   new Situation[]{sit1,sit3},                      new Concept[]{hiphop,power});
        tag("Sunflower",           "Post Malone",       "ApXoWvfEYVU",   new Situation[]{sit4,sit5,sit6},                 new Concept[]{chill,romantic});
        tag("Circles",             "Post Malone",       "wXhTHyIgQ_U",   new Situation[]{sit5,sit6,sit7},                 new Concept[]{chill,romantic});
        tag("Congratulations",     "Post Malone",       "SC4xMk98Pdc",   new Situation[]{sit1,sit6},                      new Concept[]{hiphop,exciting});

        // ── Billie Eilish 추가 ────────────────────────────────────────────────────────
        tag("Ocean Eyes",          "Billie Eilish",     "viimfQi_pUw",   new Situation[]{sit5,sit7},                      new Concept[]{chill,ballad});
        tag("Happier Than Ever",   "Billie Eilish",     "5GJWxDKyk3A",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,angry});
        tag("Therefore I Am",      "Billie Eilish",     "RUSFXgJb0qA",   new Situation[]{sit1,sit6},                      new Concept[]{exciting,fresh});

        // ── The Weeknd 추가 ───────────────────────────────────────────────────────────
        tag("Save Your Tears",     "The Weeknd",        "XXYlFuWiqOA",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});
        tag("Can't Feel My Face",  "The Weeknd",        "KEI4qSrkPAs",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,edm});

        // ── Dua Lipa 추가 ─────────────────────────────────────────────────────────────
        tag("Don't Start Now",     "Dua Lipa",          "oygrmJFkYZM",   new Situation[]{sit1,sit2},                      new Concept[]{fresh,exciting});
        tag("Physical",            "Dua Lipa",          "9HDEHj2yzew",   new Situation[]{sit3,sit6},                      new Concept[]{power,exciting,edm});
        tag("New Rules",           "Dua Lipa",          "k2qgadSvNyU",   new Situation[]{sit1,sit2},                      new Concept[]{fresh,exciting});

        // ── Ariana Grande ─────────────────────────────────────────────────────────────
        tag("7 rings",             "Ariana Grande",     "QYh6mYIJG2Y",   new Situation[]{sit1,sit6},                      new Concept[]{hiphop,exciting});
        tag("thank u, next",       "Ariana Grande",     "gl1aHhXnN1k",   new Situation[]{sit2,sit6},                      new Concept[]{fresh,exciting});
        tag("positions",           "Ariana Grande",     "tcYodQoapMg",   new Situation[]{sit5,sit6},                      new Concept[]{romantic,chill});
        tag("Into You",            "Ariana Grande",     "NnEKFW0cqSI",   new Situation[]{sit4,sit6},                      new Concept[]{romantic,edm});

        // ── Taylor Swift 추가 ─────────────────────────────────────────────────────────
        tag("Anti-Hero",           "Taylor Swift",      "b1kbLwvqugk",   new Situation[]{sit2,sit6},                      new Concept[]{fresh,exciting});
        tag("Blank Space",         "Taylor Swift",      "e-ORhEE9VVg",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,romantic});
        tag("Bad Blood",           "Taylor Swift",      "QcIy9NiNbmo",   new Situation[]{sit1,sit3},                      new Concept[]{angry,power});

        // ── Maroon 5 ─────────────────────────────────────────────────────────────────
        tag("Sugar",               "Maroon 5",          "09R8_2nJtjg",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,exciting,romantic});
        tag("Moves Like Jagger",   "Maroon 5",          "iEPTlhErL-w",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,fresh});
        tag("Girls Like You",      "Maroon 5",          "aJOTlE1K90k",   new Situation[]{sit4,sit5,sit6},                 new Concept[]{romantic,ballad});
        tag("Animals",             "Maroon 5",          "qpgTC9MDx1o",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,edm});
        tag("Memories",            "Maroon 5",          "SlPhMPnQ58k",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{ballad,romantic,chill});

        // ── Bruno Mars ───────────────────────────────────────────────────────────────
        tag("24K Magic",           "Bruno Mars",        "UqyT8IEBkvY",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,fresh});
        tag("That's What I Like",  "Bruno Mars",        "PMivT9Fd4EA",   new Situation[]{sit4,sit6},                      new Concept[]{exciting,romantic});
        tag("Just The Way You Are","Bruno Mars",        "LjhCEhWiKXk",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{romantic,ballad});
        tag("Grenade",             "Bruno Mars",        "Xn676-fLq7I",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,angry});
        tag("Count on Me",         "Bruno Mars",        "7h6RlLdQ1oo",   new Situation[]{sit6,sit7},                      new Concept[]{chill,romantic});

        // ── Charlie Puth ─────────────────────────────────────────────────────────────
        tag("Attention",           "Charlie Puth",      "nfs8NYg7yQM",   new Situation[]{sit2,sit6},                      new Concept[]{exciting,fresh});
        tag("See You Again",       "Wiz Khalifa",       "RgKAFK5djSk",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill,hiphop});
        tag("One Call Away",       "Charlie Puth",      "BxuY9FET9Y4",   new Situation[]{sit2,sit5,sit6},                 new Concept[]{romantic,ballad});

        // ── Shawn Mendes ─────────────────────────────────────────────────────────────
        tag("Stitches",            "Shawn Mendes",      "VbfpW0pbvaU",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});
        tag("Señorita",            "Shawn Mendes",      "Pkh8UtuejGw",   new Situation[]{sit4,sit6},                      new Concept[]{romantic,fresh});
        tag("There's Nothing Holdin' Me Back","Shawn Mendes","o1tMZh4Yfq4",new Situation[]{sit1,sit4},                  new Concept[]{exciting,fresh});

        // ── Justin Bieber 추가 ────────────────────────────────────────────────────────
        tag("Love Yourself",       "Justin Bieber",     "oyEuk8j8imI",   new Situation[]{sit2,sit6},                      new Concept[]{fresh,ballad});
        tag("Sorry",               "Justin Bieber",     "fRh_vgS2dFE",   new Situation[]{sit1,sit6},                      new Concept[]{fresh,exciting});
        tag("What Do You Mean",    "Justin Bieber",     "DK_0jXPuIr0",   new Situation[]{sit6},                           new Concept[]{chill,edm});

        // ── Ed Sheeran 추가 ───────────────────────────────────────────────────────────
        tag("Castle on the Hill",  "Ed Sheeran",        "K0ibBPhiaG0",   new Situation[]{sit1,sit4},                      new Concept[]{exciting,power});
        tag("Bad Habits",          "Ed Sheeran",        "orJSJGHjBLI",   new Situation[]{sit1,sit4,sit6},                 new Concept[]{exciting,edm});

        // ── Coldplay 추가 ─────────────────────────────────────────────────────────────
        tag("A Sky Full of Stars",  "Coldplay",         "mjB3GWqN4LY",   new Situation[]{sit4,sit5,sit6},                 new Concept[]{edm,romantic,chill});
        tag("Clocks",               "Coldplay",         "d020hcWA_Ww",   new Situation[]{sit4,sit7},                      new Concept[]{chill,ballad});
        tag("Paradise",             "Coldplay",         "1G4isv_Fylg",   new Situation[]{sit4,sit6},                      new Concept[]{chill,fresh,romantic});

        // ── Adele 추가 ────────────────────────────────────────────────────────────────
        tag("Set Fire to the Rain", "Adele",            "4tIPcgeFzXY",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,power,angry});
        tag("Skyfall",             "Adele",             "DeumyOPL_o0",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,power});

        // ── Calvin Harris ─────────────────────────────────────────────────────────────
        tag("Summer",              "Calvin Harris",     "ebXbLfLACGM",   new Situation[]{sit4,sit6},                      new Concept[]{edm,exciting});
        tag("Feel So Close",       "Calvin Harris",     "DLv9HMiDdXY",   new Situation[]{sit4,sit6},                      new Concept[]{edm,chill});
        tag("This Is What You Came For","Calvin Harris","kOkQ4T5WO9E",   new Situation[]{sit4,sit7},                      new Concept[]{edm,chill});

        // ── Kygo ─────────────────────────────────────────────────────────────────────
        tag("Firestone",           "Kygo",              "oT3mCytat2k",   new Situation[]{sit4,sit7},                      new Concept[]{edm,chill});
        tag("Here for You",        "Kygo",              "E_4nAKJWOzQ",   new Situation[]{sit5,sit7},                      new Concept[]{edm,romantic});
        tag("Stole the Show",      "Kygo",              "AiF7G7dNRuI",   new Situation[]{sit5,sit7},                      new Concept[]{edm,chill});

        // ── Alan Walker ───────────────────────────────────────────────────────────────
        tag("Faded",               "Alan Walker",       "60ItHLz5WEA",   new Situation[]{sit4,sit5,sit7},                 new Concept[]{edm,chill});
        tag("Alone",               "Alan Walker",       "K4DyBUG242c",   new Situation[]{sit5,sit7},                      new Concept[]{edm,chill});
        tag("Darkside",            "Alan Walker",       "6sfMBHNHuH0",   new Situation[]{sit3,sit7},                      new Concept[]{edm,power});

        // ── The Chainsmokers 추가 ─────────────────────────────────────────────────────
        tag("Don't Let Me Down",   "The Chainsmokers",  "Io0fBr1XBUA",   new Situation[]{sit1,sit4},                      new Concept[]{edm,exciting});
        tag("Something Just Like This","The Chainsmokers","FM7MFYoylVs", new Situation[]{sit4,sit5},                      new Concept[]{edm,romantic});

        // ── Imagine Dragons 추가 ──────────────────────────────────────────────────────
        tag("Thunder",             "Imagine Dragons",   "fKopy74weus",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("Whatever It Takes",   "Imagine Dragons",   "oc3HCzDDByE",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});
        tag("Enemy",               "Imagine Dragons",   "D9G1VOjN_84",   new Situation[]{sit1,sit3},                      new Concept[]{power,angry});

        // ── OneRepublic ───────────────────────────────────────────────────────────────
        tag("Counting Stars",      "OneRepublic",       "hT_nvWreIhg",   new Situation[]{sit1,sit4},                      new Concept[]{power,exciting});
        tag("Apologize",           "OneRepublic",       "ZSM3w1v-A_Y",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});
        tag("Love Runs Out",       "OneRepublic",       "S3fTw7iFRCU",   new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});

        // ── Bon Jovi ─────────────────────────────────────────────────────────────────
        tag("Livin' on a Prayer",  "Bon Jovi",          "lDK9QqIzhwk",   new Situation[]{sit3,sit4},                      new Concept[]{power,exciting});
        tag("It's My Life",        "Bon Jovi",          "vx2u5uUszsCw",  new Situation[]{sit1,sit3},                      new Concept[]{power,exciting});

        // ── Guns N' Roses ─────────────────────────────────────────────────────────────
        tag("Sweet Child O' Mine",  "Guns N' Roses",    "1w7OgIMMRc4",   new Situation[]{sit3,sit4},                      new Concept[]{power,exciting});
        tag("November Rain",        "Guns N' Roses",    "8SbUC-UaAxE",   new Situation[]{sit5},                           new Concept[]{ballad,chill});
        tag("Welcome to the Jungle","Guns N' Roses",    "o1tj2zJ2Wvg",   new Situation[]{sit3},                           new Concept[]{power,angry});

        // ── Linkin Park 추가 ──────────────────────────────────────────────────────────
        tag("What I've Done",      "Linkin Park",       "8sgycukfqeM",   new Situation[]{sit2,sit3},                      new Concept[]{angry,power});
        tag("Crawling",            "Linkin Park",       "Gd9OhYroLN0",   new Situation[]{sit2,sit5},                      new Concept[]{angry,chill});
        tag("Faint",               "Linkin Park",       "LYU-8IFcDPw",   new Situation[]{sit1,sit3},                      new Concept[]{angry,power});

        // ── Eminem 추가 ───────────────────────────────────────────────────────────────
        tag("Love the Way You Lie","Eminem",            "uelHwf8o7_U",   new Situation[]{sit2,sit5},                      new Concept[]{angry,ballad});
        tag("Till I Collapse",     "Eminem",            "eKitHIFbX7Pw",  new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});

        // ── Drake 추가 ────────────────────────────────────────────────────────────────
        tag("One Dance",           "Drake",             "qL8KH_ABKOA",   new Situation[]{sit4,sit6},                      new Concept[]{hiphop,exciting});
        tag("Hotline Bling",       "Drake",             "uxpDa-c-4Mc",   new Situation[]{sit2,sit6},                      new Concept[]{hiphop,fresh});
        tag("In My Feelings",      "Drake",             "DRS_PpOrUZ4",   new Situation[]{sit4,sit6},                      new Concept[]{hiphop,exciting});

        // ── Kendrick Lamar 추가 ───────────────────────────────────────────────────────
        tag("DNA",                 "Kendrick Lamar",    "NLCX5RDx-dg",   new Situation[]{sit1,sit3},                      new Concept[]{hiphop,power});
        tag("Alright",             "Kendrick Lamar",    "Z-48u_gA_dc",   new Situation[]{sit1,sit3},                      new Concept[]{hiphop,power});

        // ── Travis Scott 추가 ─────────────────────────────────────────────────────────
        tag("goosebumps",          "Travis Scott",      "A3_qMqJQ06g",   new Situation[]{sit4,sit6},                      new Concept[]{hiphop,edm});
        tag("Highest in the Room", "Travis Scott",      "fHRDfKBP35M",   new Situation[]{sit4,sit6},                      new Concept[]{hiphop,chill});

        // ── Kanye West 추가 ───────────────────────────────────────────────────────────
        tag("Gold Digger",         "Kanye West",        "CYKIvkE4YrE",   new Situation[]{sit1,sit6},                      new Concept[]{hiphop,exciting});
        tag("Heartless",           "Kanye West",        "Co0tTeuUVeU",   new Situation[]{sit2,sit5},                      new Concept[]{hiphop,chill});

        // ── J. Cole ──────────────────────────────────────────────────────────────────
        tag("No Role Modelz",      "J. Cole",           "oX8OsAGQJvU",   new Situation[]{sit1,sit6},                      new Concept[]{hiphop});
        tag("MIDDLE CHILD",        "J. Cole",           "FUFJXGe_MwE",   new Situation[]{sit1,sit3},                      new Concept[]{hiphop,power});

        // ── Khalid ────────────────────────────────────────────────────────────────────
        tag("Young Dumb & Broke",  "Khalid",            "pBkHHoOIIn8",   new Situation[]{sit1,sit2},                      new Concept[]{fresh,ballad});
        tag("Talk",                "Khalid",            "HVpifGRHkC0",   new Situation[]{sit5,sit6},                      new Concept[]{chill,romantic});
        tag("Location",            "Khalid",            "PxJVCnHWGGM",   new Situation[]{sit5,sit6},                      new Concept[]{chill,hiphop});

        // ── SZA ──────────────────────────────────────────────────────────────────────
        tag("Kill Bill",           "SZA",               "3PpwZKTNKgY",   new Situation[]{sit2,sit6},                      new Concept[]{fresh,angry});
        tag("Good Days",           "SZA",               "ClH6a0tI2V0",   new Situation[]{sit5,sit6},                      new Concept[]{chill,romantic});

        // ── Frank Ocean ──────────────────────────────────────────────────────────────
        tag("Chanel",              "Frank Ocean",       "uo35c0rNFEA",   new Situation[]{sit5,sit6,sit7},                 new Concept[]{hiphop,chill});
        tag("Nights",              "Frank Ocean",       "8Ls3TIpOCRo",   new Situation[]{sit5,sit7},                      new Concept[]{hiphop,chill,edm});

        // ── Camila Cabello ────────────────────────────────────────────────────────────
        tag("Havana",              "Camila Cabello",    "HCjNJDNzw8Y",   new Situation[]{sit4,sit6},                      new Concept[]{fresh,exciting,romantic});

        // ── Lewis Capaldi ────────────────────────────────────────────────────────────
        tag("Someone You Loved",   "Lewis Capaldi",     "zABZyZHCev4",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});
        tag("Before You Go",       "Lewis Capaldi",     "CYZiPFjkRrc",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill});

        // ── Sam Smith 추가 ────────────────────────────────────────────────────────────
        tag("Writing's On The Wall","Sam Smith",        "X4n28YDHJro",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill,romantic});
        tag("Lay Me Down",         "Sam Smith",         "IDLe6hOy7Vc",   new Situation[]{sit5,sit7},                      new Concept[]{ballad,chill});

        // ── U2 ────────────────────────────────────────────────────────────────────────
        tag("With or Without You", "U2",                "_1GS4PQBKF4",   new Situation[]{sit2,sit5},                      new Concept[]{ballad,chill,romantic});
        tag("One",                 "U2",                "ftjEcrrf7r0",   new Situation[]{sit5,sit6,sit7},                 new Concept[]{ballad,chill});

        // ── Lord Huron / 기타 인디 ───────────────────────────────────────────────────
        tag("The Night We Met",    "Lord Huron",        "KtlgYxa6BMU",   new Situation[]{sit5,sit7},                      new Concept[]{ballad,chill,romantic});
        tag("Waves",               "Mr. Probz",         "0B2KQtFaKGE",   new Situation[]{sit4,sit5,sit7},                 new Concept[]{chill,edm});
        tag("Counting Stars",      "NF",                "LsYFtAL6oQ0",   new Situation[]{sit1,sit3},                      new Concept[]{power,hiphop});
    }

    private void addNewSongs() {
        Situation s출근 = situationRepository.findByName("출근길").orElse(null);
        Situation s비오는날 = situationRepository.findByName("비 오는 날").orElse(null);
        Situation s운동 = situationRepository.findByName("운동할 때").orElse(null);
        Situation s드라이브 = situationRepository.findByName("드라이브").orElse(null);
        Situation s잠들기 = situationRepository.findByName("잠들기 전").orElse(null);
        Situation s카페 = situationRepository.findByName("카페에서").orElse(null);
        Situation s공부 = situationRepository.findByName("공부할 때").orElse(null);
        Situation s청소 = situationRepository.findByName("청소할 때").orElse(null);

        Concept c신나게  = conceptRepository.findByName("신나게").orElse(null);
        Concept c새로운  = conceptRepository.findByName("새로운").orElse(null);
        Concept c슬프게  = conceptRepository.findByName("슬프게").orElse(null);
        Concept c추억    = conceptRepository.findByName("추억돋는").orElse(null);
        Concept c잔잔    = conceptRepository.findByName("잔잔하게").orElse(null);
        Concept c위로    = conceptRepository.findByName("위로받고").orElse(null);

        if (s출근 == null || c신나게 == null) return; // DB 구조 불일치 시 스킵

        // ── BTS 추가 ──────────────────────────────────────────────────────────
        tag("Fire",                "BTS",               "ALj5MKGZCeA",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("MIC Drop",            "BTS",               "kNSFMI3f5R4",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("ON",                  "BTS",               "ylzrPFBHkEk",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Blood Sweat & Tears", "BTS",               "hmE9f-TEutc",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c신나게});
        tag("Black Swan",          "BTS",               "0tMNepKHOQU",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});
        tag("Not Today",           "BTS",               "VDkFKBhFBfI",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── BLACKPINK ─────────────────────────────────────────────────────────
        tag("How You Like That",   "BLACKPINK",         "KLpWa7SvHgs",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Kill This Love",      "BLACKPINK",         "2S7mZa5oODM",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("DDU-DU DDU-DU",       "BLACKPINK",         "IHNzOHi8sJs",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Lovesick Girls",      "BLACKPINK",         "dyHhTBiPe3U",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Pink Venom",          "BLACKPINK",         "gQlMMsDqBAs",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── TWICE ─────────────────────────────────────────────────────────────
        tag("CHEER UP",            "TWICE",             "c7rCyll5AeY",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("TT",                  "TWICE",             "ePpPVE-GGJw",   new Situation[]{s카페,s비오는날},                 new Concept[]{c새로운,c추억});
        tag("Feel Special",        "TWICE",             "3ymwOvzhwHs",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c잔잔});
        tag("What is Love?",       "TWICE",             "HGCMtNKhIKQ",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("Dance the Night Away","TWICE",             "8t9BqiSsQf0",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게});
        tag("Fancy",               "TWICE",             "kOZeHFRSBsU",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── Stray Kids ────────────────────────────────────────────────────────
        tag("God's Menu",          "Stray Kids",        "TQTlCHxyuu8",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("MIROH",               "Stray Kids",        "P5IkqD2VsiQ",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("MANIAC",              "Stray Kids",        "xh2VNdHRV2w",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Back Door",           "Stray Kids",        "s7OnKSs84z4",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게});
        tag("Thunderous",          "Stray Kids",        "Lx_QXkG5bF4",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── aespa ─────────────────────────────────────────────────────────────
        tag("Next Level",          "aespa",             "4TWR90KJl84",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Black Mamba",         "aespa",             "bCCJtGqRHk0",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Savage",              "aespa",             "iynq7nS7_OQ",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── NCT ───────────────────────────────────────────────────────────────
        tag("Regular",             "NCT 127",           "wS9GlOqFRy8",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Kick It",             "NCT 127",           "MlBaRPHFJCM",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── SHINee ────────────────────────────────────────────────────────────
        tag("View",                "SHINee",            "H7mhHCBXpFI",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("Ring Ding Dong",      "SHINee",            "uN8gYHr7dEg",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게});

        // ── IVE ───────────────────────────────────────────────────────────────
        tag("I AM",                "IVE",               "6ZfJJxeTjFM",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Kitsch",              "IVE",               "gKGiUFVy4xk",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── LE SSERAFIM ───────────────────────────────────────────────────────
        tag("FEARLESS",            "LE SSERAFIM",       "oE4fHDg1dVQ",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("UNFORGIVEN",          "LE SSERAFIM",       "G0rzkue3Ahc",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── Red Velvet ────────────────────────────────────────────────────────
        tag("Bad Boy",             "Red Velvet",        "bvxTH0KlHEI",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c신나게});
        tag("Power Up",            "Red Velvet",        "5bguUMHmT6w",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게});

        // ── (G)I-DLE ──────────────────────────────────────────────────────────
        tag("Queencard",           "(G)I-DLE",          "DBKo_K7MOiM",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});

        // ── ENHYPEN ───────────────────────────────────────────────────────────
        tag("Drunk-Dazed",         "ENHYPEN",           "ELbMhcU2-RA",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── TXT ───────────────────────────────────────────────────────────────
        tag("Crown",               "TXT",               "T0GRdmMM4gc",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("0X1=LOVESONG",        "TXT",               "2_5IAXK7M7k",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── SEVENTEEN ─────────────────────────────────────────────────────────
        tag("Left & Right",        "SEVENTEEN",         "g16k2cQj7XM",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("HIT",                 "SEVENTEEN",         "S5cJPBaVeAM",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Rock with you",       "SEVENTEEN",         "EtGmQOJMjLY",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});

        // ── ITZY ──────────────────────────────────────────────────────────────
        tag("DALLA DALLA",         "ITZY",              "pBWGclB3Wt0",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("ICY",                 "ITZY",              "OX6aqF5FZaY",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── BIGBANG 추가 ──────────────────────────────────────────────────────
        tag("Fantastic Baby",      "BIGBANG",           "AjrKFKqfA3I",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Bang Bang Bang",      "BIGBANG",           "EvDEExNqQsA",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Fxxk It",             "BIGBANG",           "sgVy4H5pGpY",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});

        // ── IU 추가 ───────────────────────────────────────────────────────────
        tag("Coin",                "아이유",             "VJi1sYPVFEA",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("Strawberry Moon",     "아이유",             "oU_wPTjFbOw",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c위로});
        tag("Palette",             "아이유",             "HEuPl0sZhAU",   new Situation[]{s비오는날,s카페},                 new Concept[]{c잔잔,c추억});
        tag("소격동",               "아이유",             "Fm4sPSfS8SM",   new Situation[]{s비오는날,s잠들기,s공부},         new Concept[]{c슬프게,c추억});
        tag("Blueming",            "아이유",             "d7UcRnvJAQ0",   new Situation[]{s카페,s드라이브},                 new Concept[]{c새로운,c잔잔});

        // ── AKMU ──────────────────────────────────────────────────────────────
        tag("DINOSAUR",            "AKMU",              "rODw6NF-ZlE",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Melted",              "AKMU",              "0L95UZlFtVo",   new Situation[]{s비오는날,s잠들기,s공부},         new Concept[]{c슬프게,c위로});

        // ── 마마무 ────────────────────────────────────────────────────────────
        tag("HIP",                 "마마무",             "P8X7oPPPuvM",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("별이 빛나는 밤",        "마마무",             "1YfovqSB_Vs",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c위로});

        // ── 선미 ──────────────────────────────────────────────────────────────
        tag("Gashina",             "선미",               "kPNDFfGT4Mo",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("주인공",               "선미",               "1eAWXApW59Y",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── 자이언티 ──────────────────────────────────────────────────────────
        tag("양화대교",             "자이언티",           "1e7bF2_EJGU",   new Situation[]{s비오는날,s드라이브,s잠들기},     new Concept[]{c슬프게,c추억});
        tag("눈",                   "자이언티",           "zmqZGVfPGFo",   new Situation[]{s잠들기,s카페,s공부},             new Concept[]{c잔잔,c위로});

        // ── 멜로망스 ──────────────────────────────────────────────────────────
        tag("Gift",                "멜로망스",           "sONwRK3vN0k",   new Situation[]{s잠들기,s카페},                   new Concept[]{c위로,c잔잔});
        tag("동네",                 "멜로망스",           "oCfSIGkX3HQ",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c추억});
        tag("비가 오는 날엔",        "멜로망스",           "5PNT6hGNM5o",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c위로});

        // ── 볼빨간사춘기 ──────────────────────────────────────────────────────
        tag("Some",                "볼빨간사춘기",        "zVMND2HkExk",   new Situation[]{s잠들기,s카페},                   new Concept[]{c위로,c잔잔});
        tag("우주를 줄게",           "볼빨간사춘기",        "ULpL3KIRuLc",   new Situation[]{s잠들기,s카페},                   new Concept[]{c위로,c잔잔});

        // ── 기리보이 ──────────────────────────────────────────────────────────
        tag("새벽에",               "기리보이",           "2ZhXQ1PL3MA",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});

        // ── NewJeans ──────────────────────────────────────────────────────────
        tag("OMG",                 "NewJeans",          "kNHqj5CaDSc",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("Super Shy",           "NewJeans",          "AniVFBE2MIk",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── Post Malone ───────────────────────────────────────────────────────
        tag("Rockstar",            "Post Malone",       "UceaB4D0jpo",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Sunflower",           "Post Malone",       "ApXoWvfEYVU",   new Situation[]{s드라이브,s잠들기},               new Concept[]{c잔잔,c위로});
        tag("Circles",             "Post Malone",       "wXhTHyIgQ_U",   new Situation[]{s잠들기,s카페,s공부},             new Concept[]{c잔잔,c위로});
        tag("Congratulations",     "Post Malone",       "SC4xMk98Pdc",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c추억});

        // ── Billie Eilish ─────────────────────────────────────────────────────
        tag("Ocean Eyes",          "Billie Eilish",     "viimfQi_pUw",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});
        tag("Happier Than Ever",   "Billie Eilish",     "5GJWxDKyk3A",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Therefore I Am",      "Billie Eilish",     "RUSFXgJb0qA",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── The Weeknd ────────────────────────────────────────────────────────
        tag("Save Your Tears",     "The Weeknd",        "XXYlFuWiqOA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Can't Feel My Face",  "The Weeknd",        "KEI4qSrkPAs",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});

        // ── Dua Lipa ──────────────────────────────────────────────────────────
        tag("Don't Start Now",     "Dua Lipa",          "oygrmJFkYZM",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Physical",            "Dua Lipa",          "9HDEHj2yzew",   new Situation[]{s운동,s청소},                     new Concept[]{c신나게});
        tag("New Rules",           "Dua Lipa",          "k2qgadSvNyU",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});

        // ── Ariana Grande ─────────────────────────────────────────────────────
        tag("7 rings",             "Ariana Grande",     "QYh6mYIJG2Y",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("thank u, next",       "Ariana Grande",     "gl1aHhXnN1k",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("positions",           "Ariana Grande",     "tcYodQoapMg",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c위로});
        tag("Into You",            "Ariana Grande",     "NnEKFW0cqSI",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});

        // ── Taylor Swift ──────────────────────────────────────────────────────
        tag("Anti-Hero",           "Taylor Swift",      "b1kbLwvqugk",   new Situation[]{s비오는날,s카페},                 new Concept[]{c새로운,c추억});
        tag("Blank Space",         "Taylor Swift",      "e-ORhEE9VVg",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});
        tag("Bad Blood",           "Taylor Swift",      "QcIy9NiNbmo",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── Maroon 5 ──────────────────────────────────────────────────────────
        tag("Sugar",               "Maroon 5",          "09R8_2nJtjg",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("Moves Like Jagger",   "Maroon 5",          "iEPTlhErL-w",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게});
        tag("Girls Like You",      "Maroon 5",          "aJOTlE1K90k",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c위로,c잔잔});
        tag("Memories",            "Maroon 5",          "SlPhMPnQ58k",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── Bruno Mars ────────────────────────────────────────────────────────
        tag("24K Magic",           "Bruno Mars",        "UqyT8IEBkvY",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("That's What I Like",  "Bruno Mars",        "PMivT9Fd4EA",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게});
        tag("Just The Way You Are","Bruno Mars",        "LjhCEhWiKXk",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("Grenade",             "Bruno Mars",        "Xn676-fLq7I",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Count on Me",         "Bruno Mars",        "7h6RlLdQ1oo",   new Situation[]{s공부,s카페},                     new Concept[]{c잔잔,c위로});

        // ── Charlie Puth ──────────────────────────────────────────────────────
        tag("Attention",           "Charlie Puth",      "nfs8NYg7yQM",   new Situation[]{s비오는날,s카페},                 new Concept[]{c새로운,c추억});
        tag("See You Again",       "Wiz Khalifa",       "RgKAFK5djSk",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("One Call Away",       "Charlie Puth",      "BxuY9FET9Y4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});

        // ── Shawn Mendes ──────────────────────────────────────────────────────
        tag("Stitches",            "Shawn Mendes",      "VbfpW0pbvaU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Señorita",            "Shawn Mendes",      "Pkh8UtuejGw",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("There's Nothing Holdin' Me Back","Shawn Mendes","o1tMZh4Yfq4",new Situation[]{s출근,s드라이브},             new Concept[]{c신나게,c새로운});

        // ── Justin Bieber ─────────────────────────────────────────────────────
        tag("Love Yourself",       "Justin Bieber",     "oyEuk8j8imI",   new Situation[]{s비오는날,s카페},                 new Concept[]{c새로운,c추억});
        tag("Sorry",               "Justin Bieber",     "fRh_vgS2dFE",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("What Do You Mean",    "Justin Bieber",     "DK_0jXPuIr0",   new Situation[]{s카페,s공부},                     new Concept[]{c잔잔,c새로운});

        // ── Ed Sheeran 추가 ───────────────────────────────────────────────────
        tag("Castle on the Hill",  "Ed Sheeran",        "K0ibBPhiaG0",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Bad Habits",          "Ed Sheeran",        "orJSJGHjBLI",   new Situation[]{s출근,s드라이브,s청소},           new Concept[]{c신나게,c새로운});

        // ── Coldplay 추가 ─────────────────────────────────────────────────────
        tag("A Sky Full of Stars",  "Coldplay",         "mjB3GWqN4LY",   new Situation[]{s드라이브,s잠들기},               new Concept[]{c신나게,c잔잔});
        tag("Clocks",               "Coldplay",         "d020hcWA_Ww",   new Situation[]{s공부,s비오는날},                 new Concept[]{c잔잔,c추억});
        tag("Paradise",             "Coldplay",         "1G4isv_Fylg",   new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c새로운});

        // ── Adele 추가 ────────────────────────────────────────────────────────
        tag("Set Fire to the Rain", "Adele",            "4tIPcgeFzXY",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Skyfall",             "Adele",             "DeumyOPL_o0",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Calvin Harris ─────────────────────────────────────────────────────
        tag("Summer",              "Calvin Harris",     "ebXbLfLACGM",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("Feel So Close",       "Calvin Harris",     "DLv9HMiDdXY",   new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c새로운});
        tag("This Is What You Came For","Calvin Harris","kOkQ4T5WO9E",   new Situation[]{s드라이브,s공부},                 new Concept[]{c잔잔,c새로운});

        // ── Kygo ──────────────────────────────────────────────────────────────
        tag("Firestone",           "Kygo",              "oT3mCytat2k",   new Situation[]{s드라이브,s공부},                 new Concept[]{c잔잔,c위로});
        tag("Here for You",        "Kygo",              "E_4nAKJWOzQ",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});
        tag("Stole the Show",      "Kygo",              "AiF7G7dNRuI",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});

        // ── Alan Walker ───────────────────────────────────────────────────────
        tag("Faded",               "Alan Walker",       "60ItHLz5WEA",   new Situation[]{s드라이브,s잠들기,s공부},         new Concept[]{c잔잔,c위로});
        tag("Alone",               "Alan Walker",       "K4DyBUG242c",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});
        tag("Darkside",            "Alan Walker",       "6sfMBHNHuH0",   new Situation[]{s운동,s공부},                     new Concept[]{c신나게,c잔잔});

        // ── The Chainsmokers ──────────────────────────────────────────────────
        tag("Don't Let Me Down",   "The Chainsmokers",  "Io0fBr1XBUA",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("Something Just Like This","The Chainsmokers","FM7MFYoylVs", new Situation[]{s드라이브,s잠들기},               new Concept[]{c잔잔,c위로});

        // ── Imagine Dragons 추가 ──────────────────────────────────────────────
        tag("Thunder",             "Imagine Dragons",   "fKopy74weus",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Whatever It Takes",   "Imagine Dragons",   "oc3HCzDDByE",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Enemy",               "Imagine Dragons",   "D9G1VOjN_84",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── OneRepublic ───────────────────────────────────────────────────────
        tag("Counting Stars",      "OneRepublic",       "hT_nvWreIhg",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Apologize",           "OneRepublic",       "ZSM3w1v-A_Y",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Love Runs Out",       "OneRepublic",       "S3fTw7iFRCU",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── Bon Jovi ──────────────────────────────────────────────────────────
        tag("Livin' on a Prayer",  "Bon Jovi",          "lDK9QqIzhwk",   new Situation[]{s운동,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("It's My Life",        "Bon Jovi",          "vx2u5uUszsCw",  new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});

        // ── Guns N' Roses ─────────────────────────────────────────────────────
        tag("Sweet Child O' Mine",  "Guns N' Roses",    "1w7OgIMMRc4",   new Situation[]{s운동,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("November Rain",        "Guns N' Roses",    "8SbUC-UaAxE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Linkin Park 추가 ──────────────────────────────────────────────────
        tag("What I've Done",      "Linkin Park",       "8sgycukfqeM",   new Situation[]{s비오는날,s운동},                 new Concept[]{c슬프게,c신나게});
        tag("Crawling",            "Linkin Park",       "Gd9OhYroLN0",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Faint",               "Linkin Park",       "LYU-8IFcDPw",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── Eminem 추가 ───────────────────────────────────────────────────────
        tag("Love the Way You Lie","Eminem",            "uelHwf8o7_U",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Till I Collapse",     "Eminem",            "eKitHIFbX7Pw",  new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── Drake 추가 ────────────────────────────────────────────────────────
        tag("One Dance",           "Drake",             "qL8KH_ABKOA",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Hotline Bling",       "Drake",             "uxpDa-c-4Mc",   new Situation[]{s비오는날,s카페},                 new Concept[]{c추억,c새로운});
        tag("In My Feelings",      "Drake",             "DRS_PpOrUZ4",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});

        // ── Khalid ────────────────────────────────────────────────────────────
        tag("Young Dumb & Broke",  "Khalid",            "pBkHHoOIIn8",   new Situation[]{s출근,s카페},                     new Concept[]{c새로운,c추억});
        tag("Talk",                "Khalid",            "HVpifGRHkC0",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c위로});

        // ── SZA ───────────────────────────────────────────────────────────────
        tag("Kill Bill",           "SZA",               "3PpwZKTNKgY",   new Situation[]{s비오는날,s카페},                 new Concept[]{c새로운,c슬프게});
        tag("Good Days",           "SZA",               "ClH6a0tI2V0",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c위로});

        // ── Lewis Capaldi ─────────────────────────────────────────────────────
        tag("Someone You Loved",   "Lewis Capaldi",     "zABZyZHCev4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Before You Go",       "Lewis Capaldi",     "CYZiPFjkRrc",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Sam Smith 추가 ────────────────────────────────────────────────────
        tag("Writing's On The Wall","Sam Smith",        "X4n28YDHJro",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── U2 ────────────────────────────────────────────────────────────────
        tag("With or Without You", "U2",                "_1GS4PQBKF4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("One",                 "U2",                "ftjEcrrf7r0",   new Situation[]{s잠들기,s카페,s공부},             new Concept[]{c잔잔,c위로});

        // ── 기타 ──────────────────────────────────────────────────────────────
        tag("The Night We Met",    "Lord Huron",        "KtlgYxa6BMU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Waves",               "Mr. Probz",         "0B2KQtFaKGE",   new Situation[]{s드라이브,s잠들기,s공부},         new Concept[]{c잔잔,c새로운});
        tag("Sunflower",           "Rex Orange County", "wf7bj5bSLcI",   new Situation[]{s비오는날,s카페,s공부},           new Concept[]{c잔잔,c위로});
        tag("Retrograde",          "James Blake",       "oYGm8GCxRLc",   new Situation[]{s비오는날,s잠들기,s공부},         new Concept[]{c잔잔,c슬프게});

        // ── 추가 46곡 ─────────────────────────────────────────────────────────
        tag("Hate That I Love You",  "Rihanna",           "Ki2iBrNpFk8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("We Found Love",         "Rihanna",           "tg00YEETFzg",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("Stay",                  "Rihanna",           "k6GEMjGLAZE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Umbrella",              "Rihanna",           "CvBfHwUxHIY",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c신나게,c추억});
        tag("Crazy in Love",         "Beyoncé",           "ViwtNLUqkMY",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Halo",                  "Beyoncé",           "D4IvG1LOFKQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("Irreplaceable",         "Beyoncé",           "2EwViQxSe24",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c새로운});
        tag("Rolling in the Deep",   "Adele",             "rYEDA3JcQqw",   new Situation[]{s비오는날,s운동},                 new Concept[]{c슬프게,c신나게});
        tag("Hello",                 "Adele",             "YQHsXMglC9A",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Easy On Me",            "Adele",             "U3ASj1L6_sY",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Shallow",               "Lady Gaga",         "bo_efYSyWq4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Bad Romance",           "Lady Gaga",         "qrO4YZeyl0I",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Poker Face",            "Lady Gaga",         "bESGLojNYSo",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게});
        tag("Telephone",             "Lady Gaga",         "EVBsypHKBFw",   new Situation[]{s청소,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("Blister in the Sun",    "Violent Femmes",    "kZcMBz_Zc-Y",   new Situation[]{s청소,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Mr. Brightside",        "The Killers",       "gGdGFtwCNBE",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c신나게,c추억});
        tag("Somebody That I Used to Know","Gotye",       "MpBWDMIqXH8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Take Me to Church",     "Hozier",            "MYSVMgRr6pw",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c위로});
        tag("Work Song",             "Hozier",            "j-RvVPRKBAs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("Cherry Wine",           "Hozier",            "4pr9zajFTPY",   new Situation[]{s비오는날,s카페,s잠들기},         new Concept[]{c위로,c잔잔});
        tag("Skinny Love",           "Bon Iver",          "sGmGMSo-5o4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c잔잔});
        tag("Holocene",              "Bon Iver",          "TWcyIpul8OE",   new Situation[]{s비오는날,s공부,s잠들기},         new Concept[]{c잔잔,c위로});
        tag("Liability",             "Lorde",             "TRBOaEaXxuQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Royals",                "Lorde",             "nlcIKh6oBek",   new Situation[]{s카페,s드라이브},                 new Concept[]{c새로운,c잔잔});
        tag("Green Light",           "Lorde",             "TIItX3C-3sY",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("Creep",                 "Radiohead",         "XFkzRNyygfk",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Karma Police",          "Radiohead",         "1uYWYWPc9HU",   new Situation[]{s비오는날,s공부},                 new Concept[]{c잔잔,c슬프게});
        tag("Human",                 "The Killers",       "RIZdjT9SCNU",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c신나게,c추억});
        tag("Smells Like Teen Spirit","Nirvana",          "hTWKbfoikeg",   new Situation[]{s운동,s청소},                     new Concept[]{c신나게,c추억});
        tag("Come As You Are",       "Nirvana",           "vabnZ9-ex7o",   new Situation[]{s비오는날,s공부},                 new Concept[]{c잔잔,c추억});
        tag("Seven Nation Army",     "The White Stripes", "0J2QdDbelmY",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});
        tag("Under the Bridge",      "Red Hot Chili Peppers","TMmPS-HqyvI",new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("Californication",       "Red Hot Chili Peppers","YlUKcNNmywk",new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c추억});
        tag("Stressed Out",          "Twenty One Pilots", "pXRviuL6vMY",   new Situation[]{s비오는날,s공부},                 new Concept[]{c슬프게,c위로});
        tag("Ride",                  "Twenty One Pilots", "Pw-oABCRkMY",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c잔잔,c위로});
        tag("Heathens",              "Twenty One Pilots", "sMOf7FtnZo0",   new Situation[]{s비오는날,s공부},                 new Concept[]{c잔잔,c슬프게});
        tag("Intro",                 "The xx",            "MV_3Dpw-BRY",   new Situation[]{s잠들기,s공부,s비오는날},         new Concept[]{c잔잔,c위로});
        tag("On Hold",               "The xx",            "blWgABJQPNo",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c추억});
        tag("Crystalised",           "The xx",            "oBSEk2G7Pso",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c잔잔,c슬프게});
        tag("Electric Feel",         "MGMT",              "MmZexg8sxyk",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});
        tag("Kids",                  "MGMT",              "WEQnzs8wl6E",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c추억});
        tag("Midnight City",         "M83",               "dX3k_QDnzHE",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c신나게,c잔잔});
        tag("Oblivion",              "Grimes",            "B8RqBCOZbdw",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c슬프게});
        tag("Pursuit of Happiness",  "Kid Cudi",          "lxiAoINbTl4",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c신나게,c추억});
        tag("Day 'N' Nite",          "Kid Cudi",          "1_7SfZ0v5hY",   new Situation[]{s잠들기,s비오는날},               new Concept[]{c잔잔,c위로});
        tag("Summertime Sadness",    "Lana Del Rey",      "TdrL3QxjyVw",   new Situation[]{s비오는날,s드라이브,s잠들기},     new Concept[]{c슬프게,c추억});
        tag("Video Games",           "Lana Del Rey",      "3kkM8Jt6_M0",   new Situation[]{s잠들기,s카페},                   new Concept[]{c잔잔,c추억});

        // ══════════════════════════════════════════════════════════════════════
        // ── J-POP ─────────────────────────────────────────────────────────────
        // ══════════════════════════════════════════════════════════════════════

        // ── 米津玄師 Kenshi Yonezu ─────────────────────────────────────────────
        tag("Lemon",               "米津玄師",           "SX_ViX4G9uU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("KICK BACK",           "米津玄師",           "ChoGFGCFnlw",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Pale Blue",           "米津玄師",           "uo7FnLJtJD8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Flamingo",            "米津玄師",           "K2MMUBSeMpU",   new Situation[]{s카페,s드라이브},                 new Concept[]{c새로운,c신나게});
        tag("Paprika",             "米津玄師",           "PCNd3BQFSE4",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("馬と鹿",               "米津玄師",           "LoHO-2EWJFU",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c신나게});
        tag("海の幽霊",             "米津玄師",           "QgakUY_tcSA",   new Situation[]{s잠들기,s비오는날},               new Concept[]{c잔잔,c위로});
        tag("感電",                 "米津玄師",           "ZIFsvM-3JCY",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── YOASOBI ───────────────────────────────────────────────────────────
        tag("夜に駆ける",           "YOASOBI",           "y4WZGrJmJt0",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c신나게,c슬프게});
        tag("アイドル",             "YOASOBI",           "zkzJdpFe7kA",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("群青",                 "YOASOBI",           "x8g7dCCBjpA",   new Situation[]{s비오는날,s공부},                 new Concept[]{c슬프게,c위로});
        tag("怪物",                 "YOASOBI",           "YSm0pOA4WTw",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("ハルジオン",           "YOASOBI",           "mN4E62D0GhA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("たぶん",               "YOASOBI",           "5FiJMzF2gbs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("もしも命が描けたら",    "YOASOBI",           "XcNlIiMdx-c",   new Situation[]{s잠들기,s공부},                   new Concept[]{c잔잔,c위로});

        // ── Official HIGE DANdism ─────────────────────────────────────────────
        tag("Pretender",           "Official HIGE DANdism","TQ8WFbyDp5I",new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("I LOVE...",           "Official HIGE DANdism","MJbJVDCQNhU",new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Universe",            "Official HIGE DANdism","baRpQyEJ70A",new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("Cry Baby",            "Official HIGE DANdism","BolNFuAE16o",new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Subtitle",            "Official HIGE DANdism","WF8KbPUHAqA",new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("ホワイトノイズ",       "Official HIGE DANdism","D3yuhhLNiJY",new Situation[]{s공부,s카페},                     new Concept[]{c잔잔,c새로운});
        tag("Traveler",            "Official HIGE DANdism","GZB0pDQhiB0",new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c새로운});

        // ── King Gnu ──────────────────────────────────────────────────────────
        tag("白日",                 "King Gnu",           "IG6JfK02GXA",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("一途",                 "King Gnu",           "9vWFP3RY_wQ",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("逆夢",                 "King Gnu",           "GHhDO-Hplwc",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Teenager Forever",    "King Gnu",           "PKhbztvIppU",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("三文小説",             "King Gnu",           "wYKRiGXy-4g",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});

        // ── Radwimps ──────────────────────────────────────────────────────────
        tag("前前前世",             "RADWIMPS",           "PDSkFeMVNFs",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
        tag("スパークル",           "RADWIMPS",           "RRW6jCEfVSc",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("なんでもないや",       "RADWIMPS",           "ZVcCEgBrjGU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("愛にできることはまだあるかい","RADWIMPS",    "o9f8_Fq4MpA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Ado ───────────────────────────────────────────────────────────────
        tag("うっせぇわ",           "Ado",                "Aa9U8HMgTak",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("新時代",               "Ado",                "h6vlgDjY8Ks",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("踊",                   "Ado",                "gtr-RuT6OiE",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("私は最強",             "Ado",                "hj1M-pLRhlI",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── Aimer ─────────────────────────────────────────────────────────────
        tag("カタオモイ",           "Aimer",              "DWRdtBqtDYw",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("眩いばかり",           "Aimer",              "5gYHGUWNZ0M",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("花びらたちのマーチ",   "Aimer",              "ZxTwOJZ4_1s",   new Situation[]{s비오는날,s카페},                 new Concept[]{c잔잔,c위로});
        tag("残響散歌",             "Aimer",              "BDZaVJLTkEk",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── LiSA ──────────────────────────────────────────────────────────────
        tag("紅蓮華",               "LiSA",               "CwkzK-F0Y1g",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("炎",                   "LiSA",               "NQOL6IUkUOQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Crossing Field",      "LiSA",               "d7pbdFSXAQA",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── Fujii Kaze ────────────────────────────────────────────────────────
        tag("何なんw",              "Fujii Kaze",         "GSVBb0F9HH0",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("死ぬのがいいわ",        "Fujii Kaze",         "ItBN8DKDrUI",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("まつり",               "Fujii Kaze",         "7wWGcM43r2c",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("きらり",               "Fujii Kaze",         "lqRCFuWNPz8",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c잔잔});

        // ── Aimyon ────────────────────────────────────────────────────────────
        tag("マリーゴールド",       "Aimyon",             "YqIGBbLqJgU",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c추억});
        tag("愛を伝えたいだとか",   "Aimyon",             "eGNDLnIumTw",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("ハルノヒ",             "Aimyon",             "dD6FBYnH1m0",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── One OK Rock ───────────────────────────────────────────────────────
        tag("Wherever You Are",    "ONE OK ROCK",        "akNOBukXwwE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("The Beginning",       "ONE OK ROCK",        "QrXLBHHsHO4",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Mighty Long Fall",    "ONE OK ROCK",        "Wfv-NX6-PFU",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("We Are",              "ONE OK ROCK",        "RbkBQn_dQFo",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Taking Off",          "ONE OK ROCK",        "aB-n5WvNWP0",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── Eve ───────────────────────────────────────────────────────────────
        tag("廻廻奇譚",             "Eve",                "w7CiqIc4Z9Y",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("蒼のワルツ",           "Eve",                "EH1x8JXBvHI",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c잔잔,c위로});
        tag("お気に召すまま",       "Eve",                "wS6Z0xWV1vA",   new Situation[]{s카페,s공부},                     new Concept[]{c잔잔,c새로운});

        // ── Yorushika ─────────────────────────────────────────────────────────
        tag("だから僕は音楽を辞めた","Yorushika",         "sHKSGitxEHs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("花に亡霊",             "Yorushika",          "lRNs5Nl-jHs",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c잔잔});
        tag("春泥棒",               "Yorushika",          "pJCxHRx8E3w",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});

        // ── Hikaru Utada ──────────────────────────────────────────────────────
        tag("First Love",          "宇多田ヒカル",        "7XHZsLtnlEA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Automatic",           "宇多田ヒカル",        "mFBBCDFwU0Q",   new Situation[]{s카페,s비오는날},                 new Concept[]{c추억,c잔잔});
        tag("道",                   "宇多田ヒカル",        "5Vif3ZEVVIM",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("Flavor Of Life",      "宇多田ヒカル",        "g1Hb-4nLoPs",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});

        // ── SEKAI NO OWARI ────────────────────────────────────────────────────
        tag("RPG",                 "SEKAI NO OWARI",     "lHEkxzwqRLA",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c새로운});
        tag("Dragon Night",        "SEKAI NO OWARI",     "MBHHQNJLHx4",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("Habit",               "SEKAI NO OWARI",     "bMtGtBKIgls",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});

        // ── Back Number ───────────────────────────────────────────────────────
        tag("クリスマスソング",     "back number",        "QXNZX4FBmag",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("高嶺の花子さん",       "back number",        "EkMGIhJV4BE",   new Situation[]{s카페,s드라이브},                 new Concept[]{c추억,c신나게});
        tag("水平線",               "back number",        "Cn4uo_S4L5E",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Creepy Nuts ───────────────────────────────────────────────────────
        tag("Bling-Bang-Bang-Born","Creepy Nuts",        "GH3UoOdKSMg",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("のびしろ",             "Creepy Nuts",        "xT4mNLRbxts",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── Perfume ───────────────────────────────────────────────────────────
        tag("チョコレイト・ディスコ","Perfume",           "UmxHk-IQPQQ",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});
        tag("STAR TRAIN",          "Perfume",            "n-B9EM05xaU",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("ポリリズム",           "Perfume",            "5_-TbnK9IeA",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});

        // ── Mr. Children ──────────────────────────────────────────────────────
        tag("Tomorrow never knows","Mr.Children",        "sSF8xGbB0s0",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("HERO",                "Mr.Children",        "Ai6sFx0gTL4",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c위로,c잔잔});
        tag("しるし",               "Mr.Children",        "VlHn3JMywJE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── B'z ───────────────────────────────────────────────────────────────
        tag("ultra soul",          "B'z",                "xDi-SalLGr0",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});
        tag("愛のままにわがままに 僕は君だけを傷つけない","B'z","BvuTBOG-Xg4",new Situation[]{s비오는날,s잠들기},             new Concept[]{c슬프게,c추억});

        // ── Bump of Chicken ───────────────────────────────────────────────────
        tag("天体観測",             "BUMP OF CHICKEN",    "d7lCd7LFVHE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("ray",                 "BUMP OF CHICKEN",    "F3mSHMrJO9g",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c위로,c잔잔});
        tag("車輪の唄",             "BUMP OF CHICKEN",    "f_k4K2i2xyc",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c슬프게,c추억});

        // ── Spitz ─────────────────────────────────────────────────────────────
        tag("チェリー",             "スピッツ",            "EY7QwCMBY9w",   new Situation[]{s비오는날,s카페},                 new Concept[]{c추억,c잔잔});
        tag("ロビンソン",           "スピッツ",            "LF64bPFVIv0",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c추억,c잔잔});

        // ── 藤井風 other songs ───────────────────────────────────────────────
        tag("旅路",                 "Fujii Kaze",         "cVEjWmvW_Uk",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c잔잔,c위로});

        // ══════════════════════════════════════════════════════════════════════
        // ── K-POP / 국내 추가 ─────────────────────────────────────────────────
        // ══════════════════════════════════════════════════════════════════════

        // ── EXO ───────────────────────────────────────────────────────────────
        tag("Power",               "EXO",                "L9KHicgQFnQ",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Ko Ko Bop",           "EXO",                "FHcHCN66t1c",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});
        tag("Love Shot",           "EXO",                "pSudEWBAYRE",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Growl",               "EXO",                "iwd8N6_q9Tw",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Monster",             "EXO",                "KSH-FVVtTf0",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});

        // ── GOT7 ──────────────────────────────────────────────────────────────
        tag("Hard Carry",          "GOT7",               "En2GvRLI2yE",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("If You Do",           "GOT7",               "9Co_AcCr9tQ",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("Just Right",          "GOT7",               "_hCJ81bZiXA",   new Situation[]{s출근,s카페},                     new Concept[]{c신나게,c새로운});
        tag("Never Ever",          "GOT7",               "l3UUscrXNr8",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c새로운});

        // ── DAY6 ──────────────────────────────────────────────────────────────
        tag("한 페이지가 될 수 있게","DAY6",              "sR2cCBH5Hxs",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c추억});
        tag("예뻤어",               "DAY6",               "rLC7O7YJ3C8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("놓아 놓아 놓아",        "DAY6",               "0k5V4uCzqLo",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c위로});
        tag("Sweet Chaos",         "DAY6",               "jzP-SnW-DFk",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── ATEEZ ─────────────────────────────────────────────────────────────
        tag("FIREWORKS",           "ATEEZ",              "HjCcEkMkl1s",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Inception",           "ATEEZ",              "dQ5VSNiRy9Y",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("ANSWER",              "ATEEZ",              "rjvqHFbVJwc",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Deja Vu",             "ATEEZ",              "Y8JkJ7bFBkk",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});

        // ── MONSTA X ──────────────────────────────────────────────────────────
        tag("DRAMARAMA",           "MONSTA X",           "sP3iBd5MXPY",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Follow",              "MONSTA X",           "UOp6h4-iipc",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("Rush Hour",           "Crush",              "FDFxXpXFqBo",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});

        // ── 에픽하이 Epik High ────────────────────────────────────────────────
        tag("Born Hater",          "에픽하이",            "bFkzECdcNLc",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게});
        tag("Fly",                 "에픽하이",            "xwYfhqN35cU",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c위로,c추억});
        tag("우산",                 "에픽하이",            "T1JHWhiSHVc",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("HAPPEN ENDING",       "에픽하이",            "obrX4nVHD08",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("연애소설",             "에픽하이",            "Q5FkMUlNT5s",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});

        // ── 10cm ──────────────────────────────────────────────────────────────
        tag("사랑은 은하수 다방에서","10cm",              "vbPAPMnxFxs",   new Situation[]{s카페,s비오는날},                 new Concept[]{c잔잔,c추억});
        tag("아메리카노",           "10cm",               "iGKXKb_2TRw",   new Situation[]{s카페,s비오는날},                 new Concept[]{c잔잔,c추억});
        tag("어디선가 무언가",      "10cm",               "O-YJ8VW9GIA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("봄이 좋냐",            "10cm",               "qLQ8I9v3j3o",   new Situation[]{s비오는날,s카페},                 new Concept[]{c잔잔,c추억});

        // ── 장범준 Jang Beom June ─────────────────────────────────────────────
        tag("벚꽃 엔딩",            "버스커 버스커",        "lGxNFCCWNFk",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c추억,c잔잔});
        tag("홍대 앞에서",          "버스커 버스커",        "7yhtdZJz3OM",   new Situation[]{s비오는날,s카페},                 new Concept[]{c추억,c잔잔});

        // ── 이적 ──────────────────────────────────────────────────────────────
        tag("하늘을 달리다",        "이적",               "Xx2yDqkFMqU",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c위로});
        tag("거짓말거짓말거짓말",   "이적",               "S8Gub0xRpNg",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── 성시경 Sung Si Kyung ─────────────────────────────────────────────
        tag("거리에서",             "성시경",              "l5RJt5CMZiA",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("넌 감동이었어",        "성시경",              "JFdpBl43LOI",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── 박효신 Park Hyo Shin ─────────────────────────────────────────────
        tag("야생화",               "박효신",              "eo5BBnwLkFY",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("숨",                   "박효신",              "7HxVg-7IhTM",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── 윤하 Younha ───────────────────────────────────────────────────────
        tag("오르트구름",           "윤하",               "z8KKrJ6Wqbk",   new Situation[]{s비오는날,s공부},                 new Concept[]{c위로,c잔잔});
        tag("우산",                 "윤하",               "w5SFGiGQRgY",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── 이하이 Lee Hi ─────────────────────────────────────────────────────
        tag("한숨",                 "이하이",              "j-4KFxXGJgc",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("My Star",             "이하이",              "oUL24ks4rJM",   new Situation[]{s카페,s비오는날},                 new Concept[]{c위로,c잔잔});

        // ── 태연 TaeYeon ──────────────────────────────────────────────────────
        tag("I",                   "태연",               "xvqeSP81UTs",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c새로운});
        tag("Fine",                "태연",               "0kYmEIXkFhI",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("四季 (Four Seasons)", "태연",               "XxgC9SEYUQA",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c잔잔});

        // ── 폴킴 Paul Kim ─────────────────────────────────────────────────────
        tag("초록빛",               "폴킴",               "BHzDlgsTy4Y",   new Situation[]{s비오는날,s카페},                 new Concept[]{c잔잔,c위로});
        tag("웃음",                 "폴킴",               "A9JwtCULBbQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});

        // ── 임창정 Lim Chang Jung ─────────────────────────────────────────────
        tag("소주 한 잔",           "임창정",              "nUHUCE1XOYM",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("그때 또 다시",         "임창정",              "0ER3uyJOaHQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── 로꼬 Loco + 기타 국내 힙합 ───────────────────────────────────────
        tag("오늘 취하면",          "로꼬",               "e_a8XjW4cKQ",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("BERMUDA TRIANGLE",    "Zico",               "3oVt4nHNyBw",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("쌍꺼풀",               "Zico",               "BLlRMfOsaOo",   new Situation[]{s카페,s비오는날},                 new Concept[]{c슬프게,c추억});
        tag("Thinking",            "pH-1",               "CfC9UOF7k64",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("스물셋",               "아이유",              "aOI8lWbe5VA",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("나의 옛날 이야기",     "아이유",              "1VQ4vhL0o7o",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("이름에게",             "아이유",              "AYpn0GIoBv8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});

        // ── INFINITE ──────────────────────────────────────────────────────────
        tag("The Chaser",          "INFINITE",           "1YrxBpTm7Lc",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});
        tag("Back",                "INFINITE",           "HZm43kTCLGE",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});

        // ── 하이라이트 Highlight (구 B2ST/BEAST) ─────────────────────────────
        tag("Fiction",             "비스트",              "F9b4xCXn5L8",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("좋아",                 "하이라이트",          "JmSJUaNLFm8",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── 소녀시대 SNSD ─────────────────────────────────────────────────────
        tag("Gee",                 "소녀시대",            "U3IHb5pErtQ",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});
        tag("Tell Me Your Wish",   "소녀시대",            "fdvpEJmYmkc",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});
        tag("I Got a Boy",         "소녀시대",            "6okuSEiQMF4",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── 2PM ───────────────────────────────────────────────────────────────
        tag("Again & Again",       "2PM",                "DcMTHPHfhK8",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});
        tag("Heartbeat",           "2PM",                "JhMIfLGxrCs",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});

        // ── 신화 Shinhwa ──────────────────────────────────────────────────────
        tag("전사의 후예",          "신화",               "U-7vGXFGi5M",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});

        // ══════════════════════════════════════════════════════════════════════
        // ── 서양 팝 추가 ──────────────────────────────────────────────────────
        // ══════════════════════════════════════════════════════════════════════

        // ── Olivia Rodrigo 추가 ───────────────────────────────────────────────
        tag("brutal",              "Olivia Rodrigo",     "RHn1quJxBCc",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("traitor",             "Olivia Rodrigo",     "0bttTDMdj3U",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("deja vu",             "Olivia Rodrigo",     "cii6rumLOiM",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});

        // ── Harry Styles 추가 ─────────────────────────────────────────────────
        tag("Adore You",           "Harry Styles",       "VF-r5TtlT9w",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Golden",              "Harry Styles",       "P3cffdsEXXw",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c새로운});
        tag("Matilda",             "Harry Styles",       "H5v3kku2yFQ",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c잔잔});

        // ── Doja Cat ──────────────────────────────────────────────────────────
        tag("Say So",              "Doja Cat",           "pok-4JlUAYU",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Kiss Me More",        "Doja Cat",           "0EVVKs6NsBo",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Need to Know",        "Doja Cat",           "Wck-1k8oGGI",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Planet Her",          "Doja Cat",           "wTMWnBfSnhI",   new Situation[]{s청소,s카페},                     new Concept[]{c신나게,c새로운});

        // ── The 1975 ──────────────────────────────────────────────────────────
        tag("Chocolate",           "The 1975",           "cN1hbCJyBLg",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("The Sound",           "The 1975",           "pK7rQLqIKPM",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c새로운});
        tag("Somebody Else",       "The 1975",           "6YQEAhVIpCc",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c새로운});
        tag("If You're Too Shy",   "The 1975",           "S1RJAiMvPJw",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── Arctic Monkeys ────────────────────────────────────────────────────
        tag("Do I Wanna Know?",    "Arctic Monkeys",     "bpOSxM0rNPM",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c새로운,c잔잔});
        tag("R U Mine?",           "Arctic Monkeys",     "lKnAMbKGbyI",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Why'd You Only Call Me When You're High?","Arctic Monkeys","tAMWDtJXVF0",new Situation[]{s비오는날,s카페},     new Concept[]{c새로운,c잔잔});
        tag("Fluorescent Adolescent","Arctic Monkeys",   "IHgFJEJgUrg",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
        tag("I Bet You Look Good on the Dancefloor","Arctic Monkeys","pO2y7sGZ3TY",new Situation[]{s청소,s운동},           new Concept[]{c신나게,c추억});

        // ── Tame Impala ───────────────────────────────────────────────────────
        tag("The Less I Know The Better","Tame Impala",  "2SUwOgTvvXY",   new Situation[]{s드라이브,s카페},                 new Concept[]{c새로운,c잔잔});
        tag("Feels Like We Only Go Backwards","Tame Impala","F1UC2n0bNnU", new Situation[]{s비오는날,s공부},               new Concept[]{c잔잔,c추억});
        tag("Let It Happen",       "Tame Impala",        "pFptt7Cargc",   new Situation[]{s드라이브,s공부},                 new Concept[]{c잔잔,c새로운});
        tag("New Person, Same Old Mistakes","Tame Impala","eBShN0gGLzk",  new Situation[]{s카페,s공부},                     new Concept[]{c잔잔,c추억});

        // ── Daniel Caesar ─────────────────────────────────────────────────────
        tag("Best Part",           "Daniel Caesar",      "i2ajT-TU4DM",   new Situation[]{s카페,s잠들기},                   new Concept[]{c잔잔,c위로});
        tag("Get You",             "Daniel Caesar",      "rFlLHO01VBs",   new Situation[]{s카페,s잠들기},                   new Concept[]{c잔잔,c위로});

        // ── Khalid 추가 ───────────────────────────────────────────────────────
        tag("Better",              "Khalid",             "XMMA_j-qLmI",   new Situation[]{s카페,s비오는날},                 new Concept[]{c위로,c잔잔});
        tag("OTW",                 "Khalid",             "_sSWPvNHGKg",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});

        // ── Childish Gambino ──────────────────────────────────────────────────
        tag("Redbone",             "Childish Gambino",   "Kp7eSUU9oy8",   new Situation[]{s카페,s잠들기},                   new Concept[]{c잔잔,c추억});
        tag("This Is America",     "Childish Gambino",   "VYOjWnS4cMY",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("3005",                "Childish Gambino",   "qTG7EZJUfh4",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c잔잔,c추억});

        // ── Tyler the Creator ─────────────────────────────────────────────────
        tag("See You Again",       "Tyler the Creator",  "k2qgadSvNyU",   new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c위로});
        tag("Earfquake",           "Tyler the Creator",  "_QR7l4K3T7Y",   new Situation[]{s카페,s드라이브},                 new Concept[]{c잔잔,c새로운});
        tag("WUSYANAME",           "Tyler the Creator",  "Jbs_9kFAnAU",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c새로운});

        // ── Anderson .Paak ────────────────────────────────────────────────────
        tag("Come Down",           "Anderson .Paak",     "Xjcns8AUPOM",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c새로운});
        tag("Tints",               "Anderson .Paak",     "1_evXZBSSC0",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c새로운});

        // ── Juice WRLD ────────────────────────────────────────────────────────
        tag("Lucid Dreams",        "Juice WRLD",         "mzB1VGEGcSU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("All Girls Are the Same","Juice WRLD",       "2wHJpomMVQs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Legends",             "Juice WRLD",         "90eGu3nO9_M",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── Lil Nas X ─────────────────────────────────────────────────────────
        tag("MONTERO",             "Lil Nas X",          "6swmTBVI83k",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("INDUSTRY BABY",       "Lil Nas X",          "UTQV7KDcPB8",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});

        // ── Katy Perry ────────────────────────────────────────────────────────
        tag("Roar",                "Katy Perry",         "CevxZvSJLk8",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c새로운});
        tag("Firework",            "Katy Perry",         "QGJuMBdaqIw",   new Situation[]{s출근,s드라이브},                 new Concept[]{c신나게,c위로});
        tag("Teenage Dream",       "Katy Perry",         "98WtmW-lqdQ",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c추억});
        tag("Dark Horse",          "Katy Perry",         "0KSOMA3QBU0",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});

        // ── Nicki Minaj ───────────────────────────────────────────────────────
        tag("Super Bass",          "Nicki Minaj",        "4JipHEz53sU",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Starships",           "Nicki Minaj",        "oRdxUFDoQe0",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});

        // ── Cardi B ───────────────────────────────────────────────────────────
        tag("WAP",                 "Cardi B",            "hsm4poTWjMs",   new Situation[]{s청소,s운동},                     new Concept[]{c신나게,c새로운});
        tag("I Like It",           "Cardi B",            "53hAKHCLaDY",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c새로운});

        // ── Alicia Keys ───────────────────────────────────────────────────────
        tag("If I Ain't Got You",  "Alicia Keys",        "Ju8hr_vMnF4",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});
        tag("No One",              "Alicia Keys",        "N-oBZbfhOAQ",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c잔잔});
        tag("Fallin'",             "Alicia Keys",        "Urdlqc4LAUA",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── John Mayer ────────────────────────────────────────────────────────
        tag("Slow Dancing in a Burning Room","John Mayer","GvD3NJ4DKQM",  new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Gravity",             "John Mayer",         "Iyr5N2JqIXM",   new Situation[]{s비오는날,s카페},                 new Concept[]{c잔잔,c위로});
        tag("Your Body Is a Wonderland","John Mayer",    "AkIrfzfvCIg",   new Situation[]{s카페,s잠들기},                   new Concept[]{c잔잔,c추억});

        // ── Jason Mraz ────────────────────────────────────────────────────────
        tag("I'm Yours",           "Jason Mraz",         "EkHTsc9KyA8",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c위로});
        tag("I Won't Give Up",     "Jason Mraz",         "J_ub7Etch2U",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c잔잔});

        // ── Paramore ──────────────────────────────────────────────────────────
        tag("The Only Exception",  "Paramore",           "gHMBhRBNTmU",   new Situation[]{s비오는날,s카페},                 new Concept[]{c위로,c잔잔});
        tag("Decode",              "Paramore",           "HxoXHrqKkiA",   new Situation[]{s비오는날,s공부},                 new Concept[]{c슬프게,c잔잔});
        tag("Still Into You",      "Paramore",           "TedCCIW6Eko",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c새로운});

        // ── My Chemical Romance ───────────────────────────────────────────────
        tag("Welcome to the Black Parade","My Chemical Romance","RRKJiM9Njww",new Situation[]{s드라이브,s출근},            new Concept[]{c신나게,c추억});
        tag("Famous Last Words",   "My Chemical Romance","M4tZkFlCY7s",   new Situation[]{s운동,s출근},                     new Concept[]{c신나게,c추억});

        // ── Panic! At The Disco ───────────────────────────────────────────────
        tag("I Write Sins Not Tragedies","Panic! At The Disco","Pim8O8O5FDw",new Situation[]{s드라이브,s출근},             new Concept[]{c신나게,c추억});
        tag("High Hopes",          "Panic! At The Disco","IPXIgEAGz4M",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c위로});

        // ── Green Day ─────────────────────────────────────────────────────────
        tag("Boulevard of Broken Dreams","Green Day",    "Soa3gO7tL-c",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c슬프게,c추억});
        tag("American Idiot",      "Green Day",          "Ee_uujKuJMI",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});
        tag("Wake Me Up When September Ends","Green Day","NU9JoFKlaHE",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── Fleetwood Mac ─────────────────────────────────────────────────────
        tag("Dreams",              "Fleetwood Mac",      "mrZRURcb1cM",   new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c추억});
        tag("The Chain",           "Fleetwood Mac",      "tHLTRMJlSf4",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
        tag("Go Your Own Way",     "Fleetwood Mac",      "wGTbGnVnFhA",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});

        // ── Eagles ────────────────────────────────────────────────────────────
        tag("Hotel California",    "Eagles",             "BciS5grzkjU",   new Situation[]{s드라이브,s카페},                 new Concept[]{c잔잔,c추억});
        tag("Take It Easy",        "Eagles",             "b7SBmSslQa0",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});

        // ── Elton John ────────────────────────────────────────────────────────
        tag("Tiny Dancer",         "Elton John",         "DC3QWPhDPow",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c잔잔,c추억});
        tag("Rocket Man",          "Elton John",         "DtVBCG6ThDk",   new Situation[]{s드라이브,s비오는날},             new Concept[]{c잔잔,c추억});
        tag("Your Song",           "Elton John",         "GlPlfCy1urI",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Crocodile Rock",      "Elton John",         "4sHMFHEMzIA",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c추억});

        // ── Michael Jackson ───────────────────────────────────────────────────
        tag("Billie Jean",         "Michael Jackson",    "Zi_XLOBDo_Y",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});
        tag("Beat It",             "Michael Jackson",    "oRdxUFDoQe0",   new Situation[]{s출근,s운동},                     new Concept[]{c신나게,c추억});
        tag("Thriller",            "Michael Jackson",    "sOnqjkJTMaA",   new Situation[]{s청소,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Black or White",      "Michael Jackson",    "F2AitTPI5U0",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c추억});

        // ── Whitney Houston ───────────────────────────────────────────────────
        tag("I Will Always Love You","Whitney Houston",  "3JWTaaS7LdU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});
        tag("Greatest Love of All","Whitney Houston",    "BGnE2hEfBBs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c추억});

        // ── Mariah Carey ──────────────────────────────────────────────────────
        tag("All I Want for Christmas Is You","Mariah Carey","aAkMkVFwAoo",new Situation[]{s청소,s드라이브},               new Concept[]{c신나게,c추억});
        tag("We Belong Together",  "Mariah Carey",       "g1Hb-4nLoPs",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── David Bowie ───────────────────────────────────────────────────────
        tag("Heroes",              "David Bowie",        "3Q4rmbGBHak",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
        tag("Space Oddity",        "David Bowie",        "iYYRH4apXDo",   new Situation[]{s드라이브,s공부},                 new Concept[]{c잔잔,c추억});
        tag("Let's Dance",         "David Bowie",        "N4bFqW_eu2I",   new Situation[]{s청소,s드라이브},                 new Concept[]{c신나게,c추억});

        // ── The Beatles ───────────────────────────────────────────────────────
        tag("Hey Jude",            "The Beatles",        "A_MjCqQoLLA",   new Situation[]{s비오는날,s드라이브},             new Concept[]{c위로,c추억});
        tag("Let It Be",           "The Beatles",        "_QpP5T6bRbU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c위로,c추억});
        tag("Come Together",       "The Beatles",        "45cYwDMibGo",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
        tag("Yesterday",           "The Beatles",        "NrgmdOz227c",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c추억});

        // ── Florence + The Machine ────────────────────────────────────────────
        tag("Dog Days Are Over",   "Florence + The Machine","iWOyfLBYtuU",new Situation[]{s출근,s드라이브},               new Concept[]{c신나게,c새로운});
        tag("Shake It Out",        "Florence + The Machine","WbN0nX61ovk",new Situation[]{s비오는날,s출근},               new Concept[]{c위로,c신나게});
        tag("You've Got the Love", "Florence + The Machine","qBHKIRZyFdE",new Situation[]{s출근,s비오는날},               new Concept[]{c위로,c신나게});

        // ── Vampire Weekend ───────────────────────────────────────────────────
        tag("A-Punk",              "Vampire Weekend",    "_XC2mqcFLLs",   new Situation[]{s출근,s청소},                     new Concept[]{c신나게,c새로운});
        tag("Oxford Comma",        "Vampire Weekend",    "EOtACzEXAcU",   new Situation[]{s카페,s드라이브},                 new Concept[]{c새로운,c잔잔});

        // ── Mac DeMarco ───────────────────────────────────────────────────────
        tag("Chamber of Reflection","Mac DeMarco",       "yFPECmMEDlE",   new Situation[]{s카페,s공부},                     new Concept[]{c잔잔,c추억});
        tag("Salad Days",          "Mac DeMarco",        "4Aw6TiJhDOY",   new Situation[]{s카페,s드라이브},                 new Concept[]{c잔잔,c새로운});

        // ── Hozier 추가 ───────────────────────────────────────────────────────
        tag("Movement",            "Hozier",             "kqFCMCNqMWw",   new Situation[]{s카페,s비오는날},                 new Concept[]{c잔잔,c위로});
        tag("Like Real People Do","Hozier",             "H7wClMY0XDQ",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c잔잔,c위로});

        // ── Fleet Foxes ───────────────────────────────────────────────────────
        tag("White Winter Hymnal","Fleet Foxes",         "hAMi5SLSR4Q",   new Situation[]{s비오는날,s공부},                 new Concept[]{c잔잔,c추억});
        tag("Helplessness Blues",  "Fleet Foxes",        "Y32AW-bq9oU",   new Situation[]{s비오는날,s공부},                 new Concept[]{c잔잔,c위로});

        // ── Sufjan Stevens ────────────────────────────────────────────────────
        tag("Death With Dignity",  "Sufjan Stevens",     "M2C1Zs4VYoU",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});
        tag("Mystery of Love",     "Sufjan Stevens",     "OFNGkj1HOPY",   new Situation[]{s비오는날,s잠들기},               new Concept[]{c슬프게,c위로});

        // ── Simon & Garfunkel ─────────────────────────────────────────────────
        tag("The Sound of Silence","Simon & Garfunkel",  "NAEppFUWLfc",   new Situation[]{s비오는날,s공부},                 new Concept[]{c슬프게,c추억});
        tag("Mrs. Robinson",       "Simon & Garfunkel",  "HhDNGYKlk0s",   new Situation[]{s드라이브,s카페},                 new Concept[]{c신나게,c추억});
        tag("The Boxer",           "Simon & Garfunkel",  "l3OLFWPGPZQ",   new Situation[]{s비오는날,s공부},                 new Concept[]{c슬프게,c추억});

        // ── Norah Jones ───────────────────────────────────────────────────────
        tag("Come Away with Me",   "Norah Jones",        "nGnsKIFNuH4",   new Situation[]{s카페,s잠들기},                   new Concept[]{c잔잔,c위로});
        tag("Don't Know Why",      "Norah Jones",        "tO4dxHijIGs",   new Situation[]{s카페,s비오는날},                 new Concept[]{c잔잔,c추억});

        // ── Amy Winehouse ─────────────────────────────────────────────────────
        tag("Rehab",               "Amy Winehouse",      "KUmZp8pR1uc",   new Situation[]{s카페,s드라이브},                 new Concept[]{c신나게,c추억});
        tag("Back to Black",       "Amy Winehouse",      "TJAfLE39ZZ8",   new Situation[]{s비오는날,s카페},                 new Concept[]{c슬프게,c추억});
        tag("Valerie",             "Amy Winehouse",      "5ggkMrFBz8w",   new Situation[]{s드라이브,s청소},                 new Concept[]{c신나게,c추억});

        // ── Frank Sinatra ─────────────────────────────────────────────────────
        tag("Fly Me to the Moon",  "Frank Sinatra",      "ZEcqHA7dbwM",   new Situation[]{s카페,s드라이브},                 new Concept[]{c잔잔,c추억});
        tag("New York, New York",  "Frank Sinatra",      "_pMtLaB4fOw",   new Situation[]{s드라이브,s출근},                 new Concept[]{c신나게,c추억});
    }

    private void tag(String title, String artist, String videoId,
                     Situation[] situations, Concept[] concepts) {
        Song song = songRepository.findFirstByTitleAndArtist(title, artist)
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

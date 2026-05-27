package com.melodypharmacy.repository;

import com.melodypharmacy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 저장 성공")
    void saveUser() {
        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@test.com");
        assertThat(saved.getNickname()).isEqualTo("테스터");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmail() {
        userRepository.save(User.builder()
                .email("find@test.com")
                .password("encodedPassword")
                .nickname("찾기테스터")
                .build());

        Optional<User> found = userRepository.findByEmail("find@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 빈 값 반환")
    void findByEmail_notFound() {
        Optional<User> found = userRepository.findByEmail("none@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하는 경우")
    void existsByEmail_true() {
        userRepository.save(User.builder()
                .email("exists@test.com")
                .password("encodedPassword")
                .nickname("중복테스터")
                .build());

        boolean exists = userRepository.existsByEmail("exists@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하지 않는 경우")
    void existsByEmail_false() {
        boolean exists = userRepository.existsByEmail("no@test.com");

        assertThat(exists).isFalse();
    }
}

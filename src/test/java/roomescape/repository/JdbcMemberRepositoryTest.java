package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.domain.Member;

@JdbcTest
@Import(JdbcMemberRepository.class)
class JdbcMemberRepositoryTest {

    static final Long SAMPLE_ID = 1L;
    static final String SAMPLE_LOGIN_ID = "myId";
    static final String SAMPLE_NAME = "rudevico";
    static final String SAMPLE_PASSWORD = "myPassword";

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    JdbcMemberRepository jdbcMemberRepository;

    @DisplayName("회원을 저장한다")
    @Test
    void id가_없는_회원을_전달하면_id가_부여된_회원을_리턴한다() {
        Member member = Member.withoutId("myId", "rudevico", "myPassword");
        Member saved = jdbcMemberRepository.save(member);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(member);
    }

    @DisplayName("회원을 id로 조회한다")
    @Test
    void 회원을_id로_조회한다() {
        // given
        insertSampleMember(SAMPLE_ID, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD);
        Member expectedMember = new Member(SAMPLE_ID, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD);

        // when
        Optional<Member> found = jdbcMemberRepository.findById(1L);

        // then
        assertThat(found)
                .as("회원을 조회하지 못했음")
                .isPresent().get()
                .as("조회한 회원의 정보가 기대와 다름")
                .usingRecursiveComparison()
                .isEqualTo(expectedMember);
    }

    @DisplayName("회원을 로그인 id로 조회한다")
    @Test
    void 회원을_로그인_id로_조회한다() {
        // given
        insertSampleMember(SAMPLE_ID, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD);
        Member expectedMember = new Member(SAMPLE_ID, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD);

        // when
        Optional<Member> found = jdbcMemberRepository.findByLoginId("myId");

        // then
        assertThat(found)
                .as("회원을 조회하지 못했음")
                .isPresent().get()
                .as("조회한 회원의 정보가 기대와 다름")
                .usingRecursiveComparison()
                .isEqualTo(expectedMember);
    }

    @DisplayName("회원을 id로 삭제한다")
    @Test
    void 회원을_id로_삭제한다() {
        insertSampleMember(SAMPLE_ID, SAMPLE_LOGIN_ID, SAMPLE_NAME, SAMPLE_PASSWORD);

        jdbcMemberRepository.deleteById(1L);

        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM member WHERE id = 1", Integer.class);
        assertThat(count).isZero();
    }

    private void insertSampleMember(Long id, String sampleLoginId, String sampleName, String samplePassword) {
        String sql = """
                INSERT INTO member (id, login_id, name, password)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                id, sampleLoginId, sampleName, samplePassword
        );
    }
}

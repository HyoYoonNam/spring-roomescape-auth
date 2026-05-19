package roomescape.repository;

import java.util.Optional;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Member;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member save(Member member) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");

        long generatedKey = simpleJdbcInsert
                .executeAndReturnKey(new BeanPropertySqlParameterSource(member))
                .longValue();

        return new Member(
                generatedKey,
                member.getLoginId(),
                member.getName(),
                member.getPassword()
        );
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = """
                SELECT id, login_id, name, password
                FROM member
                WHERE id = ?
                """;

        return jdbcTemplate.query(
                        sql,
                        getMemberRowMapper(),
                        id
                )
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        String sql = """
                SELECT id, login_id, name, password
                FROM member
                WHERE login_id = ?
                """;

        return jdbcTemplate.query(
                        sql,
                        getMemberRowMapper(),
                        loginId
                )
                .stream()
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM member WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static DataClassRowMapper<Member> getMemberRowMapper() {
        return new DataClassRowMapper<>(Member.class);
    }
}

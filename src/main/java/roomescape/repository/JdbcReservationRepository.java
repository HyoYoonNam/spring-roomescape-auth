package roomescape.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private static final String FIND_RESERVATION_BY_ID = """
                SELECT
                    r.id AS reservation_id,
                    m.id AS member_id, m.login_id, m.name AS member_name, m.password, m.role,
                    r.date, 
                    r.store_id,
                    t.id AS reservation_time_id,
                    t.start_at AS time_value,
                    th.id AS reservation_theme_id,
                    th.name AS reservation_theme_name,
                    th.description AS reservation_theme_description,
                    th.image_url AS reservation_theme_image_url

                FROM reservation AS r 
                INNER JOIN member AS m
                ON r.member_id = m.id

                INNER JOIN reservation_time AS t
                ON r.time_id = t.id

                INNER JOIN theme AS th
                ON r.theme_id = th.id

                WHERE r.id = ?
            """;

    private static final String FIND_ALL_RESERVATIONS = """
                SELECT r.id AS reservation_id,
                m.id AS member_id, m.login_id, m.name AS member_name, m.password, m.role,
                r.date,
                r.store_id,
                t.id AS reservation_time_id,
                t.start_at AS time_value,
                th.id AS reservation_theme_id,
                th.name AS reservation_theme_name,
                th.description AS reservation_theme_description,
                th.image_url AS reservation_theme_image_url
            
                FROM reservation AS r 
                INNER JOIN member AS m
                ON r.member_id = m.id

                INNER JOIN reservation_time AS t
                ON r.time_id = t.id 
            
                INNER JOIN theme AS th
                ON r.theme_id = th.id
            """;
    private static final String FIND_ALL_RESERVATIONS_BY_MEMBER_ID = """
                SELECT r.id AS reservation_id,
                m.id AS member_id, m.login_id, m.name AS member_name, m.password, m.role,
                r.date,
                r.store_id,
                t.id AS reservation_time_id,
                t.start_at AS time_value,
                th.id AS reservation_theme_id,
                th.name AS reservation_theme_name,
                th.description AS reservation_theme_description,
                th.image_url AS reservation_theme_image_url
            
                FROM reservation AS r
                INNER JOIN member AS m
                ON r.member_id = m.id

                INNER JOIN reservation_time AS t
                ON r.time_id = t.id
            
                INNER JOIN theme AS th
                ON r.theme_id = th.id
            
                WHERE m.id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Reservation save(Reservation reservation) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("member_id", reservation.getMember().getId())
                .addValue("date", reservation.getDate())
                .addValue("time_id", reservation.getTimeId())
                .addValue("theme_id", reservation.getThemeId())
                .addValue("store_id", reservation.getStoreId());

        long generatedKey = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

        return new Reservation(
                generatedKey,
                reservation.getMember(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme(),
                reservation.getStoreId()
        );
    }

    @Override
    public List<Reservation> findAll() {
        return jdbcTemplate.query(
                FIND_ALL_RESERVATIONS,
                getReservationRowMapper()
        );
    }

    @Override
    public List<Reservation> findAllByMemberId(Long memberId) {
        return jdbcTemplate.query(
                FIND_ALL_RESERVATIONS_BY_MEMBER_ID,
                getReservationRowMapper(),
                memberId
        );
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        List<Reservation> results = jdbcTemplate.query(
                FIND_RESERVATION_BY_ID,
                getReservationRowMapper(),
                id
        );
        return results.stream()
                .findFirst();
    }

    @Override
    public Optional<Long> findReservationIdWith(LocalDate date, Long timeId, Long themeId) {
        String sql = """
                SELECT id
                FROM reservation
                WHERE date = :date
                AND time_id = :timeId
                AND theme_id = :themeId
                """;

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("date", date)
                .addValue("timeId", timeId)
                .addValue("themeId", themeId);
        return namedParameterJdbcTemplate.query(
                        sql,
                        parameterSource,
                        (resultSet, rowNum) -> resultSet.getLong("id")
                )
                .stream()
                .findFirst();
    }

    @Override
    public int update(Reservation reservation) {
        String sql = """
                UPDATE reservation
                SET date = :date,
                    time_id = :timeId
                WHERE id = :id
                """;

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("date", reservation.getDate())
                .addValue("timeId", reservation.getTimeId())
                .addValue("id", reservation.getId());

        return namedParameterJdbcTemplate.update(
                sql,
                parameterSource
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);
    }

    @Override
    public int deleteReservationWith(
            Long memberId,
            LocalDate date,
            Long timeId,
            Long themeId
    ) {
        String sql = """
                DELETE FROM reservation AS r
                WHERE r.member_id = :memberId
                AND r.date = :date
                AND r.time_id = :timeId
                AND r.theme_id = :themeId
                """;

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("date", date)
                .addValue("timeId", timeId)
                .addValue("themeId", themeId);
        return namedParameterJdbcTemplate.update(
                sql,
                parameterSource
        );
    }

    @Override
    public boolean existsReservationByTimeId(Long timeId) {
        String sql = "SELECT count(*) FROM reservation WHERE time_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, timeId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsReservationByThemeId(Long themeId) {
        String sql = "SELECT count(*) FROM reservation WHERE theme_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, themeId);
        return count != null && count > 0;
    }

    private static RowMapper<Reservation> getReservationRowMapper() {
        return (resultSet, rowNum) -> {
            Member member = new Member(
                    resultSet.getLong("member_id"),
                    resultSet.getString("login_id"),
                    resultSet.getString("member_name"),
                    resultSet.getString("password"),
                    Member.Role.valueOf(resultSet.getString("role"))
            );

            ReservationTime time = new ReservationTime(
                    resultSet.getLong("reservation_time_id"),
                    resultSet.getObject("time_value", LocalTime.class)
            );

            Theme theme = new Theme(
                    resultSet.getLong("reservation_theme_id"),
                    resultSet.getString("reservation_theme_name"),
                    resultSet.getString("reservation_theme_description"),
                    resultSet.getString("reservation_theme_image_url")
            );
            return new Reservation(
                    resultSet.getLong("reservation_id"),
                    member,
                    resultSet.getObject("date", LocalDate.class),
                    time,
                    theme,
                    resultSet.getLong("store_id")
            );
        };
    }
}

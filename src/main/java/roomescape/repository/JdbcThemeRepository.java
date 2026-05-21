package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Theme;

@Repository
public class JdbcThemeRepository implements ThemeRepository {

    private static final String FIND_POPULAR_THEMES = """
                SELECT th.id, th.name, th.description, th.image_url, th.running_time, COUNT(re.id) AS reservation_count
                FROM theme AS th
                LEFT JOIN reservation AS re
                ON th.id = re.theme_id
                    AND re.date >= ?
                    AND re.date < ?
                GROUP BY th.id, th.name
                ORDER BY reservation_count DESC, th.name
                LIMIT 10
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Theme save(Theme theme) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("theme")
                .usingGeneratedKeyColumns("id");

        long generatedKey = insert.executeAndReturnKey(
                new BeanPropertySqlParameterSource(theme)
        ).longValue();

        return new Theme(
                generatedKey,
                theme.getName(),
                theme.getDescription(),
                theme.getImageUrl()
        );
    }

    @Override
    public List<Theme> findAll() {
        String sql = "SELECT id, name, description, image_url FROM theme";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Theme(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url")
                )
        );
    }

    @Override
    public Optional<Theme> findById(Long id) {
        String sql = "SELECT id, name, description, image_url FROM theme WHERE id=?";

        List<Theme> themes = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Theme(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url")
                ),
                id
        );
        return themes.stream()
                .findFirst();
    }

    @Override
    public List<Theme> findPopularThemes() {
        LocalDate today = LocalDate.now();
        LocalDate beforeOneWeeks = today.minusWeeks(1);

        return jdbcTemplate.query(
                FIND_POPULAR_THEMES,
                (rs, rowNum) -> new Theme(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("image_url")
                ),
                beforeOneWeeks, today
        );
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM theme WHERE id=?", id);
    }
}


